package com.Biblioteca.BibliotecaUniversitaria.backend.Controller;

import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearPrestamoRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.IPrestamoService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.utils.ResponseDTOService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/Prestamo")
@RequiredArgsConstructor
public class PrestamoController {

    private final IPrestamoService prestamoService;
    private final ResponseDTOService responseDTOService;

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@Valid @RequestBody CrearPrestamoRequest request) {
        return responseDTOService.response(prestamoService.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/devolucion")
    public ResponseEntity<?> registrarDevolucion(@PathVariable Long id) {
        return responseDTOService.response(prestamoService.registrarDevolucion(id), HttpStatus.OK);
    }

    @GetMapping("/activos")
    public ResponseEntity<?> listarActivos() {
        return responseDTOService.response(prestamoService.listarActivos(), HttpStatus.OK);
    }

    @GetMapping("/historial")
    public ResponseEntity<?> listarHistorial() {
        return responseDTOService.response(prestamoService.listarHistorial(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> leer(@PathVariable Long id) {
        return responseDTOService.response(prestamoService.leer(id), HttpStatus.OK);
    }
}