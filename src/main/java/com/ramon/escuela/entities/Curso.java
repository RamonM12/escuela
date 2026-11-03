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
@Table(name = "CURSOS")
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CURSO")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, unique = true, length = 100)
    private String nombreCursos;

    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;

    @Column(name = "CREDITOS", nullable = false)
    private Integer creditos;

    public static void validarDatos(String nombreCursos, String descripcion, Integer creditos) {
        StringCustomUtils.validarTamanio(nombreCursos, 3, 100,
                "El nombre del curso es requerido y debe tener entre 3 y 100 caracteres");

        if (descripcion != null && !descripcion.isBlank()) {
            StringCustomUtils.validarTamanio(descripcion, 0, 200,
                    "La descripción no puede superar los 200 caracteres");
        }

        ValoresNumericosUtils.validarEnteroPositivo(creditos,
                "Los créditos son requeridos y deben ser un número entero positivo");
    }

    public void actualizar(String nombreCursos, String descripcion, Integer creditos) {
        validarDatos(nombreCursos, descripcion, creditos);
        this.nombreCursos = nombreCursos.trim();
        this.descripcion = descripcion != null ? descripcion.trim() : null;
        this.creditos = creditos;
    }

    public static Curso crear(String nombreCursos, String descripcion, Integer creditos) {
        validarDatos(nombreCursos, descripcion, creditos);
        return Curso.builder()
                .nombreCursos(nombreCursos.trim())
                .descripcion(descripcion != null ? descripcion.trim() : null)
                .creditos(creditos)
                .build();
    }
}
