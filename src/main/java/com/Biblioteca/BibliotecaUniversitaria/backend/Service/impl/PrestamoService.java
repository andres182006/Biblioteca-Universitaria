package com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.PrestamoDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Estudiante;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Libro;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Prestamo;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Usuario;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.EstudianteRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.LibroRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.PrestamoRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.UsuarioRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearPrestamoRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.IPrestamoService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.ConflictoException;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.RecursoNoEncontradoException;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.ReglaNegocioException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrestamoService implements IPrestamoService {
    private final PrestamoRepository prestamoRepository;
    private final EstudianteRepository estudianteRepository;
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public PrestamoDTO crear(CrearPrestamoRequest request) {

        // 1. El estudiante debe existir
        Estudiante estudiante = estudianteRepository.findById(request.getIdestudiante())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con id: " + request.getIdestudiante()));

        // 2. Quien registra debe ser bibliotecario (HU04 y Caso de Uso 8)
        Usuario usuario = usuarioRepository.findById(request.getIdusuario())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id: " + request.getIdusuario()));
        if (usuario.getRol() != Usuario.RolUsuario.bibliotecario) {
            throw new ReglaNegocioException("Solo un bibliotecario puede registrar préstamos");
        }

        // 3. Se busca el libro bloqueando su fila hasta terminar la transacción
        Libro libro = libroRepository.findByIdForUpdate(request.getIdlibro())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Libro no encontrado con id: " + request.getIdlibro()));

        // 4. Debe quedar al menos una copia disponible
        if (libro.getCantidaddisponible() <= 0) {
            throw new ConflictoException("No hay copias disponibles de este libro");
        }

        // 5. Se descuenta una copia y se guarda el préstamo
        // (fechaprestamo y el estado activo los asigna la entidad)
        libro.setCantidaddisponible(libro.getCantidaddisponible() - 1);
        libroRepository.save(libro);

        Prestamo prestamo = new Prestamo();
        prestamo.setEstudiante(estudiante);
        prestamo.setLibro(libro);
        prestamo.setUsuariobibliotecario(usuario);

        return aDTO(prestamoRepository.save(prestamo));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public PrestamoDTO registrarDevolucion(Long id) {

        // Se bloquea el préstamo para que dos devoluciones simultáneas no sumen dos copias
        Prestamo prestamo = prestamoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Préstamo no encontrado con id: " + id));

        if (prestamo.getEstado() != Prestamo.EstadoPrestamo.activo) {
            throw new ConflictoException("El préstamo ya fue devuelto");
        }

        Libro libro = libroRepository.findByIdForUpdate(prestamo.getLibro().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Libro no encontrado con id: " + prestamo.getLibro().getId()));

        prestamo.setFechadevolucionreal(LocalDate.now());
        prestamo.setEstado(Prestamo.EstadoPrestamo.devuelto);
        libro.setCantidaddisponible(libro.getCantidaddisponible() + 1);

        libroRepository.save(libro);
        return aDTO(prestamoRepository.save(prestamo));
    }

    @Override
    public PrestamoDTO leer(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Préstamo no encontrado con id: " + id));
        return aDTO(prestamo);
    }

    @Override
    public List<PrestamoDTO> listarActivos() {
        return prestamoRepository.findByEstadoOrderByIdDesc(Prestamo.EstadoPrestamo.activo)
                .stream()
                .map(this::aDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrestamoDTO> listarHistorial() {
        return prestamoRepository.findAllByOrderByIdDesc()
                .stream()
                .map(this::aDTO)
                .collect(Collectors.toList());
    }

    // Arma el DTO y calcula los días: hasta hoy si sigue activo, hasta la devolución si ya volvió
    private PrestamoDTO aDTO(Prestamo prestamo) {
        PrestamoDTO dto = modelMapper.map(prestamo, PrestamoDTO.class);
        LocalDate fin = prestamo.getFechadevolucionreal() != null
                ? prestamo.getFechadevolucionreal()
                : LocalDate.now();
        dto.setDiasenprestamo(ChronoUnit.DAYS.between(prestamo.getFechaprestamo(), fin));
        return dto;
    }
}

