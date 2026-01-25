package com.ansicode.SistemaAdministracionGym.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesUpdateValidator  implements ConstraintValidator<PasswordMatchesUpdate, UserUpdateRequest> {
    @Override
    public void initialize(PasswordMatchesUpdate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }


    @Override
    public boolean isValid(UserUpdateRequest value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String pass = value.getPassword();
        String confirm = value.getConfirmPassword();

        boolean passEmpty = (pass == null || pass.isBlank());
        boolean confirmEmpty = (confirm == null || confirm.isBlank());

        // Si no están intentando cambiar contraseña -> OK
        if (passEmpty && confirmEmpty) {
            return true;
        }

        // Si mandaron uno pero no el otro -> ERROR
        if (passEmpty || confirmEmpty) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Debe confirmar la contraseña")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
            return false;
        }

        // Ambos vienen -> deben coincidir
        boolean ok = pass.equals(confirm);
        if (!ok) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Las contraseñas no coinciden")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }
        return ok;
    }
}
