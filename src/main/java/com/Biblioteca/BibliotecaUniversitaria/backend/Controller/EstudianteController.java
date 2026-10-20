package com.Biblioteca.BibliotecaUniversitaria.backend.Controller;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.EstudianteDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearEstudianteRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl.EstudianteService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.utils.ResponseDTOService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/Estudiante")
@RequiredArgsConstructor
public class EstudianteController {
    private final EstudianteService estudianteService;
    private final ResponseDTOService responseDTOService;

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@Valid@RequestBody CrearEstudianteRequest crearEstudianteRequest){
        return responseDTOService.response(
                estudianteService.crear(crearEstudianteRequest), HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @Valid@RequestBody CrearEstudianteRequest crearEstudianteRequest){
        return responseDTOService.response(
                estudianteService.actualizar(id, crearEstudianteRequest), HttpStatus.OK);
    }
    @GetMapping("/{documento}")
    public ResponseEntity<?> leer(@PathVariable String documento) {
        return responseDTOService.response(
                estudianteService.leer(documento), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<?> listar(){
        return responseDTOService.response(
                estudianteService.listar(), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        estudianteService.eliminar(id);
        return responseDTOService.response(HttpStatus.NO_CONTENT);
    }
}
