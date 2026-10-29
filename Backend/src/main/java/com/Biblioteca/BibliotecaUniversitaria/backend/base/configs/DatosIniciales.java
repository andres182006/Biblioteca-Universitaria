package com.Biblioteca.BibliotecaUniversitaria.backend.base.configs;

import com.Biblioteca.BibliotecaUniversitaria.backend.Entities.Usuario;
import com.Biblioteca.BibliotecaUniversitaria.backend.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner usuariosIniciales(UsuarioRepository usuarioRepository,
                                        PasswordEncoder passwordEncoder,
                                        @Value("${app.usuarios-iniciales.password}") String password) {
        return args -> {
            crearSiNoExiste(usuarioRepository, passwordEncoder, "0000000001",
                    "Administrador", "admin@unicartagena.edu.co", password,
                    Usuario.RolUsuario.administrador);
            crearSiNoExiste(usuarioRepository, passwordEncoder, "0000000002",
                    "Bibliotecario", "biblioteca@unicartagena.edu.co", password,
                    Usuario.RolUsuario.bibliotecario);
        };
    }

    private void crearSiNoExiste(UsuarioRepository repo, PasswordEncoder encoder,
                                 String documento, String nombre, String correo,
                                 String password, Usuario.RolUsuario rol) {
        if (repo.findByDocumento(documento).isPresent()) {
            return;
        }
        Usuario usuario = new Usuario();
        usuario.setDocumento(documento);
        usuario.setNombrecompleto(nombre);
        usuario.setCorreo(correo);
        usuario.setContrasena(encoder.encode(password));
        usuario.setRol(rol);
        repo.save(usuario);
    }
}