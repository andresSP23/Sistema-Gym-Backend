package com.ansicode.PlantillaSeguridad;

import com.ansicode.PlantillaSeguridad.role.Role;
import com.ansicode.PlantillaSeguridad.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class PlantillaSeguridadApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlantillaSeguridadApplication.class, args);
	}


	@Bean
	public CommandLineRunner unner(RoleRepository roleRepository) {
		return args -> {
			if(roleRepository.findByName("USER").isEmpty()){
				roleRepository.save(Role.builder().name("USER").build());
			}
		};
	}
}
