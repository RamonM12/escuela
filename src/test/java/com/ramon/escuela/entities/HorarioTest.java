package com.ramon.escuela.entities;

import com.ramon.escuela.enums.DiaSemana;
import com.ramon.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HorarioTest {
    @Test
    void crear_debeRetornarHorario_cuandoDatosSonValidos() {
        // Arrange
        Grupo grupo = new Grupo();

        // Act
        Horario horario = Horario.crear(grupo, DiaSemana.LUNES, "07:00", "09:00");

        // Assert
        assertThat(horario).isNotNull();
        assertThat(horario.getGrupo()).isEqualTo(grupo);
        assertThat(horario.getDiaSemana()).isEqualTo(DiaSemana.LUNES);
        assertThat(horario.getHoraInicio()).isEqualTo("07:00");
        assertThat(horario.getHoraFin()).isEqualTo("09:00");
    }

    @Test
    void actualizar_debeModificarDatos_cuandoTodoEsValido() {
        // Arrange
        Horario horario = Horario.builder()
                .horaInicio("08:00")
                .horaFin("10:00")
                .build();

        Grupo nuevoGrupo = new Grupo();

        // Act
        horario.actualizar(nuevoGrupo, DiaSemana.MARTES, "10:00", "12:00");

        // Assert
        assertThat(horario.getGrupo()).isEqualTo(nuevoGrupo);
        assertThat(horario.getDiaSemana()).isEqualTo(DiaSemana.MARTES);
        assertThat(horario.getHoraInicio()).isEqualTo("10:00");
        assertThat(horario.getHoraFin()).isEqualTo("12:00");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoGrupoEsNulo() {
        assertThatThrownBy(() -> Horario.crear(null, DiaSemana.LUNES, "07:00", "09:00"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El grupo es requerido");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoDiaSemanaEsNulo() {
        assertThatThrownBy(() -> Horario.crear(new Grupo(), null, "07:00", "09:00"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El día de la semana es requerido");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoFormatoHoraInicioEsInvalido() {
        // Sin el cero inicial
        assertThatThrownBy(() -> Horario.crear(new Grupo(), DiaSemana.LUNES, "7:00", "09:00"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La hora de inicio es requerida y debe tener el formato HH:mm");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoHoraNoExiste() {
        // Tiene 5 caracteres, pero 25 no es una hora valida
        assertThatThrownBy(() -> Horario.crear(new Grupo(), DiaSemana.LUNES, "08:00", "25:00"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La hora de fin es requerida y debe tener el formato HH:mm");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoHoraFinEsAnteriorAInicio() {
        assertThatThrownBy(() -> Horario.crear(new Grupo(), DiaSemana.LUNES, "10:00", "08:00"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La hora de fin debe ser posterior a la hora de inicio");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoHorasSonIguales() {
        assertThatThrownBy(() -> Horario.crear(new Grupo(), DiaSemana.LUNES, "10:00", "10:00"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La hora de fin debe ser posterior a la hora de inicio");
    }

    @Test
    void seTraslapaCon_debeRetornarTrue_cuandoLasHorasSeCruzan() {
        Horario horario = Horario.crear(new Grupo(), DiaSemana.LUNES, "08:00", "10:00");

        assertThat(horario.seTraslapaCon("09:00", "11:00")).isTrue();
    }

    @Test
    void seTraslapaCon_debeRetornarFalse_cuandoUnoEmpiezaJustoAlTerminarElOtro() {
        Horario horario = Horario.crear(new Grupo(), DiaSemana.LUNES, "08:00", "10:00");

        assertThat(horario.seTraslapaCon("10:00", "12:00")).isFalse();
    }
}