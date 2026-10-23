package com.Biblioteca.BibliotecaUniversitaria.backend.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "prestamo")
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idprestamo")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idestudiante", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idlibro", nullable = false)
    private Libro libro;

    @ManyToOne
    @JoinColumn(name = "idusuario")
    private Usuario usuariobibliotecario;

    @Column(name = "fechasolicitud", nullable = false, updatable = false)
    private LocalDateTime fechasolicitud;

    @Column(name = "fechaprestamo")
    private LocalDate fechaprestamo;

    @Column(name = "motivorechazo", length = 200)
    private String motivorechazo;

    @Column(name = "fechadevolucionesperada")
    private LocalDate fechadevolucionesperada;

    @Column(name = "fechadevolucionreal")
    private LocalDate fechadevolucionreal;

    public enum EstadoPrestamo {solicitado, activo, devuelto, rechazado, cancelado}
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPrestamo estado = EstadoPrestamo.solicitado;

    @PrePersist
    public void prePersist() {
        if (this.fechasolicitud == null) {
            this.fechasolicitud = LocalDateTime.now();
        }
    }
}

