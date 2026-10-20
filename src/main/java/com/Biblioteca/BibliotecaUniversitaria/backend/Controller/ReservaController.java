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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/Reserva")
@RequiredArgsConstructor
public class ReservaController {
    private final ReservaService reservaService;
    private final ResponseDTOService responseDTOService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearReserva(@Valid @RequestBody CrearReservaRequest crearReservaRequest){
        return responseDTOService.response(
                reservaService.crear(crearReservaRequest), HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody ActualizarReservaRequest request) {
        return  responseDTOService.response(
                reservaService.actualizar(id,request), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> leer(@PathVariable Long id) {
        return responseDTOService.response(
                reservaService.leerid(id), HttpStatus.OK);

    }
    @GetMapping
    public ResponseEntity<?> listar(@RequestParam Long idusuario) {
        return responseDTOService.response(reservaService.listar(idusuario), HttpStatus.OK);
    }
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id, @RequestParam Long idusuario) {
        return responseDTOService.response(reservaService.cancelar(id, idusuario), HttpStatus.OK);
    }

    @PutMapping("/{id}/entregar")
    public ResponseEntity<?> entregar(@PathVariable Long id, @RequestParam Long idusuario) {
        return responseDTOService.response(reservaService.entregar(id, idusuario), HttpStatus.OK);
    }

    @PutMapping("/{id}/devolver")
    public ResponseEntity<?> devolver(@PathVariable Long id, @RequestParam Long idusuario) {
        return responseDTOService.response(reservaService.devolver(id, idusuario), HttpStatus.OK);
    }
    @GetMapping("/disponibilidad")
    public ResponseEntity<?> disponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horainicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horafin) {
        return responseDTOService.response(
                reservaService.disponibilidad(fecha, horainicio, horafin), HttpStatus.OK);
    }

}
