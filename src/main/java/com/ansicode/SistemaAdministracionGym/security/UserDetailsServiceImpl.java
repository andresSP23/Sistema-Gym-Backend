package com.ansicode.SistemaAdministracionGym.security;

import com.ansicode.SistemaAdministracionGym.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl  implements UserDetailsService {


    private final UserRepository userRepository;
    @Override
    @Transactional //con esto cargamos el usuario y los roles o los permisos del usuario
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
