package com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearEstudianteRequest {

    @NotBlank(message = "el nombre completo es obligatorio")
    @Size(max = 120)
    private String nombrecompleto;

    @NotBlank(message = "el documento es obligatorio")
    @Size(max = 20)
    private String documento;

    @NotBlank(message = "el correo es obligatorio")
    @Email(message = "el correo no tiene un formato valido")
    @Size(max = 120)
    private String correo;

    @NotBlank(message = "la contraseña es obligatoria")
    @Size(min = 8, max = 72, message = "la contraseña debe tener entre 8 y 72 caracteres")
    private String contrasena;

    @Size(max = 20)
    private String telefono;
}
