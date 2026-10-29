package com.Biblioteca.BibliotecaUniversitaria.backend.Controller;

import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.RechazarPrestamoRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearPrestamoRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.IPrestamoService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.utils.ResponseDTOService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
// Por esto (ajustando el puerto según donde corra tu frontend):
// cambiar cuando utilice angular@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
@RequestMapping("/Prestamo")
@RequiredArgsConstructor
public class PrestamoController {

    private final IPrestamoService prestamoService;
    private final ResponseDTOService responseDTOService;

    @PreAuthorize("hasRole('ESTUDIANTE')")
    @PostMapping("/solicitar")
    public ResponseEntity<?> solicitar(@Valid @RequestBody CrearPrestamoRequest request,
                                       Authentication authentication) {
        return responseDTOService.response(
                prestamoService.solicitar(request, authentication.getName()), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ESTUDIANTE')")
    @GetMapping("/mis-prestamos")
    public ResponseEntity<?> misPrestamos(Authentication authentication) {
        return responseDTOService.response(
                prestamoService.listarMisPrestamos(authentication.getName()), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ESTUDIANTE')")
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id, Authentication authentication) {
        return responseDTOService.response(
                prestamoService.cancelar(id, authentication.getName()), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','BIBLIOTECARIO')")
    @GetMapping("/solicitudes")
    public ResponseEntity<?> listarSolicitudes() {
        return responseDTOService.response(prestamoService.listarSolicitudes(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','BIBLIOTECARIO')")
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobar(@PathVariable Long id, Authentication authentication) {
        return responseDTOService.response(
                prestamoService.aprobar(id, authentication.getName()), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','BIBLIOTECARIO')")
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazar(@PathVariable Long id,
                                      @Valid @RequestBody(required = false) RechazarPrestamoRequest request,
                                      Authentication authentication) {
        return responseDTOService.response(
                prestamoService.rechazar(id, request, authentication.getName()), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','BIBLIOTECARIO')")
    @PutMapping("/{id}/devolucion")
    public ResponseEntity<?> registrarDevolucion(@PathVariable Long id) {
        return responseDTOService.response(prestamoService.registrarDevolucion(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','BIBLIOTECARIO')")
    @GetMapping("/activos")
    public ResponseEntity<?> listarActivos() {
        return responseDTOService.response(prestamoService.listarActivos(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','BIBLIOTECARIO')")
    @GetMapping("/historial")
    public ResponseEntity<?> listarHistorial() {
        return responseDTOService.response(prestamoService.listarHistorial(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','BIBLIOTECARIO')")
    @GetMapping("/{id}")
    public ResponseEntity<?> leer(@PathVariable Long id) {
        return responseDTOService.response(prestamoService.leer(id), HttpStatus.OK);
    }
}