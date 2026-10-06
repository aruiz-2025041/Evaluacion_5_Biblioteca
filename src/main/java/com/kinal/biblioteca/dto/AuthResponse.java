package com.kinal.biblioteca.dto;

public record AuthResponse(String token, String tipo, Long id, String email, String rol) {
}