package com.Biblioteca.BibliotecaUniversitaria.backend.Service.impl;

import com.Biblioteca.BibliotecaUniversitaria.backend.DTO.PrestamoDTO;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.PrestamoRepository;
import com.Biblioteca.BibliotecaUniversitaria.backend.Request.Crear.CrearPrestamoRequest;
import com.Biblioteca.BibliotecaUniversitaria.backend.Service.IPrestamoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamoService implements IPrestamoService {
    private final PrestamoRepository prestamoRepository;
    private final ModelMapper modelMapper;

    @Override
    public PrestamoDTO crear(CrearPrestamoRequest crearPrestamoRequest) {
        return null;
    }

    @Override
    public PrestamoDTO registrarDevolucion(Long idDevolucion) {
        return null;
    }

    @Override
    public PrestamoDTO leer(Long idPrestamo) {
        return null;
    }

    @Override
    public List<PrestamoDTO> listaractivos() {
        return List.of();
    }

    @Override
    public List<PrestamoDTO> listarhistorial() {
        return List.of();
    }


}
