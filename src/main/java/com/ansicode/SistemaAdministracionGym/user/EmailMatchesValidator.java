package com.ansicode.SistemaAdministracionGym.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validador para @EmailMatches.
 * Compara email y confirmEmail en UserRequest.
 */
public class EmailMatchesValidator implements ConstraintValidator<EmailMatches, UserRequest> {

    @Override
    public void initialize(EmailMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserRequest value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        String email = value.getEmail();
        String confirmEmail = value.getConfirmEmail();

        // Deja que @NotBlank/@Email maneje null/blank
        if (email == null || confirmEmail == null)
            return true;

        boolean ok = email.equalsIgnoreCase(confirmEmail);

        if (!ok) {
            // Poner el error en el campo confirmEmail (más útil para el front)
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmEmail")
                    .addConstraintViolation();
        }

        return ok;
    }
}
