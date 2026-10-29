package com.Biblioteca.BibliotecaUniversitaria.backend.DTO;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {

    private Long id;
    private String nombrecompleto;
    private String documento;
    private String correo;
    // el atributo private String contrasena;
    // no se coloca por seguridad para no exponer la contraseña
    private Usuario.RolUsuario rol;
    private LocalDateTime fecharegistro;

}
