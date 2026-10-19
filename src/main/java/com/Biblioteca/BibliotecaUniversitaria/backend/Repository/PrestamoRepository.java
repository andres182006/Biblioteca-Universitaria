package com.Biblioteca.BibliotecaUniversitaria.backend.Repository;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo,Long> {
}
