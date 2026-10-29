package com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RechazarPrestamoRequest {
    @Size(max = 200)
    private String motivo;
}