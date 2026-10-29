package com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.ReservaDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.VideobeamDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Reserva;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Usuario;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Videobeam;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.ReservaRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.UsuarioRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.VideobeamRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarReservaRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearReservaRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.IReservaService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService implements IReservaService {
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VideobeamRepository videobeamRepository;
    private final ModelMapper modelMapper;

    //crear
    @Override
    @Transactional(rollbackOn = Exception.class)
    public ReservaDTO crear(CrearReservaRequest crearReservaRequest, String correo) {
        // 1. Validar el horario
        validarHorario(crearReservaRequest.getFechareserva(),
                crearReservaRequest.getHorainicio(),
                crearReservaRequest.getHorafin());
        // 2. Validar que sea un profesor (RNF7)
        Usuario profesor = buscarUsuario(correo);
        if (profesor.getRol() != Usuario.RolUsuario.profesor) {
            throw new ReglaNegocioException(
                    "Solo los usuarios con rol profesor pueden reservar videobeams");
        }
        // 3. Buscar el videobeam bloqueando su fila hasta terminar la transacción
        Videobeam videobeam = videobeamRepository.findByIdForUpdate(crearReservaRequest.getIdvideobeam())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Videobeam no encontrado con id: " + crearReservaRequest.getIdvideobeam()));
        if (videobeam.getEstado() == Videobeam.EstadoVideobeam.mantenimiento) {
            throw new ReglaNegocioException("El videobeam está en mantenimiento");
        }

// 4. Validar que el horario esté libre (RF20)
        boolean ocupado = reservaRepository.existeTraslape(
                videobeam.getId(),
                crearReservaRequest.getFechareserva(),
                crearReservaRequest.getHorainicio(),
                crearReservaRequest.getHorafin(),
                Reserva.EstadoReserva.cancelado);
        if (ocupado) {
            throw new AlreadyExists("El videobeam ya está reservado en ese horario");
        }
// 5. Guardar (el estado pendiente y la fechacreacion los asigna la entidad)
        Reserva reserva = new Reserva();
        reserva.setUsuarioprofesor(profesor);
        reserva.setVideobeam(videobeam);
        reserva.setFechareserva(crearReservaRequest.getFechareserva());
        reserva.setHorainicio(crearReservaRequest.getHorainicio());
        reserva.setHorafin(crearReservaRequest.getHorafin());
        reserva.setMotivo(crearReservaRequest.getMotivo());

        return modelMapper.map(reservaRepository.save(reserva), ReservaDTO.class);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ReservaDTO actualizar(Long id, ActualizarReservaRequest actualizarReservaRequest, String correo) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reserva no encontrada con id: " + id));

        // 1. Solo se modifican reservas pendientes
        if (reserva.getEstado() != Reserva.EstadoReserva.pendiente) {
            throw new ReglaNegocioException(
                    "Solo se pueden modificar reservas en estado pendiente");
        }

        // 2. Permisos: el profesor solo modifica las suyas
        Usuario usuario = buscarUsuario(correo);
        boolean esDuenio = reserva.getUsuarioprofesor().getId().equals(usuario.getId());
        if (usuario.getRol() == Usuario.RolUsuario.profesor && !esDuenio) {
            throw new ReglaNegocioException(
                    "Un profesor solo puede modificar sus propias reservas");
        }

        // 3. Validar el horario
        validarHorario(actualizarReservaRequest.getFechareserva(), actualizarReservaRequest.getHorainicio(), actualizarReservaRequest.getHorafin());

        // 4. Videobeam con bloqueo de fila
        Videobeam videobeam = videobeamRepository.findByIdForUpdate(actualizarReservaRequest.getIdvideobeam())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Videobeam no encontrado con id: " + actualizarReservaRequest.getIdvideobeam()));
        if (videobeam.getEstado() == Videobeam.EstadoVideobeam.mantenimiento) {
            throw new ReglaNegocioException("El videobeam está en mantenimiento");
        }

        // 5. Traslape ignorando esta misma reserva
        boolean ocupado = reservaRepository.existeTraslapeExcluyendo(
                id,
                videobeam.getId(),
                actualizarReservaRequest.getFechareserva(),
                actualizarReservaRequest.getHorainicio(),
                actualizarReservaRequest.getHorafin(),
                Reserva.EstadoReserva.cancelado);
        if (ocupado) {
            throw new AlreadyExists("El videobeam ya está reservado en ese horario");
        }

        // 6. Aplicar los cambios (el profesor, el estado y fechacreacion no se tocan)
        reserva.setVideobeam(videobeam);
        reserva.setFechareserva(actualizarReservaRequest.getFechareserva());
        reserva.setHorainicio(actualizarReservaRequest.getHorainicio());
        reserva.setHorafin(actualizarReservaRequest.getHorafin());
        reserva.setMotivo(actualizarReservaRequest.getMotivo());

        return modelMapper.map(reservaRepository.save(reserva), ReservaDTO.class);
    }

    @Override
    public ReservaDTO leerid(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(()-> new RecursoNoEncontradoException(
                        "Reserva no encontrado con id: " + id));
        return modelMapper.map(reserva, ReservaDTO.class);
    }
    @Override
    public List<ReservaDTO> listar(String correo) {
        Usuario usuario = buscarUsuario(correo);
        List<Reserva> reservas = usuario.getRol() == Usuario.RolUsuario.profesor
                ? reservaRepository.findByUsuarioprofesorIdOrderByFechareservaDescHorainicioDesc(usuario.getId())
                : reservaRepository.findAllByOrderByFechareservaDescHorainicioDesc();
        return reservas.stream()
                .map(r -> modelMapper.map(r, ReservaDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(rollbackOn = Exception.class)
    public ReservaDTO cancelar(Long id, String correo) {
        Reserva reserva = buscarReserva(id);
        Usuario usuario = buscarUsuario(correo);

        boolean esDuenio = reserva.getUsuarioprofesor().getId().equals(usuario.getId());
        if (usuario.getRol() == Usuario.RolUsuario.profesor && !esDuenio) {
            throw new ReglaNegocioException("Un profesor solo puede cancelar sus propias reservas");
        }
        if (reserva.getEstado() != Reserva.EstadoReserva.pendiente) {
            throw new ConflictoException("Solo se pueden cancelar reservas en estado pendiente");
        }

        reserva.setEstado(Reserva.EstadoReserva.cancelado);
        return modelMapper.map(reservaRepository.save(reserva), ReservaDTO.class);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ReservaDTO entregar(Long id, String correo) {
        return cambiarEstado(id, correo,
                Reserva.EstadoReserva.pendiente, Reserva.EstadoReserva.entregado);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ReservaDTO devolver(Long id, String correo) {
        return cambiarEstado(id, correo,
                Reserva.EstadoReserva.entregado, Reserva.EstadoReserva.devuelto);
    }

    private ReservaDTO cambiarEstado(Long id, String correo,
                                     Reserva.EstadoReserva requerido,
                                     Reserva.EstadoReserva nuevo) {
        Reserva reserva = buscarReserva(id);
        Usuario usuario = buscarUsuario(correo);

        if (usuario.getRol() == Usuario.RolUsuario.profesor) {
            throw new ReglaNegocioException(
                    "Solo el bibliotecario o el administrador pueden registrar la entrega y la devolución");
        }
        if (reserva.getEstado() != requerido) {
            throw new ConflictoException("La reserva debe estar en estado " + requerido
                    + " (estado actual: " + reserva.getEstado() + ")");
        }

        reserva.setEstado(nuevo);
        return modelMapper.map(reservaRepository.save(reserva), ReservaDTO.class);
    }

    private Reserva buscarReserva(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reserva no encontrada con id: " + id));
    }

    private Usuario buscarUsuario(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con correo: " + correo));
    }
    @Override
    public List<VideobeamDTO> disponibilidad(LocalDate fecha, LocalTime horainicio, LocalTime horafin) {
        validarHorario(fecha, horainicio, horafin);
        return videobeamRepository.findDisponibles(fecha, horainicio, horafin,
                        Videobeam.EstadoVideobeam.mantenimiento, Reserva.EstadoReserva.cancelado)
                .stream()
                .map(v -> modelMapper.map(v, VideobeamDTO.class))
                .collect(Collectors.toList());
    }
    // Se usa en crear y en actualizar, para no repetir las validaciones
    private void validarHorario(LocalDate fecha, LocalTime inicio, LocalTime fin) {
        if (!inicio.isBefore(fin)) {
            throw new ReglaNegocioException("La hora de inicio debe ser anterior a la hora de fin");
        }
        if (fecha.equals(LocalDate.now()) && inicio.isBefore(LocalTime.now())) {
            throw new ReglaNegocioException("No se puede reservar en una hora que ya pasó");
        }
    }
}