package com.kinal.biblioteca.service;

import com.kinal.biblioteca.dto.LibroRequest;
import com.kinal.biblioteca.dto.LibroResponse;
import com.kinal.biblioteca.dto.PageResponse;
import com.kinal.biblioteca.entity.Libro;
import com.kinal.biblioteca.exception.BusinessRuleException;
import com.kinal.biblioteca.exception.ResourceNotFoundException;
import com.kinal.biblioteca.repository.LibroRepository;
import com.kinal.biblioteca.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LibroService {

    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;

    @Transactional(readOnly = true)
    public PageResponse<LibroResponse> listar(String titulo, String categoria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("titulo"));
        return PageResponse.from(
                libroRepository.buscar(
                        titulo == null ? "" : titulo,
                        categoria == null ? "" : categoria,
                        pageable).map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public LibroResponse obtener(Long id) {
        return toResponse(buscarPorId(id));
    }

    @Transactional
    public LibroResponse crear(LibroRequest req) {
        if (libroRepository.existsByIsbn(req.isbn())) {
            throw new BusinessRuleException("Ya existe un libro con ese ISBN");
        }
        Libro libro = Libro.builder()
                .isbn(req.isbn())
                .titulo(req.titulo())
                .autor(req.autor())
                .categoria(req.categoria())
                .stockTotal(req.stockTotal())
                .stockDisponible(req.stockTotal())
                .build();
        return toResponse(libroRepository.save(libro));
    }

    @Transactional
    public LibroResponse actualizar(Long id, LibroRequest req) {
        Libro libro = buscarPorId(id);
        if (!libro.getIsbn().equals(req.isbn()) && libroRepository.existsByIsbn(req.isbn())) {
            throw new BusinessRuleException("Ya existe un libro con ese ISBN");
        }
        int prestados = libro.getStockTotal() - libro.getStockDisponible();
        if (req.stockTotal() < prestados) {
            throw new BusinessRuleException(
                    "El stock total no puede ser menor a los ejemplares prestados (" + prestados + ")");
        }
        libro.setIsbn(req.isbn());
        libro.setTitulo(req.titulo());
        libro.setAutor(req.autor());
        libro.setCategoria(req.categoria());
        libro.setStockTotal(req.stockTotal());
        libro.setStockDisponible(req.stockTotal() - prestados);
        return toResponse(libroRepository.save(libro));
    }

    @Transactional
    public void eliminar(Long id) {
        Libro libro = buscarPorId(id);
        if (prestamoRepository.existsByLibroId(id)) {
            throw new BusinessRuleException("No se puede eliminar: el libro tiene préstamos registrados");
        }
        libroRepository.delete(libro);
    }

    private Libro buscarPorId(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id " + id));
    }

    private LibroResponse toResponse(Libro l) {
        return new LibroResponse(l.getId(), l.getIsbn(), l.getTitulo(), l.getAutor(),
                l.getCategoria(), l.getStockTotal(), l.getStockDisponible());
    }
}