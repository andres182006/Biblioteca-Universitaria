package com.Biblioteca.BibliotecaUniversitaria;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class BibliotecaUniversitariaApplication {
@PostConstruct
public void init() {
	// Establece la zona horaria predeterminada de la JVM a America/Bogota
	TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));
}
	public static void main(String[] args) {
		SpringApplication.run(BibliotecaUniversitariaApplication.class, args);
	}

}
