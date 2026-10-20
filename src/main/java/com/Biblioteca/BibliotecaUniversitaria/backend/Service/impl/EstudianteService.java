package com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.EstudianteDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Estudiante;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Usuario;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.EstudianteRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.PrestamoRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearEstudianteRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.IEstudianteService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.AlreadyExists;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.ConflictoException;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.EstudianteNoEncontradoException;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.RecursoNoEncontradoException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstudianteService implements IEstudianteService {
    private final EstudianteRepository estudianteRepository;
    private final ModelMapper modelMapper;
    private final PrestamoRepository prestamoRepository;

    //crea estudiante
    @Override
    @Transactional(rollbackOn =  Exception.class)
    public EstudianteDTO crear(CrearEstudianteRequest crearEstudianteRequest ) {
        Optional<Estudiante> estudianteOptional = estudianteRepository.findByDocumento(crearEstudianteRequest.getDocumento());
        //Validar documento unico
        if (estudianteOptional.isPresent()){
            throw new AlreadyExists("Error: el documento ya existente"
            + crearEstudianteRequest.getDocumento());
        }

        Estudiante newEstudiante = new Estudiante();
        newEstudiante.setDocumento(crearEstudianteRequest.getDocumento());
        newEstudiante.setCorreo(crearEstudianteRequest.getCorreo());
        newEstudiante.setNombrecompleto(crearEstudianteRequest.getNombrecompleto());
        newEstudiante.setTelefono(crearEstudianteRequest.getTelefono());
        return modelMapper.map(estudianteRepository.save(newEstudiante), EstudianteDTO.class);
    }
    //actualizar
    @Override
    @Transactional(rollbackOn =   Exception.class)
    public EstudianteDTO actualizar(Long id, CrearEstudianteRequest crearEstudianteRequest) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con id" + id));
        //Validar documento unico
        if (!estudiante.getDocumento().equals(crearEstudianteRequest.getDocumento())
                && estudianteRepository.findByDocumento(crearEstudianteRequest.getDocumento()).isPresent()) {
            throw new AlreadyExists("Error: el documento ya existe: "
                    + crearEstudianteRequest.getDocumento());
        }
        estudiante.setNombrecompleto(crearEstudianteRequest.getNombrecompleto());
        estudiante.setDocumento(crearEstudianteRequest.getDocumento());
        estudiante.setCorreo(crearEstudianteRequest.getCorreo());
        estudiante.setTelefono(crearEstudianteRequest.getTelefono());

        return modelMapper.map(estudianteRepository.save(estudiante), EstudianteDTO.class);
    }
    //leer
    @Override
    public EstudianteDTO leer(String documento) {
        Estudiante estudiante = estudianteRepository.findByDocumento(documento)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con ese numero de documento" + documento));
        return modelMapper.map(estudiante, EstudianteDTO.class);
    }

    @Override
    public List<EstudianteDTO> listar() {
        return estudianteRepository.findAll()
                .stream()
                .map(estudiante -> modelMapper.map(estudiante, EstudianteDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackOn =   Exception.class)
    public void eliminar(Long id) {
        if (!estudianteRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Estudiante no encontrado con id" + id);
        }
        if (prestamoRepository.existsByEstudianteId(id)) {
            throw new ConflictoException("No se puede eliminar: el estudiante tiene préstamos registrados");
        }
        estudianteRepository.deleteById(id);

    }
}
