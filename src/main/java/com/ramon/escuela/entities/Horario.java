package com.ramon.escuela.entities;


import com.ramon.escuela.enums.DiaSemana;
import com.ramon.escuela.exceptions.DatoInvalidoException;
import com.ramon.escuela.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.*;


@Builder @Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HORARIOS")
public class Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HORARIO")
    private Long idHorario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GRUPO", nullable = false)
    private Grupo grupo;

    @Enumerated(EnumType.STRING)
    @Column(name = "DIA", nullable = false)
    private DiaSemana diaSemana;

    @Column(name = "HORA_INICIO", nullable = false, length = 5)
    private String horaInicio;

    @Column(name = "HORA_FIN", nullable = false, length = 5)
    private String horaFin;

    public static void validarDatos(Grupo grupo, DiaSemana diaSemana, String horaInicio, String horaFin) {
        if (grupo == null) {
            throw new DatoInvalidoException("El grupo es requerido");
        }
        if (diaSemana == null) {
            throw new DatoInvalidoException("El día de la semana es requerido");
        }
        StringCustomUtils.validarTamanio(horaInicio, 5, 5,
                "La hora de inicio es requerida y debe tener el formato HH:mm (5 caracteres)");
        StringCustomUtils.validarTamanio(horaFin, 5, 5,
                "La hora de fin es requerida y debe tener el formato HH:mm (5 caracteres)");
    }

    public void actualizar(Grupo grupo, DiaSemana diaSemana, String horaInicio, String horaFin) {
        validarDatos(grupo, diaSemana, horaInicio, horaFin);
        this.grupo = grupo;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio.trim();
        this.horaFin = horaFin.trim();
    }

    public static Horario crear(Grupo grupo, DiaSemana diaSemana, String horaInicio, String horaFin) {
        validarDatos(grupo, diaSemana, horaInicio, horaFin);
        return Horario.builder()
                .grupo(grupo)
                .diaSemana(diaSemana)
                .horaInicio(horaInicio.trim())
                .horaFin(horaFin.trim())
                .build();
    }
}
