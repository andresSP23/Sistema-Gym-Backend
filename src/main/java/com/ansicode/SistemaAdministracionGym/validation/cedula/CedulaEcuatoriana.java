package com.ansicode.SistemaAdministracionGym.validation.cedula;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CedulaEcuatorianaValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface  CedulaEcuatoriana {


    String message() default "La cédula ecuatoriana no es válida";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
