package com.Biblioteca.BibliotecaUniversitaria.backend.Service;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.VideobeamDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearVideombeamRequest;

import java.util.List;

public interface IVideonbeamService {
    VideobeamDTO crear(CrearVideombeamRequest crearVideombeamRequest);
    VideobeamDTO actualizar(Long id, CrearVideombeamRequest crearVideombeamRequest);
    VideobeamDTO leer(Long id);
    List<VideobeamDTO> listar();
    void eliminar(Long id);
}
