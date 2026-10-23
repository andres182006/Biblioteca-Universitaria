package com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions;

import com.Biblioteca.BibliotecaUniversitaria.backend.base.configs.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
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

    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ResponseDTO<?>> conflicto(ConflictoException ex) {
        return construir(HttpStatus.CONFLICT, ex.getMessage());
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

    // Errores de @Valid: indica qué campo falló
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<?>> validacion(MethodArgumentNotValidException ex) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return construir(HttpStatus.BAD_REQUEST, detalle);
    }

    // Captura parámetros requeridos faltantes en la petición (devuelve HTTP 400)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDTO<?>> handleMissingParams(MissingServletRequestParameterException ex) {
        String mensaje = String.format("El parámetro requerido '%s' no está presente", ex.getParameterName());
        return construir(HttpStatus.BAD_REQUEST, mensaje);
    }

    // Handler genérico de respaldo para cualquier error inesperado (devuelve HTTP 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<?>> handleGeneralException(Exception ex) {
        log.error("error inesperado", ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error interno en el servidor: " + ex.getMessage());
    }

    private ResponseEntity<ResponseDTO<?>> construir(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(new ResponseDTO<>(status.value(), mensaje));
    }
    @ExceptionHandler(AccessDeniedException.class)   // org.springframework.security.access
    public ResponseEntity<ResponseDTO<?>> accesoDenegado(AccessDeniedException ex) {
        return construir(HttpStatus.FORBIDDEN, "No tienes permisos para realizar esta acción");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)   // org.springframework.web.method.annotation
    public ResponseEntity<ResponseDTO<?>> tipoInvalido(MethodArgumentTypeMismatchException ex) {
        return construir(HttpStatus.BAD_REQUEST, "El valor del parámetro '" + ex.getName() + "' no es válido");
    }
}