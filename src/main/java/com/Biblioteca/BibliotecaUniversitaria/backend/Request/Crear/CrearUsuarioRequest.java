package com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Usuario;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CrearUsuarioRequest {
    private String nombrecompleto;
    private String documento;
    private String correo;
    private String contrasena;
    private Usuario.RolUsuario rol;
}
