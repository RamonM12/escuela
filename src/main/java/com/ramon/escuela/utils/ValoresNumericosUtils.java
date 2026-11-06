package com.ramon.escuela.utils;

import com.ramon.escuela.exceptions.DatoInvalidoException;

import java.math.BigDecimal;

public class ValoresNumericosUtils {
    public static <N extends Number>void validarNumeroRequerido(N numero, String mensaje){
        if(numero == null)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarEnteroPositivo(Integer numero, String mensaje){
        validarNumeroRequerido(numero, mensaje);

        if(numero <= 0)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarBigDecimalPositivo(BigDecimal numero, String mensaje){
        validarNumeroRequerido(numero, mensaje);

        if(numero.compareTo(BigDecimal.ZERO) <= 0)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarBigDecimalEnRango(BigDecimal numero, BigDecimal min, BigDecimal max, String mensaje){
        validarNumeroRequerido(numero, mensaje);

        if(numero.compareTo(min) < 0 || numero.compareTo(max) > 0)
            throw new DatoInvalidoException(mensaje);
    }
}