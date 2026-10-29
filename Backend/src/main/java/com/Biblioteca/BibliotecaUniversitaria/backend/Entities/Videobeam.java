package com.Biblioteca.BibliotecaUniversitaria.backend.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "videobeam")
public class Videobeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idvideobeam")
    private Long id;

    @Column(name = "codigo", length = 20, nullable = false, unique = true)
    private String codigo;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    public enum EstadoVideobeam {disponible, reservado, mantenimiento}
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoVideobeam estado = EstadoVideobeam.disponible;
}
