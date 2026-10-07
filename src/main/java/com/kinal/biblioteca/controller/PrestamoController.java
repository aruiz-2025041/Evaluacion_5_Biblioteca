package com.kinal.biblioteca.controller;

import com.kinal.biblioteca.dto.PrestamoRequest;
import com.kinal.biblioteca.dto.PrestamoResponse;
import com.kinal.biblioteca.service.PrestamoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prestamos")
@RequiredArgsConstructor
public class PrestamoController {

    private final PrestamoService prestamoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrestamoResponse registrar(@Valid @RequestBody PrestamoRequest request) {
        return prestamoService.registrar(request);
    }

    @PatchMapping("/{id}/devolucion")
    public PrestamoResponse devolver(@PathVariable Long id) {
        return prestamoService.devolver(id);
    }

    @GetMapping("/mis-prestamos")
    public List<PrestamoResponse> misPrestamos(Authentication authentication) {
        return prestamoService.misPrestamos(authentication.getName());
    }

    @GetMapping("/atrasados")
    public List<PrestamoResponse> atrasados() {
        return prestamoService.atrasados();
    }
}