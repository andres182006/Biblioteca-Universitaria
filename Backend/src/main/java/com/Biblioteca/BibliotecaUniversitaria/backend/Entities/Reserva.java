package com.Biblioteca.BibliotecaUniversitaria.backend.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idreserva")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuarioprofesor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idvideobeam", nullable = false)
    private Videobeam videobeam;

    @Column(name = "fechareserva", nullable = false)
    private LocalDate fechareserva;

    @Column(name = "horainicio", nullable = false)
    private LocalTime horainicio;

    @Column(name = "horafin", nullable = false)
    private LocalTime horafin;

    @Column(name = "motivo", length = 200)
    private String motivo;

    public enum EstadoReserva{pendiente, entregado, devuelto, cancelado }
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoReserva estado = EstadoReserva.pendiente;

    @Column(name = "fechacreacion", updatable = false)
    private LocalDateTime fechacreacion;

    @PrePersist
    public void prePersist() {
        if (this.fechacreacion == null) {
            this.fechacreacion = LocalDateTime.now();
        }
    }
}

