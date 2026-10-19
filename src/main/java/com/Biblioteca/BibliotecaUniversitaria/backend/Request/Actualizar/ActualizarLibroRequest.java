package com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Libro;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarLibroRequest {
    private String titulo;
    private String autor;
    private String isbn;
    private String categoria;
    private Integer cantidadtotal;
    private Integer cantidaddisponible;
}
