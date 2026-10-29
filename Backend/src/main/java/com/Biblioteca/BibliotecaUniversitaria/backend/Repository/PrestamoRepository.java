package com.Biblioteca.BibliotecaUniversitaria.backend.Repository;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Prestamo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo,Long> {
    // Bloquea el préstamo al devolverlo, para que dos devoluciones simultáneas no sumen dos copias
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Prestamo p WHERE p.id = :id")
    Optional<Prestamo> findByIdForUpdate(@Param("id") Long id);

    List<Prestamo> findByEstadoOrderByIdDesc(Prestamo.EstadoPrestamo estado);

    List<Prestamo> findAllByOrderByIdDesc();

    // Para validar antes de eliminar un estudiante o un libro
    boolean existsByEstudianteId(Long idEstudiante);

    boolean existsByLibroId(Long idLibro);
    List<Prestamo> findByEstadoOrderByIdAsc(Prestamo.EstadoPrestamo estado);

    List<Prestamo> findByEstudianteIdOrderByIdDesc(Long idEstudiante);

    boolean existsByEstudianteIdAndLibroIdAndEstadoIn(Long idEstudiante, Long idLibro,
                                                      Collection<Prestamo.EstadoPrestamo> estados);
}
