package com.Biblioteca.BibliotecaUniversitaria.backend.Controller;


import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.LibroDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarLibroRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearLibroRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl.LibroService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.utils.ResponseDTOService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/Libro")
@RequiredArgsConstructor
public class LibroController {
    private final LibroService libroService;
    private final ResponseDTOService  responseDTOService;

    @PostMapping("/Crear")
    public ResponseEntity<?> crear(@Valid @RequestBody CrearLibroRequest crearLibroRequest) {
        return responseDTOService.response(
                libroService.crear(crearLibroRequest), HttpStatus.CREATED);

    }
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @Valid@RequestBody ActualizarLibroRequest actualizarLibroRequest){

        return responseDTOService.response
                (libroService.actualizar(id,actualizarLibroRequest), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> leer(@PathVariable Long id){
        return responseDTOService.response(
                libroService.leer(id), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<?> listar(){
        return responseDTOService.response(
                libroService.listar(), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        libroService.eliminar(id);
        return responseDTOService.response(HttpStatus.NO_CONTENT);
    }
}
