package com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions;

public class EstudianteNoEncontradoException extends RecursoNoEncontradoException {
    private static final String DESCRIPCION = "no encontrado";
    public EstudianteNoEncontradoException(String message) {
        super(String.format("%s: %s", DESCRIPCION, message));
    }
}
