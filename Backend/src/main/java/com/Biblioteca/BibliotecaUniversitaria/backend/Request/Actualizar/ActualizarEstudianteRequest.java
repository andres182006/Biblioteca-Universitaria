package com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarEstudianteRequest {
    @NotBlank(message = "el nombre completo es obligatorio")
    @Size(max = 120)
    private String nombrecompleto;

    @NotBlank(message = "el correo es obligatorio")
    @Email(message = "el correo no tiene un formato valido")
    @Size(max = 120)
    private String correo;

    @Size(max = 20)
    private String telefono;
}
