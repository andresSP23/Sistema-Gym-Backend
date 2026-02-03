package com.ansicode.SistemaAdministracionGym;

import com.ansicode.SistemaAdministracionGym.role.Role;
import com.ansicode.SistemaAdministracionGym.role.RoleRepository;
import com.ansicode.SistemaAdministracionGym.user.User;
import com.ansicode.SistemaAdministracionGym.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
@EnableScheduling

public class SistemaAdministracionGym {

	public static void main(String[] args) {
		SpringApplication.run(SistemaAdministracionGym.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository, UserRepository userRepository,
			PasswordEncoder passwordEncoder, org.springframework.core.env.Environment env) {
		return args -> {
			// Check active profile
			boolean isDev = java.util.Arrays.asList(env.getActiveProfiles()).contains("dev");

			if (roleRepository.findByName("ADMINISTRADOR").isEmpty()) {
				roleRepository.save(Role.builder().name("ADMINISTRADOR").build());
			}

			if (roleRepository.findByName("CAJERO").isEmpty()) {
				roleRepository.save(Role.builder().name("CAJERO").build());
			}

			if (roleRepository.findByName("ENTRENADOR").isEmpty()) {
				roleRepository.save(Role.builder().name("ENTRENADOR").build());
			}

			// ===== USER ADMIN =====
			if (isDev && userRepository.findByEmail("admin@admin.com").isEmpty()) {

				Role adminRole = roleRepository.findByName("ADMINISTRADOR")
						.orElseThrow(() -> new RuntimeException("Rol ADMINISTRADOR no existe"));

				User admin = User.builder()
						.nombre("Admin")
						.apellido("Principal")
						.telefono("0999999999")
						.email("admin@admin.com")
						.password(passwordEncoder.encode("admin123"))
						.fechaNacimiento(LocalDate.of(1990, 1, 1))
						.roles(List.of(adminRole))
						.activa(true)
						.build();

				userRepository.save(admin);

				System.out.println(" Usuario administrador creado: admin@admin.com / admin123");
			}
		};
	}
}
