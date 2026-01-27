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

        int provincia = Integer.parseInt(ruc.substring(0, 2));
        if (!isProvinciaValida(provincia)) {
            return false;
        }

        int tercerDigito = Character.getNumericValue(ruc.charAt(2));

        // Persona natural (0..5): últimos 3 = establecimiento, != 000
        if (tercerDigito >= 0 && tercerDigito <= 5) {
            String establecimiento = ruc.substring(10, 13);
            if (establecimiento.equals("000")) return false;

            String cedula = ruc.substring(0, 10);
            return validarCedula(cedula);
        }

        // Entidad pública (6): últimos 4 = establecimiento, != 0000
        if (tercerDigito == 6) {
            String establecimiento = ruc.substring(9, 13); // 4 dígitos
            if (establecimiento.equals("0000")) return false;

            return validarRucPublico(ruc);
        }

        // Sociedad privada (9): últimos 3 = establecimiento, != 000
        if (tercerDigito == 9) {
            String establecimiento = ruc.substring(10, 13);
            if (establecimiento.equals("000")) return false;

            return validarRucPrivado(ruc);
        }

        // 7 u 8 no son válidos para RUC en Ecuador
        return false;
    }

    private boolean isProvinciaValida(int provincia) {
        return (provincia >= 1 && provincia <= 24) || provincia == 30; // 30: exterior
    }

    // ========== Persona natural (cédula) ==========
    private boolean validarCedula(String cedula) {

        if (!cedula.matches("\\d{10}")) return false;

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        int tercerDigito = Character.getNumericValue(cedula.charAt(2));

        if (!isProvinciaValida(provincia)) return false;
        if (tercerDigito >= 6) return false;

        int suma = 0;
        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};

        for (int i = 0; i < coeficientes.length; i++) {
            int valor = Character.getNumericValue(cedula.charAt(i)) * coeficientes[i];
            suma += (valor >= 10) ? valor - 9 : valor;
        }

        int digitoVerificador = (10 - (suma % 10)) % 10;
        return digitoVerificador == Character.getNumericValue(cedula.charAt(9));
    }

    // ========== Sociedad privada (tercer dígito = 9) ==========
    // DV está en la posición 10 (índice 9)
    private boolean validarRucPrivado(String ruc) {
        int[] coef = {4, 3, 2, 7, 6, 5, 4, 3, 2}; // se multiplican por los primeros 9 dígitos
        int suma = 0;

        for (int i = 0; i < coef.length; i++) {
            suma += Character.getNumericValue(ruc.charAt(i)) * coef[i];
        }

        int residuo = suma % 11;
        int dvCalculado = (residuo == 0) ? 0 : 11 - residuo;

        int dv = Character.getNumericValue(ruc.charAt(9));
        return dvCalculado == dv;
    }

    // ========== Entidad pública (tercer dígito = 6) ==========
    // DV está en la posición 9 (índice 8)
    private boolean validarRucPublico(String ruc) {
        int[] coef = {3, 2, 7, 6, 5, 4, 3, 2}; // se multiplican por los primeros 8 dígitos
        int suma = 0;

        for (int i = 0; i < coef.length; i++) {
            suma += Character.getNumericValue(ruc.charAt(i)) * coef[i];
        }

        int residuo = suma % 11;
        int dvCalculado = (residuo == 0) ? 0 : 11 - residuo;

        int dv = Character.getNumericValue(ruc.charAt(8));
        return dvCalculado == dv;
    }
}
