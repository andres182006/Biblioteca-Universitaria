package com.Biblioteca.BibliotecaUniversitaria.backend.Service;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.LibroDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarLibroRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearLibroRequest;

import java.util.List;

public interface ILibroService {
    LibroDTO crear(CrearLibroRequest crearLibroRequest);
    LibroDTO actualizar(Long id,ActualizarLibroRequest actualizarLibroRequest);
    LibroDTO leer(Long id);
    List<LibroDTO> listar();
    void eliminar(Long id);

}
