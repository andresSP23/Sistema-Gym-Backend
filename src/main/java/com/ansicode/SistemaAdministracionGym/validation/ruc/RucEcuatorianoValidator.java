package com.ansicode.SistemaAdministracionGym.validation.ruc;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RucEcuatorianoValidator  implements ConstraintValidator<RucEcuatoriano, String> {


    @Override
    public boolean isValid(String ruc, ConstraintValidatorContext context) {

        if (ruc == null || ruc.isBlank()) {
            return true; // @NotBlank se encarga
        }

        if (!ruc.matches("\\d{13}")) {
            return false;
        }

        String cedula = ruc.substring(0, 10);
        String establecimiento = ruc.substring(10, 13);

        if (establecimiento.equals("000")) {
            return false;
        }

        return validarCedula(cedula);
    }

    // 🔐 Algoritmo oficial de cédula ecuatoriana
    private boolean validarCedula(String cedula) {

        if (!cedula.matches("\\d{10}")) {
            return false;
        }

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        int tercerDigito = Integer.parseInt(cedula.substring(2, 3));

        if (provincia < 1 || provincia > 24) return false;
        if (tercerDigito >= 6) return false;

        int suma = 0;
        int[] coeficientes = {2,1,2,1,2,1,2,1,2};

        for (int i = 0; i < coeficientes.length; i++) {
            int valor = Character.getNumericValue(cedula.charAt(i)) * coeficientes[i];
            suma += (valor >= 10) ? valor - 9 : valor;
        }

        int digitoVerificador = (10 - (suma % 10)) % 10;

        return digitoVerificador == Character.getNumericValue(cedula.charAt(9));
    }
}
