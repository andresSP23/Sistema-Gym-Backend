package com.ansicode.SistemaAdministracionGym.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(contact = @Contact(name = "Andres", email = "andres.dx23@hotmail.com", url = "https://ansicode.com"

), description = "OpenApi documentation for spring security", title = "OpenApi specification  - Ansicode", version = "1.0", license = @License(name = "License name", url = "https://ansicode.com"

), termsOfService = "Terms of service"

), servers = {
    @Server(description = "Local ENV", url = "http://localhost:8081/api/v1"),
    @Server(description = "PROD ENV (Railway)", url = "https://sistema-gym-backend-production.up.railway.app/api/v1")
}, security = {
    @SecurityRequirement(name = "bearerAuth")
}

)
@SecurityScheme(name = "bearerAuth", description = "JWT auth description", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER

)
public class OpenApiConfig {
}
