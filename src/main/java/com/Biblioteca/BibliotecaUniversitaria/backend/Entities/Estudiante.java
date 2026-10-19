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
@Table(name = "estudiante")
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idestudiante")
    private Long id;

    @Column(name = "nombrecompleto",length = 120, nullable = false)
    private String nombrecompleto;

    @Column(name = "documento",length = 20, nullable = false, unique = true)
    private String documento;

    @Column(name = "correo", length = 120)
    private String correo;

    @Column(name = "telefono", length = 120)
    private String telefono;

    // updatable = false evita que la fecha de registro cambie si actualizas otros datos del estudiante más adelante
    @Column(name = "fecharegistro", nullable = false, updatable = false)
    private LocalDateTime fecharegistro;

    // Asigna automáticamente la fecha actual justo antes de insertar el registro en la BD
    @PrePersist
    public void prePersist() {
        this.fecharegistro = LocalDateTime.now();
    }

}
