package com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.EstudianteDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Estudiante;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Usuario;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.EstudianteRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.PrestamoRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.UsuarioRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarEstudianteRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearEstudianteRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.IEstudianteService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.AlreadyExists;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.ConflictoException;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.EstudianteNoEncontradoException;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.RecursoNoEncontradoException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstudianteService implements IEstudianteService {
    private final EstudianteRepository estudianteRepository;
    private final ModelMapper modelMapper;
    private final PrestamoRepository prestamoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    //crea estudiante
    @Override
    @Transactional(rollbackOn =  Exception.class)
    public EstudianteDTO crear(CrearEstudianteRequest request ) {
        if (estudianteRepository.findByDocumento(request.getDocumento()).isPresent()
                || usuarioRepository.findByDocumento(request.getDocumento()).isPresent()) {
            throw new AlreadyExists("Error: ya existe un registro con el documento: "
                    + request.getDocumento());
        }
        if (usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
            throw new AlreadyExists("Error: ya existe un usuario con el correo: "
                    + request.getCorreo());
        }

        Usuario cuenta = new Usuario();
        cuenta.setNombrecompleto(request.getNombrecompleto());
        cuenta.setDocumento(request.getDocumento());
        cuenta.setCorreo(request.getCorreo());
        cuenta.setContrasena(passwordEncoder.encode(request.getContrasena()));
        cuenta.setRol(Usuario.RolUsuario.estudiante);
        usuarioRepository.save(cuenta);

        Estudiante nuevo = new Estudiante();
        nuevo.setDocumento(request.getDocumento());
        nuevo.setCorreo(request.getCorreo());
        nuevo.setNombrecompleto(request.getNombrecompleto());
        nuevo.setTelefono(request.getTelefono());
        nuevo.setUsuario(cuenta);
        return modelMapper.map(estudianteRepository.save(nuevo), EstudianteDTO.class);
    }
    //actualizar
    @Override
    @Transactional(rollbackOn = Exception.class)
    public EstudianteDTO actualizar(Long id, ActualizarEstudianteRequest request) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con id: " + id));

        if (!Objects.equals(estudiante.getCorreo(), request.getCorreo())
                && usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
            throw new AlreadyExists("Error: ya existe un usuario con el correo: "
                    + request.getCorreo());
        }

        estudiante.setNombrecompleto(request.getNombrecompleto());
        estudiante.setCorreo(request.getCorreo());
        estudiante.setTelefono(request.getTelefono());
        if (estudiante.getUsuario() != null) {
            estudiante.getUsuario().setNombrecompleto(request.getNombrecompleto());
            estudiante.getUsuario().setCorreo(request.getCorreo());
        }
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
    @Transactional(rollbackOn = Exception.class)
    public void eliminar(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con id: " + id));
        if (prestamoRepository.existsByEstudianteId(id)) {
            throw new ConflictoException("No se puede eliminar: el estudiante tiene préstamos registrados");
        }
        Usuario cuenta = estudiante.getUsuario();
        estudianteRepository.delete(estudiante);
        if (cuenta != null) {
            usuarioRepository.delete(cuenta);
        }
    }
}
