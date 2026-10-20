package com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.VideobeamDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Videobeam;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.ReservaRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.VideobeamRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearVideombeamRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.IReservaService;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.IVideonbeamService;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.AlreadyExists;
import com.Biblioteca.BibliotecaUniversitaria.backend.base.exceptions.ConflictoException;
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
public class VideobeamService implements IVideonbeamService {
    private final VideobeamRepository videobeamRepository;
    private final ModelMapper modelMapper;
    private final ReservaRepository  reservaRepository;

    //crear
    @Override
    @Transactional(rollbackOn =  Exception.class)
    public VideobeamDTO crear(CrearVideombeamRequest crearVideombeamRequest) {
        Optional<Videobeam> videobeamOptional = videobeamRepository.findByCodigo(crearVideombeamRequest.getCodigo());
        //validar por codigo
        if (videobeamOptional.isPresent()) {
            throw new AlreadyExists("el codigo ya existe "
            + crearVideombeamRequest.getCodigo());
        }
        Videobeam newVideobeam = new Videobeam();
        newVideobeam.setCodigo(crearVideombeamRequest.getCodigo());
        newVideobeam.setDescripcion(crearVideombeamRequest.getDescripcion());
        newVideobeam.setEstado(crearVideombeamRequest.getEstado());
        return modelMapper.map(videobeamRepository.save(newVideobeam), VideobeamDTO.class);
    }
    //actualizar
    @Override
    @Transactional(rollbackOn =  Exception.class)
    public VideobeamDTO actualizar(Long id, CrearVideombeamRequest crearVideombeamRequest) {
        Videobeam videobeam = videobeamRepository.findById(id)
                .orElseThrow(() -> new EstudianteNoEncontradoException(
                        "Video no encontrado" + id));
        videobeam.setDescripcion(crearVideombeamRequest.getDescripcion());
        videobeam.setEstado(crearVideombeamRequest.getEstado());
        videobeam.setCodigo(crearVideombeamRequest.getCodigo());
        videobeam.setEstado(crearVideombeamRequest.getEstado());
        return modelMapper.map(videobeamRepository.save(videobeam), VideobeamDTO.class);
    }
    //leer
    @Override
    public VideobeamDTO leer(Long id) {
        Videobeam videobeam = videobeamRepository.findById(id)
                .orElseThrow(() -> new EstudianteNoEncontradoException(
                        "video no encontrado" + id));
        return modelMapper.map(videobeam, VideobeamDTO.class);
    }

    @Override
    public List<VideobeamDTO> listar() {
        return videobeamRepository.findAll()
                .stream()
                .map(videobeam ->  modelMapper.map(videobeam, VideobeamDTO.class))
                .collect(Collectors.toList());
    }
    //eliminar
    @Override
    @Transactional(rollbackOn =  Exception.class)
    public void eliminar(Long id) {
        if (!videobeamRepository.existsById(id)) {
            throw new EstudianteNoEncontradoException("video no encontrado"  + id);
        }
        if (reservaRepository.existsByVideobeamId(id)){
            throw new ConflictoException("no se puede eliminar porque el videobeam tiene una reserva registrada");
        }
        videobeamRepository.deleteById(id);

    }
}
