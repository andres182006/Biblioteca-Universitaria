package com.Biblioteca.BibliotecaUniversitaria.backend.Controller;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.ReservaDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarReservaRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearReservaRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl.ReservaService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.utils.ResponseDTOService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/Reserva")
@RequiredArgsConstructor
public class ReservaController {
    private final ReservaService reservaService;
    private final ResponseDTOService responseDTOService;

    @PostMapping("/crear")
    public ResponseEntity<ReservaDTO> crearReserva(@Valid @RequestBody CrearReservaRequest crearReservaRequest,
                                                   BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return (ResponseEntity<ReservaDTO>) responseDTOService.response(HttpStatus.BAD_REQUEST);
        }else {
            return (ResponseEntity<ReservaDTO>)  responseDTOService.response(
                    reservaService.crear(crearReservaRequest), HttpStatus.CREATED);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody ActualizarReservaRequest request,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return responseDTOService.response(HttpStatus.BAD_REQUEST);
        }
        return responseDTOService.response(reservaService.actualizar(id, request), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> leer(@PathVariable Long id) {
        return responseDTOService.response(
                reservaService.leerid(id), HttpStatus.OK);

    }
    @GetMapping
    public ResponseEntity<?> listar(){
        return responseDTOService.response(
                reservaService.listar(), HttpStatus.OK);
    }

}
