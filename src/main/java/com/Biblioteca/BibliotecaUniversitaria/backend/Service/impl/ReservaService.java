package com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.ReservaDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Reserva;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Usuario;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Videobeam;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.ReservaRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.UsuarioRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.VideobeamRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Actualizar.ActualizarReservaRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearReservaRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.IReservaService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.AlreadyExists;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.EstudianteNoEncontradoException;
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
    public ReservaDTO crear(CrearReservaRequest crearReservaRequest) {
        // 1. Validar el horario
        if (!crearReservaRequest.getHorainicio().isBefore(crearReservaRequest.getHorafin())) {
            throw new EstudianteNoEncontradoException("La hora de inicio debe ser anterior a la hora de fin");
        }
        if (crearReservaRequest.getFechareserva().equals(LocalDate.now())
                && crearReservaRequest.getHorainicio().isBefore(LocalTime.now())) {
            throw new EstudianteNoEncontradoException("No se puede reservar en una hora que ya pasó");
        }
        // 2. Validar que sea un profesor (RNF7)
        Usuario profesor = usuarioRepository.findById(crearReservaRequest.getIdusuarioprofesor())
                .orElseThrow(() -> new EstudianteNoEncontradoException(
                        "Usuario no encontrado con id: " + crearReservaRequest.getIdusuarioprofesor()));
        if (profesor.getRol() != Usuario.RolUsuario.profesor) {
            throw new EstudianteNoEncontradoException(
                    "Solo los usuarios con rol profesor pueden reservar videobeams");
        }
        // 3. Buscar el videobeam bloqueando su fila hasta terminar la transacción
        Videobeam videobeam = videobeamRepository.findByIdForUpdate(crearReservaRequest.getIdvideobeam())
                .orElseThrow(() -> new EstudianteNoEncontradoException(
                        "Videobeam no encontrado con id: " + crearReservaRequest.getIdvideobeam()));
        if (videobeam.getEstado() == Videobeam.EstadoVideobeam.mantenimiento) {
            throw new EstudianteNoEncontradoException("El videobeam está en mantenimiento");
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
    public ReservaDTO actualizar(Long id, ActualizarReservaRequest actualizarReservaRequest) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new EstudianteNoEncontradoException(
                        "Reserva no encontrada con id: " + id));

        // 1. Solo se modifican reservas pendientes
        if (reserva.getEstado() != Reserva.EstadoReserva.pendiente) {
            throw new EstudianteNoEncontradoException(
                    "Solo se pueden modificar reservas en estado pendiente");
        }

        // 2. Permisos: el profesor solo modifica las suyas
        Usuario usuario = usuarioRepository.findById(actualizarReservaRequest.getIdusuario())
                .orElseThrow(() -> new EstudianteNoEncontradoException(
                        "Usuario no encontrado con id: " + actualizarReservaRequest.getIdusuario()));
        boolean esDuenio = reserva.getUsuarioprofesor().getId().equals(usuario.getId());
        if (usuario.getRol() == Usuario.RolUsuario.profesor && !esDuenio) {
            throw new EstudianteNoEncontradoException(
                    "Un profesor solo puede modificar sus propias reservas");
        }

        // 3. Validar el horario
        validarHorario(actualizarReservaRequest.getFechareserva(), actualizarReservaRequest.getHorainicio(), actualizarReservaRequest.getHorafin());

        // 4. Videobeam con bloqueo de fila
        Videobeam videobeam = videobeamRepository.findByIdForUpdate(actualizarReservaRequest.getIdvideobeam())
                .orElseThrow(() -> new EstudianteNoEncontradoException(
                        "Videobeam no encontrado con id: " + actualizarReservaRequest.getIdvideobeam()));
        if (videobeam.getEstado() == Videobeam.EstadoVideobeam.mantenimiento) {
            throw new EstudianteNoEncontradoException("El videobeam está en mantenimiento");
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
                .orElseThrow(()-> new EstudianteNoEncontradoException(
                        "Reserva no encontrado con id: " + id));
        return modelMapper.map(reserva, ReservaDTO.class);
    }

    @Override
    public List<ReservaDTO> listar() {
        return reservaRepository.findAll()
                .stream()
                .map(reserva -> modelMapper.map(reserva, ReservaDTO.class))
                .collect(Collectors.toList());
    }

    // Se usa en crear y en actualizar, para no repetir las validaciones
    private void validarHorario(LocalDate fecha, LocalTime inicio, LocalTime fin) {
        if (!inicio.isBefore(fin)) {
            throw new EstudianteNoEncontradoException("La hora de inicio debe ser anterior a la hora de fin");
        }
        if (fecha.equals(LocalDate.now()) && inicio.isBefore(LocalTime.now())) {
            throw new EstudianteNoEncontradoException("No se puede reservar en una hora que ya pasó");
        }
    }
    }

