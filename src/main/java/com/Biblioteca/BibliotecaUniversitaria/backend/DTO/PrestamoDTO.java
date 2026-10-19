package com.Biblioteca.BibliotecaUniversitaria.backend.DTO;


import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Prestamo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrestamoDTO {

    private Long id;
    //Se usan DTOs anidados (EstudianteDTO, LibroDTO, UsuarioDTO) y no las entidades
    // completas, para no exponer datos internos como la contraseña del bibliotecario
    private EstudianteDTO estudiante;
    private LibroDTO libro;
    private UsuarioDTO usuariobibliotecario;

    private LocalDate fechaprestamo;

    // Opcional: el préstamo no tiene plazo fijo, por eso puede venir en null
    private LocalDate fechadevolucionesperada;

    // Es null mientras el préstamo siga activo; se llena al registrar la devolución
    private LocalDate fechadevolucionreal;

    private Prestamo.EstadoPrestamo estado;

}
