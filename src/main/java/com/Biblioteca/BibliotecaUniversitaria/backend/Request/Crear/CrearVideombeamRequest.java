package com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Videobeam;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearVideombeamRequest {
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20)
    private String codigo;

    @Size(max = 200)
    private String descripcion;

    @NotNull(message = "El estado es obligatorio")
    private Videobeam.EstadoVideobeam estado;
}
