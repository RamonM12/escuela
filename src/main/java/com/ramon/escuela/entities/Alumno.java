package com.ramon.escuela.entities;

import com.ramon.escuela.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Table(name = "ALUMNOS")
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ALUMNO")
    private Long idAlumno;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombreAlumno;

    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaternoAlumno;

    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private String apellidoMaternoAlumno;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "MATRICULA", nullable = false, unique = true, length = 10)
    private String matricula;

    @Builder.Default
    @Column(name = "FECHA_INGRESO")
    private LocalDate fechaIngreso = LocalDate.now();

    @Builder.Default
    @OneToMany(mappedBy = "alumno", fetch = FetchType.LAZY)
    private List<Inscripcion> incripciones = new ArrayList<>();

    //MIO
    public static void validarDatos(
            String nombreAlumno,
            String apellidoPaternoAlumno,
            String apellidoMaternoAlumno,
            String email,
            String matricula) {

        StringCustomUtils.validarTamanio(nombreAlumno, 2, 50,
                "El nombre es requerido y debe tener entre 2 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoPaternoAlumno, 2, 50,
                "El apellido paterno es requerido y debe tener entre 2 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoMaternoAlumno, 2, 50,
                "El apellido materno es requerido y debe tener entre 2 y 50 caracteres");

        StringCustomUtils.validarTamanio(email, 5, 100,
                "El email es requerido y debe tener una longitud válida");

        StringCustomUtils.validarTamanio(matricula, 1, 10,
                "La matrícula es requerida y debe tener máximo 10 caracteres");
    }

    public void actualizar(
            String nombreAlumno,
            String apellidoPaternoAlumno,
            String apellidoMaternoAlumno,
            String email,
            String matricula) {

        validarDatos(nombreAlumno, apellidoPaternoAlumno, apellidoMaternoAlumno, email, matricula);

        this.nombreAlumno = nombreAlumno.trim();
        this.apellidoPaternoAlumno = apellidoPaternoAlumno.trim();
        this.apellidoMaternoAlumno = apellidoMaternoAlumno.trim();
        this.email = email.trim();
        this.matricula = matricula.trim();
    }

    public static Alumno crear(
            String nombreAlumno,
            String apellidoPaternoAlumno,
            String apellidoMaternoAlumno,
            String email,
            String matricula) {

        validarDatos(nombreAlumno, apellidoPaternoAlumno, apellidoMaternoAlumno, email, matricula);

        return Alumno.builder()
                .nombreAlumno(nombreAlumno.trim())
                .apellidoPaternoAlumno(apellidoPaternoAlumno.trim())
                .apellidoMaternoAlumno(apellidoMaternoAlumno.trim())
                .email(email.trim())
                .matricula(matricula.trim())
                .build();
    }
}
