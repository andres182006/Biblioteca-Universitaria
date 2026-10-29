package com.Biblioteca.BibliotecaUniversitaria.backend.DTO.Auth;

import java.util.List;

public record LoginResponse(
        String token,
        String tipo,
        long expiraEn,
        String correo,
        List<String> roles
) {
}