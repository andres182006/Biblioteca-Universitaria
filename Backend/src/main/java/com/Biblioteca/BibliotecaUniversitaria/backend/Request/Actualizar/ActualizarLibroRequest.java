package com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Libro;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarLibroRequest {
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200)
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    @Size(max = 150)
    private String autor;

    @Size(max = 20)
    private String isbn;

    @Size(max = 80)
    private String categoria;

    @NotNull(message = "La cantidad de copias es obligatoria")
    @Min(value = 1, message = "Debe haber al menos una copia")
    private Integer cantidadtotal;

}
