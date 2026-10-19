package com.Biblioteca.BibliotecaUniversitaria.backend.Repository;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva,Long> {
    @Query("""
    SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
    FROM Reserva r
    WHERE r.videobeam.id = :idVideobeam
      AND r.fechareserva = :fecha
      AND r.estado <> :estadoExcluido
      AND r.horainicio < :horafin
      AND r.horafin > :horainicio
    """)
    boolean existeTraslape(@Param("idVideobeam") Long idVideobeam,
                           @Param("fecha") LocalDate fecha,
                           @Param("horainicio") LocalTime horainicio,
                           @Param("horafin") LocalTime horafin,
                           @Param("estadoExcluido") Reserva.EstadoReserva estadoExcluido);

    @Query("""
    SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
    FROM Reserva r
    WHERE r.videobeam.id = :idVideobeam
      AND r.id <> :idReserva
      AND r.fechareserva = :fecha
      AND r.estado <> :estadoExcluido
      AND r.horainicio < :horafin
      AND r.horafin > :horainicio
    """)
    boolean existeTraslapeExcluyendo(@Param("idReserva") Long idReserva,
                                     @Param("idVideobeam") Long idVideobeam,
                                     @Param("fecha") LocalDate fecha,
                                     @Param("horainicio") LocalTime horainicio,
                                     @Param("horafin") LocalTime horafin,
                                     @Param("estadoExcluido") Reserva.EstadoReserva estadoExcluido);


    Optional<Reserva> findById(Long id);
}
