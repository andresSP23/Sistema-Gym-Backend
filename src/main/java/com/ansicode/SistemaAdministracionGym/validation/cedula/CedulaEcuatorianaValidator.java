package com.ansicode.SistemaAdministracionGym.validation.cedula;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CedulaEcuatorianaValidator  implements ConstraintValidator<CedulaEcuatoriana,String> {
    @Override
    public void initialize(CedulaEcuatoriana constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String cedula, ConstraintValidatorContext context) {

        if (cedula == null || !cedula.matches("\\d{10}")) {
            return false;
        }

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        int tercerDigito = Character.getNumericValue(cedula.charAt(2));

        if (provincia < 1 || provincia > 24 || tercerDigito >= 6) {
            return false;
        }

        int[] coeficientes = {2,1,2,1,2,1,2,1,2};
        int suma = 0;

        for (int i = 0; i < coeficientes.length; i++) {
            int valor = Character.getNumericValue(cedula.charAt(i)) * coeficientes[i];
            suma += (valor >= 10) ? valor - 9 : valor;
        }

        int digitoVerificador = Character.getNumericValue(cedula.charAt(9));
        int modulo = suma % 10;
        int resultado = (modulo == 0) ? 0 : 10 - modulo;

        return resultado == digitoVerificador;
    }

}
