package com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.LibroDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Libro;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.LibroRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarLibroRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearLibroRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.ILibroService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.AlreadyExists;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.EstudianteNoEncontradoException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibroService implements ILibroService {
    private final LibroRepository libroRepository;
    private final ModelMapper modelMapper;
    //crear
    @Override
    @Transactional(rollbackOn =  Exception.class)
    public LibroDTO crear(CrearLibroRequest crearLibroRequest) {
        Optional<Libro> libroOptional = libroRepository.findByTitulo(crearLibroRequest.getTitulo());
        //validar titulo
        if (libroOptional.isPresent()) {
            throw new AlreadyExists("Libro Ya existe"
            + crearLibroRequest.getTitulo());
        }
        Libro newlibro = new Libro();
        newlibro.setTitulo(crearLibroRequest.getTitulo());
        newlibro.setAutor(crearLibroRequest.getAutor());
        newlibro.setIsbn(crearLibroRequest.getIsbn());
        newlibro.setCategoria(crearLibroRequest.getCategoria());
        newlibro.setCantidadtotal(crearLibroRequest.getCantidadtotal());
        return modelMapper.map(libroRepository.save(newlibro), LibroDTO.class);
    }
//actualizar
    @Override
    @Transactional(rollbackOn =  Exception.class)
    public LibroDTO actualizar(Long id, ActualizarLibroRequest actualizarLibroRequest) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new EstudianteNoEncontradoException(
                        "no encontrado id: " + id));
        libro.setTitulo(actualizarLibroRequest.getTitulo());
        libro.setAutor(actualizarLibroRequest.getAutor());
        libro.setIsbn(actualizarLibroRequest.getIsbn());
        libro.setCategoria(actualizarLibroRequest.getCategoria());
        libro.setCantidadtotal(actualizarLibroRequest.getCantidadtotal());
        libro.setCantidaddisponible(actualizarLibroRequest.getCantidaddisponible());
        return modelMapper.map(libroRepository.save(libro), LibroDTO.class);
    }
//leer
    @Override
    public LibroDTO leer(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new EstudianteNoEncontradoException(
                        "no encontrado id: " + id));
        return modelMapper.map(libro, LibroDTO.class);
    }

    @Override
    public List<LibroDTO> listar() {
        return libroRepository.findAll()
                .stream()
                .map(libro -> modelMapper.map(libro, LibroDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackOn =  Exception.class)
    public void eliminar(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new EstudianteNoEncontradoException("no encontrado id: " + id);
        }
        libroRepository.deleteById(id);

    }
}
