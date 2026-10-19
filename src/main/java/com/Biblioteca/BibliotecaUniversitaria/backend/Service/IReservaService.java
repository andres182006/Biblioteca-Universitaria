package com.Biblioteca.BibliotecaUniversitaria.backend.Service;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.ReservaDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarReservaRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearReservaRequest;

import java.util.List;

public interface IReservaService {
    ReservaDTO crear(CrearReservaRequest crearReservaRequest);
    ReservaDTO actualizar(Long id, ActualizarReservaRequest actualizarReservaRequest);
    ReservaDTO leerid(Long id);
    List<ReservaDTO> listar();

}
