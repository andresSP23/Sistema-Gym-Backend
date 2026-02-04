package com.ansicode.SistemaAdministracionGym.root;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import io.swagger.v3.oas.annotations.Hidden;

@RestController
@Hidden
public class RootController {

    @GetMapping("/")
    public RedirectView redirectToSwagger() {
        return new RedirectView("/api/v1/swagger-ui/index.html");
    }
}
