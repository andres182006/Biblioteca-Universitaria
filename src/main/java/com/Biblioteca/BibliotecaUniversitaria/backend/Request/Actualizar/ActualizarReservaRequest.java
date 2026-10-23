package com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ActualizarReservaRequest {
    @NotNull(message = "El videobeam es obligatorio")
    private Long idvideobeam;

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha no puede ser pasada")
    private LocalDate fechareserva;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horainicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horafin;

    @Size(max = 200)
    private String motivo;
}