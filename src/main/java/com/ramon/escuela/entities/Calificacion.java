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
    private Long idCalificacion;

    @Column(name = "CALIFICACION", nullable = false)
    private BigDecimal calificacion;


    @Column(name = "FECHA_REGISTRO", nullable = false)
    private LocalDate fechaRegistro = LocalDate.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_INSCRIPCION", nullable = false, unique = true)
    private Inscripcion inscripcion;

    //MIO
    public static void validarDatos(BigDecimal calificacion, Inscripcion inscripcion) {
        ValoresNumericosUtils.validarBigDecimalPositivo(calificacion,
                "La calificación es requerida y debe ser un valor positivo");

        if (inscripcion == null) {
            throw new DatoInvalidoException("La inscripción asociada es requerida");
        }
    }

    public void actualizar(BigDecimal nuevaCalificacion) {
        ValoresNumericosUtils.validarBigDecimalPositivo(nuevaCalificacion,
                "La calificación es requerida y debe ser un valor positivo");
        this.calificacion = nuevaCalificacion;
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
