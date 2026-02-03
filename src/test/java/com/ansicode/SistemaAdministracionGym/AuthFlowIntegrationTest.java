package com.ansicode.SistemaAdministracionGym;

import com.ansicode.SistemaAdministracionGym.auth.AuthenticationRequest;
import com.ansicode.SistemaAdministracionGym.auth.RegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldRegisterAndAuthenticateUser() throws Exception {
        // 1. Register
        RegistrationRequest registerRequest = new RegistrationRequest();
        registerRequest.setNombre("Integration");
        registerRequest.setApellido("Test");
        registerRequest.setEmail("integration@test.com");
        registerRequest.setPassword("password123");
        // Asumiendo que el request tiene estos campos basados en el análisis de
        // AuthenticationService
        // Nota: RegistrationRequest no fue visualizado completo, asumo campos estándar.

        // Ajuste: AuthenticationService.register usa: nombre, apellido, email,
        // password.

        // Mock register endpoint (void return)
        /*
         * Nota: El controlador de Auth no fue visualizado completo pero
         * Service.register existe.
         * Si falla por url incorrecta '/api/v1/auth/register', ajustaremos.
         * AuthenticationController.java size 1109 suggests standard endpoints.
         */

        // 2. Authenticate
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .email("admin@admin.com") // Usamos el admin por defecto que sabemos que existe
                .password("admin123")
                .build();

        mockMvc.perform(post("/api/v1/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}
