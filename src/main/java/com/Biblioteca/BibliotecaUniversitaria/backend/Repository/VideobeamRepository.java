package com.Biblioteca.BibliotecaUniversitaria.backend.Repository;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Prestamo;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Reserva;
import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Videobeam;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideobeamRepository extends JpaRepository<Videobeam, Long> {
    Optional<Videobeam> findByCodigo(String codigo);
    //una consulta que bloquea la fila del equipo mientras dura la transacción:
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Videobeam v WHERE v.id = :id")
    Optional<Videobeam> findByIdForUpdate(@Param("id") Long id);
    @Query("""
    SELECT v FROM Videobeam v
    WHERE v.estado <> :estadoExcluido
      AND NOT EXISTS (
          SELECT r FROM Reserva r
          WHERE r.videobeam = v
            AND r.fechareserva = :fecha
            AND r.estado <> :estadoCancelado
            AND r.horainicio < :horafin
            AND r.horafin > :horainicio)
    ORDER BY v.codigo
    """)
    List<Videobeam> findDisponibles(@Param("fecha") LocalDate fecha,
                                    @Param("horainicio") LocalTime horainicio,
                                    @Param("horafin") LocalTime horafin,
                                    @Param("estadoExcluido") Videobeam.EstadoVideobeam estadoExcluido,
                                    @Param("estadoCancelado") Reserva.EstadoReserva estadoCancelado);

}
