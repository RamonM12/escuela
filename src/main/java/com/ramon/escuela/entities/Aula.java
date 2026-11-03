package com.ramon.escuela.entities;

import com.ramon.escuela.utils.StringCustomUtils;
import com.ramon.escuela.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Table(name = "AULAS")
public class Aula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AULA")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "CAPACIDAD", nullable = false)
    private int capacidad;

    //MIO
    public static void validarDatos(String nombre, int capacidad) {
        StringCustomUtils.validarTamanio(nombre, 3, 100,
                "El nombre del aula es requerido y debe tener entre 3 y 100 caracteres");

        ValoresNumericosUtils.validarEnteroPositivo(capacidad,
                "La capacidad debe ser un número entero positivo");
    }

    public void actualizar(String nombre, int capacidad) {
        validarDatos(nombre, capacidad);
        this.nombre = nombre.trim();
        this.capacidad = capacidad;
    }

    public static Aula crear(String nombre, int capacidad) {
        validarDatos(nombre, capacidad);
        return Aula.builder()
                .nombre(nombre.trim())
                .capacidad(capacidad)
                .build();
    }
}
