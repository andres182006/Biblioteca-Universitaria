package com.Biblioteca.BibliotecaUniversitaria.backend.DTO;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Reserva;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaDTO {

    private Long id;

    // DTOs anidados en lugar de entidades, para no exponer la contraseña del profesor
    private UsuarioDTO usuarioprofesor;
    private VideobeamDTO videobeam;

    private LocalDate fechareserva;
    private LocalTime horainicio;
    private LocalTime horafin;
    private String motivo;
    private Reserva.EstadoReserva estado ;

    // La asigna el sistema automáticamente al crear la reserva
    private LocalDateTime fechacreacion;
}
