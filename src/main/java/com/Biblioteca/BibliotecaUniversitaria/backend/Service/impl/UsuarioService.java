package com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.UsuarioDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Usuario;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.UsuarioRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarUsuarioRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearUsuarioRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.IUsuarioService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.AlreadyExists;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.EstudianteNoEncontradoException;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.RecursoNoEncontradoException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    //crear
    @Override
    @Transactional(rollbackOn =  Exception.class)
    public UsuarioDTO crear(CrearUsuarioRequest crearUsuarioRequest) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByDocumento(crearUsuarioRequest.getDocumento());
        if (usuarioOptional.isPresent()) {
            throw new AlreadyExists("Error al crear el usuario: el numero de documento ya existe"
            + crearUsuarioRequest.getDocumento());
        }
        if (usuarioRepository.findByCorreo(crearUsuarioRequest.getCorreo()).isPresent()) {
            throw new AlreadyExists("no se puede crear un correo ya existente");
        }
        Usuario newusuario = new Usuario();
        newusuario.setDocumento(crearUsuarioRequest.getDocumento());
        newusuario.setNombrecompleto(crearUsuarioRequest.getNombrecompleto());
        newusuario.setCorreo(crearUsuarioRequest.getCorreo());
        newusuario.setContrasena(passwordEncoder.encode(crearUsuarioRequest.getContrasena()));
        newusuario.setRol(Usuario.RolUsuario.profesor);
        return modelMapper.map(usuarioRepository.save(newusuario), UsuarioDTO.class);
    }
    //ACTUALIZAR
    @Override
    @Transactional(rollbackOn =  Exception.class)
    public UsuarioDTO actualizar(Long id, ActualizarUsuarioRequest ActualizarUsuarioRequest) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El usuario no existe: " + id));
        if (!usuario.getCorreo().equals(ActualizarUsuarioRequest.getCorreo())
                && usuarioRepository.findByCorreo(ActualizarUsuarioRequest.getCorreo()).isPresent()) {
            throw new AlreadyExists("Error: el correo ya está registrado: "
                    + ActualizarUsuarioRequest.getCorreo());
        }
        usuario.setNombrecompleto(ActualizarUsuarioRequest.getNombrecompleto());
        usuario.setCorreo(ActualizarUsuarioRequest.getCorreo());
        return modelMapper.map(usuarioRepository.save(usuario), UsuarioDTO.class);
    }
    //leer
    @Override
    public UsuarioDTO leerid(String documento) {
        Usuario usuario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El usuario no existe: " + documento));
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    @Override
    public List<UsuarioDTO> leer() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                .collect(Collectors.toList());
    }

}
