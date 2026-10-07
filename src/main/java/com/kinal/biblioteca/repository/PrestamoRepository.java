package com.kinal.biblioteca.repository;

import com.kinal.biblioteca.entity.EstadoPrestamo;
import com.kinal.biblioteca.entity.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    long countByUsuarioIdAndEstadoIn(Long usuarioId, Collection<EstadoPrestamo> estados);

    boolean existsByUsuarioIdAndEstadoInAndFechaDevolucionEsperadaBefore(
            Long usuarioId, Collection<EstadoPrestamo> estados, LocalDate fecha);

    List<Prestamo> findByUsuarioIdOrderByFechaPrestamoDesc(Long usuarioId);

    List<Prestamo> findByEstadoInAndFechaDevolucionEsperadaBefore(
            Collection<EstadoPrestamo> estados, LocalDate fecha);

    boolean existsByLibroId(Long libroId);
}