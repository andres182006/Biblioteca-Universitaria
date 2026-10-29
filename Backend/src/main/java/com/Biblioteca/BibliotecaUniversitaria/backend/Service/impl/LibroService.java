package com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.LibroDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Libro;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.LibroRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.PrestamoRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarLibroRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearLibroRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.ILibroService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibroService implements ILibroService {
    private final LibroRepository libroRepository;
    private final ModelMapper modelMapper;
    private final PrestamoRepository prestamoRepository;
    //crear
    @Override
    @Transactional(rollbackOn =  Exception.class)
    public LibroDTO crear(CrearLibroRequest crearLibroRequest) {
        //validar titulo
        if (libroRepository.findByTitulo(crearLibroRequest.getTitulo()).isPresent()) {
            throw new AlreadyExists("Error: ya existe un libro con el título: "
                    + crearLibroRequest.getTitulo());
        }
        String isbn = crearLibroRequest.getIsbn();
        if (isbn != null && !isbn.isBlank()
                && libroRepository.findByIsbn(isbn).isPresent()) {
            throw new AlreadyExists("Error: ya existe un libro con el ISBN: " + isbn);
        }
        Libro newlibro = new Libro();
        newlibro.setTitulo(crearLibroRequest.getTitulo());
        newlibro.setAutor(crearLibroRequest.getAutor());
        newlibro.setIsbn(isbn == null || isbn.isBlank() ? null : isbn);
        newlibro.setCategoria(crearLibroRequest.getCategoria());
        newlibro.setCantidadtotal(crearLibroRequest.getCantidadtotal());
        return modelMapper.map(libroRepository.save(newlibro), LibroDTO.class);
    }
//actualizar
    @Override
    @Transactional(rollbackOn =  Exception.class)
    public LibroDTO actualizar(Long id, ActualizarLibroRequest actualizarLibroRequest) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "no encontrado id: " + id));
        if (!libro.getTitulo().equals(actualizarLibroRequest.getTitulo())
                && libroRepository.findByTitulo(actualizarLibroRequest.getTitulo()).isPresent()) {
            throw new AlreadyExists("Error: ya existe un libro con el título: "
                    + actualizarLibroRequest.getTitulo());
        }
        if (actualizarLibroRequest.getIsbn() != null && !actualizarLibroRequest.getIsbn().isBlank()
                && !actualizarLibroRequest.getIsbn().equals(libro.getIsbn())
                && libroRepository.findByIsbn(actualizarLibroRequest.getIsbn()).isPresent()) {
            throw new AlreadyExists("Error: ya existe un libro con el ISBN: "
                    + actualizarLibroRequest.getIsbn());
        }
        libro.setTitulo(actualizarLibroRequest.getTitulo());
        libro.setAutor(actualizarLibroRequest.getAutor());
        String isbn = actualizarLibroRequest.getIsbn();
        libro.setIsbn(isbn == null || isbn.isBlank() ? null : isbn);
        libro.setCategoria(actualizarLibroRequest.getCategoria());
        int prestadas = libro.getCantidadtotal() - libro.getCantidaddisponible();
        if (actualizarLibroRequest.getCantidadtotal() < prestadas) {
            throw new ReglaNegocioException(
                    "La cantidad total no puede ser menor a las copias prestadas: " + prestadas);
        }
        libro.setCantidadtotal(actualizarLibroRequest.getCantidadtotal());
        libro.setCantidaddisponible(actualizarLibroRequest.getCantidadtotal() - prestadas);
        return modelMapper.map(libroRepository.save(libro), LibroDTO.class);
    }
//leer
    @Override
    public LibroDTO leer(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
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
            throw new RecursoNoEncontradoException("no encontrado id: " + id);
        }
        if (prestamoRepository.existsByLibroId(id)) {
            throw new ConflictoException("No se puede eliminar: el libro tiene préstamos registrados");
        }
        libroRepository.deleteById(id);

    }
}
