package com.Biblioteca.BibliotecaUniversitaria.backend.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "idusuario")
    private Long id;

    @Column(name = "nombrecompleto", length = 120, nullable = false)
    private String nombrecompleto;

    @Column(name = "documento", length = 20, nullable = false, unique = true)
    private String documento;

    @Column(name = "correo", length = 120, nullable = false, unique = true)
    private String correo;

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    public enum RolUsuario { administrador, bibliotecario, profesor, estudiante }
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private RolUsuario rol;

    @Column(name = "fecharegistro", updatable = false)
    private LocalDateTime fecharegistro;

    @PrePersist
    public void prePersist() {
        if (this.fecharegistro == null) {
            this.fecharegistro = LocalDateTime.now();
        }
    }
}
