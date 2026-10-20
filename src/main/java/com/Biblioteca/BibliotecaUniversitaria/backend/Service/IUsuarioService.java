package com.Biblioteca.BibliotecaUniversitaria.backend.Service;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.UsuarioDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarUsuarioRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearUsuarioRequest;

import java.util.List;

public interface IUsuarioService {
UsuarioDTO crear(CrearUsuarioRequest crearUsuarioRequest);
UsuarioDTO actualizar(Long id, ActualizarUsuarioRequest ActualizarUsuarioRequest);
UsuarioDTO leerid(String documento);
List<UsuarioDTO> leer();
}
