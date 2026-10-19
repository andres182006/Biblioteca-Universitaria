package com.Biblioteca.BibliotecaUniversitaria.backend.Controller;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.VideobeamDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearEstudianteRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearVideombeamRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl.VideobeamService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.utils.ResponseDTOService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/Videobeam")
@RequiredArgsConstructor
public class VideobeamController {
    private final VideobeamService videobeamService;
    private final ResponseDTOService responseDTOService;

    @PostMapping("/crear")
    public ResponseEntity<VideobeamDTO> crear(@Valid @RequestBody CrearVideombeamRequest crearEstudianteRequest,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return (ResponseEntity<VideobeamDTO>) responseDTOService.response(HttpStatus.BAD_REQUEST);
        }else {
            return (ResponseEntity<VideobeamDTO>) responseDTOService.response(
                    videobeamService.crear(crearEstudianteRequest), HttpStatus.CREATED);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @Valid@RequestBody CrearVideombeamRequest crearVideombeamRequest,
            BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return responseDTOService.response(HttpStatus.BAD_REQUEST);
        }
        return  responseDTOService.response(
                videobeamService.actualizar(id, crearVideombeamRequest), HttpStatus.OK
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> leer(@PathVariable Long id){
        return responseDTOService.response(
                videobeamService.leer(id), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<?> listar(){
        return responseDTOService.response(
                videobeamService.listar(), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        videobeamService.eliminar(id);
        return responseDTOService.response(HttpStatus.NO_CONTENT);
    }
}
