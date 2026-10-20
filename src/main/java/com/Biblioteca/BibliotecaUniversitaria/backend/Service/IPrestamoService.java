package com.Biblioteca.BibliotecaUniversitaria.backend.Service;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.PrestamoDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearPrestamoRequest;

import java.util.List;

public interface IPrestamoService {
    PrestamoDTO crear(CrearPrestamoRequest crearPrestamoRequest);
    PrestamoDTO registrarDevolucion(Long id);
    PrestamoDTO leer(Long id);
    List<PrestamoDTO> listarActivos();
    List<PrestamoDTO> listarHistorial();
}
