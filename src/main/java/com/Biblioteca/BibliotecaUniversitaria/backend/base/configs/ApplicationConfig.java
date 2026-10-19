package com.Biblioteca.BibliotecaUniversitaria.backend.base.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración general de la aplicación.
 *
 * Registra beans de infraestructura que Spring inyectará en los servicios.
 */
@Configuration
public class ApplicationConfig {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
