package com.Biblioteca.BibliotecaUniversitaria.backend.Service;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.ReservaDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.VideobeamDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarReservaRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearReservaRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IReservaService {
    ReservaDTO crear(CrearReservaRequest crearReservaRequest);
    ReservaDTO actualizar(Long id, ActualizarReservaRequest actualizarReservaRequest);
    ReservaDTO leerid(Long id);
    List<ReservaDTO> listar(Long idusuario);
    ReservaDTO cancelar(Long id, Long idusuario);
    ReservaDTO entregar(Long id, Long idusuario);
    ReservaDTO devolver(Long id, Long idusuario);
    List<VideobeamDTO> disponibilidad(LocalDate fecha, LocalTime horainicio, LocalTime horafin);

}
