package com.Biblioteca.BibliotecaUniversitaria.backend.Service;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.EstudianteDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarEstudianteRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearEstudianteRequest;

import java.util.List;

public interface IEstudianteService {
    EstudianteDTO crear(CrearEstudianteRequest crearEstudianteRequest);

    //se decio utilizar el mismo CrearEstudianteRequest para actualizar ya que
    // no era necesario crear un ActualizarEstudianteRequest ya que dichos
    // atributos que se requieren actualizar son los mismo de crear no se agregan
    // o se quitan mas atributos
    EstudianteDTO actualizar(Long id, ActualizarEstudianteRequest actualizarEstudianteRequest);

    EstudianteDTO leer(String documento);
    List<EstudianteDTO> listar();
    void eliminar(Long id);




}
