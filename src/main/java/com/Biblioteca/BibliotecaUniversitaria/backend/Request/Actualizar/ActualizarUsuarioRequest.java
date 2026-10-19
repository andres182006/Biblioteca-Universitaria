package com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarUsuarioRequest {
    private String nombrecompleto;
    private String documento;
    private String correo;
    private String contrasena;
}
