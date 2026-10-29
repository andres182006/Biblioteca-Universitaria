package com.Biblioteca.BibliotecaUniversitaria.backend.Controller;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.ReservaDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarReservaRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearReservaRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl.ReservaService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.utils.ResponseDTOService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@CrossOrigin(origins="*", maxAge=3600)
// Por esto (ajustando el puerto según donde corra tu frontend):
// cambiar cuando utilice angular@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
@RequestMapping("/Reserva")
@RequiredArgsConstructor
public class ReservaController {
    private final ReservaService reservaService;
    private final ResponseDTOService responseDTOService;

    @PreAuthorize("hasRole('PROFESOR')")
    @PostMapping("/crear")
    public ResponseEntity<?> crearReserva(@Valid @RequestBody CrearReservaRequest crearReservaRequest,
                                          Authentication authentication){
        return responseDTOService.response(
                reservaService.crear(crearReservaRequest, authentication.getName()), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR','BIBLIOTECARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody ActualizarReservaRequest request,
                                        Authentication authentication) {
        return  responseDTOService.response(
                reservaService.actualizar(id, request, authentication.getName()), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR','BIBLIOTECARIO')")
    @GetMapping("/{id}")
    public ResponseEntity<?> leer(@PathVariable Long id) {
        return responseDTOService.response(
                reservaService.leerid(id), HttpStatus.OK);

    }
    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR','BIBLIOTECARIO')")
    @GetMapping
    public ResponseEntity<?> listar(Authentication authentication) {
        return responseDTOService.response(reservaService.listar(authentication.getName()), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR','BIBLIOTECARIO')")
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id, Authentication authentication) {
        return responseDTOService.response(reservaService.cancelar(id, authentication.getName()), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BIBLIOTECARIO')")
    @PutMapping("/{id}/entregar")
    public ResponseEntity<?> entregar(@PathVariable Long id, Authentication authentication) {
        return responseDTOService.response(reservaService.entregar(id, authentication.getName()), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BIBLIOTECARIO')")
    @PutMapping("/{id}/devolver")
    public ResponseEntity<?> devolver(@PathVariable Long id, Authentication authentication) {
        return responseDTOService.response(reservaService.devolver(id, authentication.getName()), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR','BIBLIOTECARIO')")
    @GetMapping("/disponibilidad")
    public ResponseEntity<?> disponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horainicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horafin) {
        return responseDTOService.response(
                reservaService.disponibilidad(fecha, horainicio, horafin), HttpStatus.OK);
    }

}