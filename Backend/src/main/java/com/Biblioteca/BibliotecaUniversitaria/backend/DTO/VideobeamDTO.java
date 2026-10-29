package com.Biblioteca.BibliotecaUniversitaria.backend.DTO;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Videobeam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideobeamDTO {

    private Long id;
    private String codigo;
    private String descripcion;
    private Videobeam.EstadoVideobeam estado;
}
