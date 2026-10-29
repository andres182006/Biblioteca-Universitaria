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
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.RechazarPrestamoRequest;
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

    // El estudiante pide el libro. No se descuenta ninguna copia todavía.
    @Override
    @Transactional(rollbackOn = Exception.class)
    public PrestamoDTO solicitar(CrearPrestamoRequest request, String correo) {
        Estudiante estudiante = buscarEstudiante(correo);

        Libro libro = libroRepository.findById(request.getIdlibro())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Libro no encontrado con id: " + request.getIdlibro()));

        if (libro.getCantidaddisponible() <= 0) {
            throw new ConflictoException("No hay copias disponibles de este libro");
        }

        if (prestamoRepository.existsByEstudianteIdAndLibroIdAndEstadoIn(
                estudiante.getId(), libro.getId(),
                List.of(Prestamo.EstadoPrestamo.solicitado, Prestamo.EstadoPrestamo.activo))) {
            throw new ConflictoException(
                    "Ya tienes una solicitud pendiente o un préstamo activo de este libro");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setEstudiante(estudiante);
        prestamo.setLibro(libro);
        prestamo.setEstado(Prestamo.EstadoPrestamo.solicitado);
        return aDTO(prestamoRepository.save(prestamo));
    }

    // El bibliotecario o administrador concede el préstamo: aquí sí se descuenta la copia.
    @Override
    @Transactional(rollbackOn = Exception.class)
    public PrestamoDTO aprobar(Long id, String correo) {
        Usuario aprobador = buscarUsuario(correo);

        Prestamo prestamo = prestamoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Préstamo no encontrado con id: " + id));
        if (prestamo.getEstado() != Prestamo.EstadoPrestamo.solicitado) {
            throw new ConflictoException("Solo se pueden aprobar solicitudes en estado solicitado");
        }

        Libro libro = libroRepository.findByIdForUpdate(prestamo.getLibro().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Libro no encontrado con id: " + prestamo.getLibro().getId()));
        if (libro.getCantidaddisponible() <= 0) {
            throw new ConflictoException("No hay copias disponibles de este libro");
        }

        libro.setCantidaddisponible(libro.getCantidaddisponible() - 1);
        libroRepository.save(libro);

        prestamo.setEstado(Prestamo.EstadoPrestamo.activo);
        prestamo.setFechaprestamo(LocalDate.now());
        prestamo.setUsuariobibliotecario(aprobador);
        return aDTO(prestamoRepository.save(prestamo));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public PrestamoDTO rechazar(Long id, RechazarPrestamoRequest request, String correo) {
        Usuario revisor = buscarUsuario(correo);

        Prestamo prestamo = prestamoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Préstamo no encontrado con id: " + id));
        if (prestamo.getEstado() != Prestamo.EstadoPrestamo.solicitado) {
            throw new ConflictoException("Solo se pueden rechazar solicitudes en estado solicitado");
        }

        prestamo.setEstado(Prestamo.EstadoPrestamo.rechazado);
        prestamo.setMotivorechazo(request != null ? request.getMotivo() : null);
        prestamo.setUsuariobibliotecario(revisor);
        return aDTO(prestamoRepository.save(prestamo));
    }

    // El estudiante retira su propia solicitud.
    @Override
    @Transactional(rollbackOn = Exception.class)
    public PrestamoDTO cancelar(Long id, String correo) {
        Estudiante estudiante = buscarEstudiante(correo);

        Prestamo prestamo = prestamoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Préstamo no encontrado con id: " + id));
        if (!prestamo.getEstudiante().getId().equals(estudiante.getId())) {
            throw new ReglaNegocioException("Solo puedes cancelar tus propias solicitudes");
        }
        if (prestamo.getEstado() != Prestamo.EstadoPrestamo.solicitado) {
            throw new ConflictoException("Solo se pueden cancelar solicitudes en estado solicitado");
        }

        prestamo.setEstado(Prestamo.EstadoPrestamo.cancelado);
        return aDTO(prestamoRepository.save(prestamo));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public PrestamoDTO registrarDevolucion(Long id) {
        Prestamo prestamo = prestamoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Préstamo no encontrado con id: " + id));
        if (prestamo.getEstado() != Prestamo.EstadoPrestamo.activo) {
            throw new ConflictoException("Solo se pueden devolver préstamos activos (estado actual: "
                    + prestamo.getEstado() + ")");
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
    public List<PrestamoDTO> listarSolicitudes() {
        return prestamoRepository.findByEstadoOrderByIdAsc(Prestamo.EstadoPrestamo.solicitado)
                .stream().map(this::aDTO).collect(Collectors.toList());
    }

    @Override
    public List<PrestamoDTO> listarMisPrestamos(String correo) {
        Estudiante estudiante = buscarEstudiante(correo);
        return prestamoRepository.findByEstudianteIdOrderByIdDesc(estudiante.getId())
                .stream().map(this::aDTO).collect(Collectors.toList());
    }

    @Override
    public List<PrestamoDTO> listarActivos() {
        return prestamoRepository.findByEstadoOrderByIdDesc(Prestamo.EstadoPrestamo.activo)
                .stream().map(this::aDTO).collect(Collectors.toList());
    }

    @Override
    public List<PrestamoDTO> listarHistorial() {
        return prestamoRepository.findAllByOrderByIdDesc()
                .stream().map(this::aDTO).collect(Collectors.toList());
    }

    private Usuario buscarUsuario(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con correo: " + correo));
    }

    private Estudiante buscarEstudiante(String correo) {
        return estudianteRepository.findByUsuarioCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No hay un estudiante asociado al usuario: " + correo));
    }

    private PrestamoDTO aDTO(Prestamo prestamo) {
        PrestamoDTO dto = modelMapper.map(prestamo, PrestamoDTO.class);
        if (prestamo.getFechaprestamo() != null) {
            LocalDate fin = prestamo.getFechadevolucionreal() != null
                    ? prestamo.getFechadevolucionreal()
                    : LocalDate.now();
            dto.setDiasenprestamo(ChronoUnit.DAYS.between(prestamo.getFechaprestamo(), fin));
        }
        return dto;
    }
}