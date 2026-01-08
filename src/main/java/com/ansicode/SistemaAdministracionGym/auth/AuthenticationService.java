package com.ansicode.SistemaAdministracionGym.auth;


import com.ansicode.SistemaAdministracionGym.role.RoleRepository;
import com.ansicode.SistemaAdministracionGym.security.JwtService;
import com.ansicode.SistemaAdministracionGym.user.User;
import com.ansicode.SistemaAdministracionGym.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private  final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;



    private final AuthenticationManager authenticationManager;

//    public void register(RegistrationRequest request) throws MessagingException {
//
//        //se puede modificar el valor del rol para que por defecto agarre un rol al momento de registrar un usuario
//        var userRole = roleRepository.findByName("USER").orElseThrow(()-> new IllegalStateException("El rol no a sido inicializado"));
//
//
//        var user = User.builder()
//                .nombre(request.getNombre())
//                .apellido(request.getApellido())
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .cuentaBloqueada(false)
//                .activa(true)
//                .roles(List.of(userRole))
//                .build();
//
//        userRepository.save(user);
//
//    }




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



}
