package com.ramon.escuela.entities;

import com.ramon.escuela.exceptions.DatoInvalidoException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "INSCRIPCIONES", uniqueConstraints = @UniqueConstraint(
        name = "INSCRIPCION_ALU_GRU_UK",
        columnNames = {"ID_ALUMNO", "ID_GRUPO"}
))
public class Inscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_INSCRIPCION")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ALUMNO", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GRUPO", nullable = false)
    private Grupo grupo;

    @Builder.Default
    @Column(name = "FECHA_INSCRIPCION")
    private LocalDate fechaInscripcion = LocalDate.now();

    @OneToOne(mappedBy = "inscripcion")
    private Calificacion calificacion;

    public static void validarDatos(Alumno alumno, Grupo grupo) {
        if (alumno == null) {
            throw new DatoInvalidoException("El alumno es requerido");
        }
        if (grupo == null) {
            throw new DatoInvalidoException("El grupo es requerido");
        }
    }

    public boolean cambioEnDatos(Alumno alumno, Grupo grupo) {
        boolean cambioAlumno = !this.alumno.getId().equals(alumno.getId());
        boolean cambioGrupo = !this.grupo.getId().equals(grupo.getId());

        return cambioAlumno || cambioGrupo;
    }

    public void actualizar(Alumno alumno, Grupo grupo) {
        validarDatos(alumno, grupo);
        this.alumno = alumno;
        this.grupo = grupo;
    }

    public static Inscripcion crear(Alumno alumno, Grupo grupo) {
        validarDatos(alumno, grupo);
        return Inscripcion.builder()
                .alumno(alumno)
                .grupo(grupo)
                .fechaInscripcion(LocalDate.now())
                .build();
    }
}

