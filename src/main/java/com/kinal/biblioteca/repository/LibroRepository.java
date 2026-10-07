package com.kinal.biblioteca.repository;

import com.kinal.biblioteca.entity.Libro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    boolean existsByIsbn(String isbn);

    @Query("SELECT l FROM Libro l WHERE LOWER(l.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')) " +
            "AND LOWER(l.categoria) LIKE LOWER(CONCAT('%', :categoria, '%'))")
    Page<Libro> buscar(@Param("titulo") String titulo,
                       @Param("categoria") String categoria,
                       Pageable pageable);
}