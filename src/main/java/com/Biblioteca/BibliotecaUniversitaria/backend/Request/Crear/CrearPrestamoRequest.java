package com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearPrestamoRequest {


    @NotNull(message = "El libro es obligatorio")
    private Long idlibro;
}