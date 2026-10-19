package com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Libro;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CrearLibroRequest {
    private String titulo;
    private String autor;
    private String isbn;
    private Integer cantidadtotal;
    private String categoria;

}
