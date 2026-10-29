package com.Biblioteca.BibliotecaUniversitaria.backend.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "libro")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idlibro")
    private Long id;

    @Column(name = "titulo",length = 200, nullable = false)
    private String titulo;

    @Column(name = "autor", length = 150, nullable = false)
    private String autor;

    @Column(name = "isbn", length = 20, unique = true)
    private String isbn;

    @Column(name = "categoria",length = 80)
    private String categoria;

    @Column(name = "cantidadtotal", nullable = false)
    private Integer cantidadtotal;

    @Column(name = "cantidaddisponible", nullable = false)
    private Integer cantidaddisponible;

    @Column(name = "fecharegistro", nullable = false, updatable = false)
    private LocalDateTime fecharegistro;

    @PrePersist
    public void prePersist() {
        this.fecharegistro = LocalDateTime.now();
        if (this.cantidaddisponible == null) {
            this.cantidaddisponible = this.cantidadtotal;
        }
    }
}
