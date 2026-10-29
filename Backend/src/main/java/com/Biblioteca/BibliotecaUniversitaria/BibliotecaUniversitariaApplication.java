package com.Biblioteca.BibliotecaUniversitaria;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;
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
