package com.ansicode.SistemaAdministracionGym.validation.ruc;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RucEcuatorianoValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface  RucEcuatoriano {

    String message() default "El RUC ecuatoriano no es válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
