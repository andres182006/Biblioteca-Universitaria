package com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions;

import com.Biblioteca.BibliotecaUniversitaria.backend.base.configs.dto.ResponseDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ResponseDTO<?>> noEncontrado(RecursoNoEncontradoException ex) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AlreadyExists.class)
    public ResponseEntity<ResponseDTO<?>> yaExiste(AlreadyExists ex) {
        return construir(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ResponseDTO<?>> reglaNegocio(ReglaNegocioException ex) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Red de seguridad: llaves foráneas (borrar algo con historial) o unique que no validaste a mano
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseDTO<?>> integridad(DataIntegrityViolationException ex) {
        return construir(HttpStatus.CONFLICT,
                "La operación viola una restricción de la base de datos (registro duplicado o con datos asociados)");
    }

    // JSON mal formado o con formatos inválidos (fechas, horas, enums)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDTO<?>> jsonInvalido(HttpMessageNotReadableException ex) {
        return construir(HttpStatus.BAD_REQUEST,
                "El formato del JSON no es válido. Revisa fechas (yyyy-MM-dd), horas (HH:mm) y valores de enums");
    }

    // Errores de @Valid: indica qué campo falló (ver paso 5)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<?>> validacion(MethodArgumentNotValidException ex) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return construir(HttpStatus.BAD_REQUEST, detalle);
    }

    private ResponseEntity<ResponseDTO<?>> construir(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(new ResponseDTO<>(status.value(), mensaje));
    }
    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ResponseDTO<?>> conflicto(ConflictoException ex) {
        return construir(HttpStatus.CONFLICT, ex.getMessage());
    }
}