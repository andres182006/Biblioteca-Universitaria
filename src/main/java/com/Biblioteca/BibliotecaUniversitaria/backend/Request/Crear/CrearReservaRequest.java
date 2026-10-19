package com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.UsuarioDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.VideobeamDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class CrearReservaRequest {
    private Long idusuarioprofesor;
    private Long idvideobeam;
    private LocalDate fechareserva;
    private LocalTime horainicio;
    private LocalTime horafin;
    private String motivo;
}
