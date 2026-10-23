package com.Biblioteca.BibliotecaUniversitaria.backend.Repository;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByDocumento(String documento);
    Optional<Estudiante> findByUsuarioCorreo(String correo);
}
