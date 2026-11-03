package com.ramon.escuela.entities;

import com.ramon.escuela.exceptions.DatoInvalidoException;
import com.ramon.escuela.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.*;

@Builder @Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GRUPOS", uniqueConstraints = @UniqueConstraint(
        name = "GRUPO_CU_MA_AU_PE_UK",
        columnNames = {"ID_CURSO", "ID_MAESTRO", "ID_AULA", "PERIODO"}
))
public class Grupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_GRUPO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CURSO", nullable = false)
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MAESTRO", nullable = false)
    private Maestro maestro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AULA", nullable = false)
    private Aula aula;

    @Column(name = "PERIODO", nullable = false, length = 20)
    private String periodo;

    public static void validarDatos(Curso curso, Maestro maestro, Aula aula, String periodo) {
        if (curso == null) {
            throw new DatoInvalidoException("El curso es requerido");
        }
        if (maestro == null) {
            throw new DatoInvalidoException("El maestro es requerido");
        }
        if (aula == null) {
            throw new DatoInvalidoException("El aula es requerida");
        }
        StringCustomUtils.validarTamanio(periodo, 3, 20,
                "El periodo es requerido y debe tener entre 3 y 20 caracteres");
    }

    public void actualizar(Curso curso, Maestro maestro, Aula aula, String periodo) {
        validarDatos(curso, maestro, aula, periodo);
        this.curso = curso;
        this.maestro = maestro;
        this.aula = aula;
        this.periodo = periodo.trim();
    }

    public static Grupo crear(Curso curso, Maestro maestro, Aula aula, String periodo) {
        validarDatos(curso, maestro, aula, periodo);
        return Grupo.builder()
                .curso(curso)
                .maestro(maestro)
                .aula(aula)
                .periodo(periodo.trim())
                .build();
    }
}
