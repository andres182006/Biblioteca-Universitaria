package com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CrearUsuarioRequest {
    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 120)
    private String nombrecompleto;

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 20)
    private String documento;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    private String contrasena;
}
