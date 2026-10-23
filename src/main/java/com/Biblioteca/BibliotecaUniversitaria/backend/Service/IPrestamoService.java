package com.Biblioteca.BibliotecaUniversitaria.backend.Service;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.PrestamoDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.RechazarPrestamoRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearPrestamoRequest;

import java.util.List;

public interface IPrestamoService {
        PrestamoDTO solicitar(CrearPrestamoRequest request, String correo);
        PrestamoDTO aprobar(Long id, String correo);
        PrestamoDTO rechazar(Long id, RechazarPrestamoRequest request, String correo);
        PrestamoDTO cancelar(Long id, String correo);
        PrestamoDTO registrarDevolucion(Long id);
        PrestamoDTO leer(Long id);
        List<PrestamoDTO> listarSolicitudes();
        List<PrestamoDTO> listarMisPrestamos(String correo);
        List<PrestamoDTO> listarActivos();
        List<PrestamoDTO> listarHistorial();
    }

