package com.Biblioteca.BibliotecaUniversitaria.backend.Repository;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByDocumento(String documento);
    Optional<Usuario> findByCorreo(String correo);
}
