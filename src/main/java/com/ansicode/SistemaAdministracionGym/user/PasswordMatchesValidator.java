package com.ansicode.SistemaAdministracionGym.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRequest> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }


    @Override
    public boolean isValid(UserRequest value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String pass = value.getPassword();
        String confirm = value.getConfirmPassword();

        // Deja que @NotBlank maneje null/blank
        if (pass == null || confirm == null) return true;

        boolean ok = pass.equals(confirm);

        if (!ok) {
            // Poner el error en el campo confirmPassword (más útil para el front)
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }

        return ok;
    }
}
