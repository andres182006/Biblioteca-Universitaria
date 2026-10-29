package com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarUsuarioRequest {

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 120)
    private String nombrecompleto;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 120)
    private String correo;
}
