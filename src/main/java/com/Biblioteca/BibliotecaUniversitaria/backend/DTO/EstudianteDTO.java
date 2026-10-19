package com.Biblioteca.BibliotecaUniversitaria.backend.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstudianteDTO {

    private Long id;
    private String nombrecompleto;
    private String documento;
    private String correo;
    private String telefono;
    private LocalDateTime fecharegistro;
}
