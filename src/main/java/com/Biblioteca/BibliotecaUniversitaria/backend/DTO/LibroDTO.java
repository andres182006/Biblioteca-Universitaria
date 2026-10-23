package com.Biblioteca.BibliotecaUniversitaria.backend.DTO;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Libro;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibroDTO {

    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private String categoria;
    private Integer cantidadtotal;
    private Integer cantidaddisponible;
    private LocalDateTime fecharegistro;

}
