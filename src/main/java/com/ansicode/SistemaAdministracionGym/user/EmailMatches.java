package com.ansicode.SistemaAdministracionGym.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotación para validar que email y confirmEmail coinciden.
 */
@Documented
@Constraint(validatedBy = EmailMatchesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailMatches {

    String message() default "Los correos electrónicos no coinciden";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
