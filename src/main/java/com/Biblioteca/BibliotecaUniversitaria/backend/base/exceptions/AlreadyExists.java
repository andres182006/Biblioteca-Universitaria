package com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions;

/**
 * Excepción de negocio lanzada cuando se intenta crear un registro
 * que ya existe en la base de datos.
 *
 * Hereda de RuntimeException (no verificada), por lo que no es necesario
 * declararla en la firma de los métodos.
 *
 * Ejemplo de uso:
 *   throw new AlreadyExists("Ya existe una cita en esa fecha y hora.");
 */
public class AlreadyExists extends RuntimeException {
    public AlreadyExists(String message) {
        super(message);
    }
}
