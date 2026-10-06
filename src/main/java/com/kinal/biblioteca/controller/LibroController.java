package com.kinal.biblioteca.controller;

import com.kinal.biblioteca.dto.LibroRequest;
import com.kinal.biblioteca.dto.LibroResponse;
import com.kinal.biblioteca.dto.PageResponse;
import com.kinal.biblioteca.service.LibroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/libros")
@RequiredArgsConstructor
public class LibroController {

    private final LibroService libroService;

    @GetMapping
    public PageResponse<LibroResponse> listar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return libroService.listar(titulo, categoria, page, size);
    }

    @GetMapping("/{id}")
    public LibroResponse obtener(@PathVariable Long id) {
        return libroService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LibroResponse crear(@Valid @RequestBody LibroRequest request) {
        return libroService.crear(request);
    }

    @PutMapping("/{id}")
    public LibroResponse actualizar(@PathVariable Long id, @Valid @RequestBody LibroRequest request) {
        return libroService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        libroService.eliminar(id);
    }
}