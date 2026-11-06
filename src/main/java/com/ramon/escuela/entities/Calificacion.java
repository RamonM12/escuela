package com.ramon.escuela.entities;

import com.ramon.escuela.exceptions.DatoInvalidoException;
import com.ramon.escuela.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder @Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CALIFICACIONES")
public class Calificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CALIFICACION")
    private Long id;

    @Column(name = "CALIFICACION", nullable = false, precision = 3, scale = 1)
    private BigDecimal calificacion;

    @Builder.Default
    @Column(name = "FECHA_REGISTRO")
    private LocalDate fechaRegistro = LocalDate.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_INSCRIPCION", nullable = false, unique = true)
    private Inscripcion inscripcion;

    public static void validarDatos(BigDecimal calificacion, Inscripcion inscripcion) {
        ValoresNumericosUtils.validarBigDecimalEnRango(calificacion, BigDecimal.ZERO, BigDecimal.TEN,
                "La calificación es requerida y debe estar entre 0 y 10");

        if (inscripcion == null) {
            throw new DatoInvalidoException("La inscripción asociada es requerida");
        }
    }

    public boolean cambioEnDatos(BigDecimal calificacion, Inscripcion inscripcion) {
        boolean cambioCalificacion = this.calificacion.compareTo(calificacion) != 0;
        boolean cambioInscripcion = !this.inscripcion.getId().equals(inscripcion.getId());

        return cambioCalificacion || cambioInscripcion;
    }

    public void actualizar(BigDecimal calificacion, Inscripcion inscripcion) {
        validarDatos(calificacion, inscripcion);
        this.calificacion = calificacion;
        this.inscripcion = inscripcion;
    }

    public static Calificacion crear(BigDecimal calificacion, Inscripcion inscripcion) {
        validarDatos(calificacion, inscripcion);
        return Calificacion.builder()
                .calificacion(calificacion)
                .inscripcion(inscripcion)
                .fechaRegistro(LocalDate.now())
                .build();
    }
}