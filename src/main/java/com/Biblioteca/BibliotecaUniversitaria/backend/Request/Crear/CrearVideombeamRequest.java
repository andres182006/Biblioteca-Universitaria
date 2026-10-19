package com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Videobeam;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearVideombeamRequest {
    private String codigo;
    private String descripcion;
    private Videobeam.EstadoVideobeam estado;
}
