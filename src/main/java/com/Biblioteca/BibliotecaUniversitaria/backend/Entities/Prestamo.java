package com.Biblioteca.BibliotecaUniversitaria.backend.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuariobibliotecario;

    @Column(name = "fechaprestamo", nullable = false)
    private LocalDate fechaprestamo;

    @Column(name = "fechadevolucionesperada")
    private LocalDate fechadevolucionesperada;


    @Column(name = "fechadevolucionreal")
    private LocalDate fechadevolucionreal;

    public enum EstadoPrestamo {activo, devuelto}
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPrestamo estado = EstadoPrestamo.activo;

    @PrePersist
    public void prePersist() {
        if (this.fechaprestamo == null) {
            this.fechaprestamo = LocalDate.now();
        }
    }
}

