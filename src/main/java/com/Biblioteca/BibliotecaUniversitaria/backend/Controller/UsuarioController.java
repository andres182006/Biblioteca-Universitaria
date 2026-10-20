package com.Biblioteca.BibliotecaUniversitaria.backend.Controller;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.UsuarioDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarUsuarioRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearUsuarioRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl.UsuarioService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.utils.ResponseDTOService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/Usuario")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final ResponseDTOService responseDTOService;

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@Valid @RequestBody CrearUsuarioRequest crearUsuarioRequest) {
        return responseDTOService.response(
                usuarioService.crear(crearUsuarioRequest), HttpStatus.CREATED);

}
@PutMapping("/{id}")
public ResponseEntity<?> actualizar(@PathVariable Long id,
                                    @Valid @RequestBody ActualizarUsuarioRequest actualizarUsuarioRequest) {
        return responseDTOService.response(
                usuarioService.actualizar(id,actualizarUsuarioRequest), HttpStatus.OK);
}
@GetMapping("/{documento}")
    public ResponseEntity<?> leer(@PathVariable String documento) {
        return responseDTOService.response(
                usuarioService.leerid(documento), HttpStatus.OK);
}
@GetMapping
    public ResponseEntity<?> leer(){
        return responseDTOService.response(
                usuarioService.leer(), HttpStatus.OK);
}

}
