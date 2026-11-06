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

    @Column(name = "NOMBRE", nullable = false, unique = true, length = 30)
    private String nombre;

    @Column(name = "CAPACIDAD", nullable = false)
    private Integer capacidad;

    public static void validarDatos(String nombre, Integer capacidad) {
        StringCustomUtils.validarTamanio(nombre, 3, 30,
                "El nombre del aula es requerido y debe tener entre 3 y 30 caracteres");

        ValoresNumericosUtils.validarEnteroPositivo(capacidad,
                "La capacidad debe ser un número entero positivo");
    }

    public boolean cambioEnDatos(String nombre, Integer capacidad){
        boolean cambioNombre = !this.nombre.equals(nombre.trim());
        boolean cambioCapacidad = !this.capacidad.equals(capacidad);

        return cambioNombre || cambioCapacidad;
    }

    public void actualizar(String nombre, Integer capacidad) {
        validarDatos(nombre, capacidad);
        this.nombre = nombre.trim();
        this.capacidad = capacidad;
    }

    public static Aula crear(String nombre, Integer capacidad) {
        validarDatos(nombre, capacidad);
        return Aula.builder()
                .nombre(nombre.trim())
                .capacidad(capacidad)
                .build();
    }
}
