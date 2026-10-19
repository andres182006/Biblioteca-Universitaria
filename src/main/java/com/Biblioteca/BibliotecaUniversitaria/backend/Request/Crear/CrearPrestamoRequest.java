package com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearPrestamoRequest {
    @NotNull(message = "El estudiante es obligatorio")
    private Long idestudiante;

    @NotNull(message = "El libro es obligatorio")
    private Long idlibro;

    // Temporal: bibliotecario que registra el préstamo. Cuando exista el login (HU07)
    // se quita de aquí y se toma del usuario autenticado.
    @NotNull(message = "El usuario que registra el préstamo es obligatorio")
    private Long idusuario;
}
