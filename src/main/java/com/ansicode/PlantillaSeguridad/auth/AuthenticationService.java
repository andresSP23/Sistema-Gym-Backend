package com.ansicode.PlantillaSeguridad.auth;

import com.ansicode.PlantillaSeguridad.email.EmailService;
import com.ansicode.PlantillaSeguridad.email.EmailTemplateName;
import com.ansicode.PlantillaSeguridad.role.RoleRepository;
import com.ansicode.PlantillaSeguridad.security.JwtService;
import com.ansicode.PlantillaSeguridad.user.Token;
import com.ansicode.PlantillaSeguridad.user.TokenRepository;
import com.ansicode.PlantillaSeguridad.user.User;
import com.ansicode.PlantillaSeguridad.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private  final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    private final AuthenticationManager authenticationManager;

    public void register(RegistrationRequest request) throws MessagingException {

        //se puede modificar el valor del rol para que por defecto agarre un rol al momento de registrar un usuario
        var userRole = roleRepository.findByName("USER").orElseThrow(()-> new IllegalStateException("El rol no a sido inicializado"));


        var user = User.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .cuentaBloqueada(false)
                .activa(true)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);
        //sendValidationEmail(user); si no se quiere depender de que validen el email o de un servicio de correo

    }

    private void sendValidationEmail(User user) throws MessagingException {

        var newToken = generateAndSaveActivationToken(user);
        //enviar email
        emailService.sendEmail(
                user.getEmail(),
                user.fullname(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );


        
    }

    private String generateAndSaveActivationToken(User user) {
       //generar el token
        String generatedToken = generateAndSaveActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .fechaCreacion(LocalDateTime.now())
                .fechaExpiracion(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateAndSaveActivationCode(int length) {
      String characters = "0123456789";
      StringBuilder codeBuilder = new StringBuilder();
      SecureRandom secureRandom = new SecureRandom();
      for (int i = 0; i < length; i++) {
          int randomIndex = secureRandom.nextInt(characters.length());
          codeBuilder.append(characters.charAt(randomIndex));
      }
      return codeBuilder.toString();
    }


    public AuthenticationResponse authenticate(@Valid AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String,Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullname", user.fullname());
        var jwtToken  = jwtService.generateToken(claims , user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }


    public void activateAccount(String token) throws MessagingException {

        Token savedToken = tokenRepository.findByToken(token).orElseThrow(()-> new IllegalStateException("Token invalido"));

        if(LocalDateTime.now().isAfter(savedToken.getFechaExpiracion())) {
            sendValidationEmail(savedToken.getUser());
            throw new IllegalStateException("Token expirado . Un nuevo token a sido enviado.");
        }

        var user = userRepository.findById(savedToken.getUser().getId()).orElseThrow(()-> new UsernameNotFoundException("Usuario no encontrado"));
        user.setActiva(true);
        userRepository.save(user);

        savedToken.setFechaValidacion(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}
