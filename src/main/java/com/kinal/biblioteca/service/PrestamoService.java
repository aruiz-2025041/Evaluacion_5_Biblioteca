package com.kinal.biblioteca.service;

import com.kinal.biblioteca.dto.PrestamoRequest;
import com.kinal.biblioteca.dto.PrestamoResponse;
import com.kinal.biblioteca.entity.EstadoPrestamo;
import com.kinal.biblioteca.entity.EstadoUsuario;
import com.kinal.biblioteca.entity.Libro;
import com.kinal.biblioteca.entity.Prestamo;
import com.kinal.biblioteca.entity.Rol;
import com.kinal.biblioteca.entity.Usuario;
import com.kinal.biblioteca.exception.BusinessRuleException;
import com.kinal.biblioteca.exception.ResourceNotFoundException;
import com.kinal.biblioteca.repository.LibroRepository;
import com.kinal.biblioteca.repository.PrestamoRepository;
import com.kinal.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private static final int MAX_PRESTAMOS_ACTIVOS = 3;
    private static final int DIAS_PRESTAMO = 14;
    private static final List<EstadoPrestamo> ABIERTOS =
            List.of(EstadoPrestamo.ACTIVO, EstadoPrestamo.ATRASADO);

    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;

    @Transactional(noRollbackFor = BusinessRuleException.class)
    public PrestamoResponse registrar(PrestamoRequest req) {
        Usuario usuario = usuarioRepository.findById(req.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con id " + req.usuarioId()));
        Libro libro = libroRepository.findById(req.libroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Libro no encontrado con id " + req.libroId()));
        LocalDate hoy = LocalDate.now();

        if (prestamoRepository.existsByUsuarioIdAndEstadoInAndFechaDevolucionEsperadaBefore(
                usuario.getId(), ABIERTOS, hoy)) {
            usuario.setEstado(EstadoUsuario.SANCIONADO);
            usuarioRepository.save(usuario);
            throw new BusinessRuleException(
                    "El usuario tiene préstamos vencidos y pasó a estado SANCIONADO");
        }
        if (usuario.getEstado() == EstadoUsuario.SANCIONADO) {
            throw new BusinessRuleException("El usuario está SANCIONADO");
        }
        if (usuario.getRol() == Rol.LECTOR
                && prestamoRepository.countByUsuarioIdAndEstadoIn(usuario.getId(), ABIERTOS)
                >= MAX_PRESTAMOS_ACTIVOS) {
            throw new BusinessRuleException(
                    "El lector ya tiene " + MAX_PRESTAMOS_ACTIVOS + " préstamos activos");
        }
        if (libro.getStockDisponible() <= 0) {
            throw new BusinessRuleException("No hay ejemplares disponibles de este libro");
        }

        libro.setStockDisponible(libro.getStockDisponible() - 1);
        libroRepository.save(libro);

        Prestamo prestamo = Prestamo.builder()
                .usuario(usuario)
                .libro(libro)
                .fechaPrestamo(hoy)
                .fechaDevolucionEsperada(hoy.plusDays(DIAS_PRESTAMO))
                .estado(EstadoPrestamo.ACTIVO)
                .build();
        return toResponse(prestamoRepository.save(prestamo));
    }

    @Transactional
    public PrestamoResponse devolver(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Préstamo no encontrado con id " + id));
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new BusinessRuleException("El préstamo ya fue devuelto");
        }

        LocalDate hoy = LocalDate.now();
        prestamo.setFechaDevolucionReal(hoy);
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);

        Libro libro = prestamo.getLibro();
        libro.setStockDisponible(libro.getStockDisponible() + 1);

        Usuario usuario = prestamo.getUsuario();
        if (usuario.getEstado() == EstadoUsuario.SANCIONADO
                && !prestamoRepository.existsByUsuarioIdAndEstadoInAndFechaDevolucionEsperadaBefore(
                usuario.getId(), ABIERTOS, hoy)) {
            usuario.setEstado(EstadoUsuario.ACTIVO);
        }
        return toResponse(prestamoRepository.save(prestamo));
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> misPrestamos(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return prestamoRepository.findByUsuarioIdOrderByFechaPrestamoDesc(usuario.getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public List<PrestamoResponse> atrasados() {
        List<Prestamo> lista = prestamoRepository
                .findByEstadoInAndFechaDevolucionEsperadaBefore(ABIERTOS, LocalDate.now());
        lista.forEach(p -> p.setEstado(EstadoPrestamo.ATRASADO));
        return lista.stream().map(this::toResponse).toList();
    }

    private PrestamoResponse toResponse(Prestamo p) {
        return new PrestamoResponse(p.getId(),
                p.getUsuario().getId(), p.getUsuario().getNombre(),
                p.getLibro().getId(), p.getLibro().getTitulo(),
                p.getFechaPrestamo(), p.getFechaDevolucionEsperada(),
                p.getFechaDevolucionReal(), p.getEstado());
    }
}