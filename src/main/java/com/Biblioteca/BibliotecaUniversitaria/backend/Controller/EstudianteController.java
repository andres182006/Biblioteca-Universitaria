package com.Biblioteca.BibliotecaUniversitaria.backend.Controller;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.EstudianteDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarEstudianteRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearEstudianteRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl.EstudianteService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.utils.ResponseDTOService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
// Por esto (ajustando el puerto según donde corra tu frontend):
// cambiar cuando utilice angular@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
@RequestMapping("/Estudiante")
@RequiredArgsConstructor
public class EstudianteController {
    private final EstudianteService estudianteService;
    private final ResponseDTOService responseDTOService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BIBLIOTECARIO')")
    @PostMapping("/crear")
    public ResponseEntity<?> crear(@Valid@RequestBody CrearEstudianteRequest crearEstudianteRequest){
        return responseDTOService.response(
                estudianteService.crear(crearEstudianteRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BIBLIOTECARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @Valid@RequestBody ActualizarEstudianteRequest actualizarEstudianteRequest){
        return responseDTOService.response(
                estudianteService.actualizar(id, actualizarEstudianteRequest), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BIBLIOTECARIO')")
    @GetMapping("/{documento}")
    public ResponseEntity<?> leer(@PathVariable String documento) {
        return responseDTOService.response(
                estudianteService.leer(documento), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BIBLIOTECARIO')")
    @GetMapping
    public ResponseEntity<?> listar(){
        return responseDTOService.response(
                estudianteService.listar(), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BIBLIOTECARIO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        estudianteService.eliminar(id);
        return responseDTOService.response(HttpStatus.NO_CONTENT);
    }
}
