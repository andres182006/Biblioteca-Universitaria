package com.Biblioteca.BibliotecaUniversitaria.backend.Service;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.ReservaDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.VideobeamDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarReservaRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearReservaRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IReservaService {
    ReservaDTO crear(CrearReservaRequest crearReservaRequest, String correo);
    ReservaDTO actualizar(Long id, ActualizarReservaRequest actualizarReservaRequest, String correo);
    ReservaDTO leerid(Long id);
    List<ReservaDTO> listar(String correo);
    ReservaDTO cancelar(Long id, String correo);
    ReservaDTO entregar(Long id, String correo);
    ReservaDTO devolver(Long id, String correo);
    List<VideobeamDTO> disponibilidad(LocalDate fecha, LocalTime horainicio, LocalTime horafin);

}
