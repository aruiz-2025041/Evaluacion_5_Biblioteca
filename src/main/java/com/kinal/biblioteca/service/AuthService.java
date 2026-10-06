package com.kinal.biblioteca.service;

import com.kinal.biblioteca.dto.AuthResponse;
import com.kinal.biblioteca.dto.LoginRequest;
import com.kinal.biblioteca.dto.RegisterRequest;
import com.kinal.biblioteca.entity.EstadoUsuario;
import com.kinal.biblioteca.entity.Rol;
import com.kinal.biblioteca.entity.Usuario;
import com.kinal.biblioteca.exception.BusinessRuleException;
import com.kinal.biblioteca.exception.ResourceNotFoundException;
import com.kinal.biblioteca.repository.UsuarioRepository;
import com.kinal.biblioteca.security.CustomUserDetailsService;
import com.kinal.biblioteca.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest req) {
        if (usuarioRepository.existsByEmail(req.email())) {
            throw new BusinessRuleException("El email ya está registrado");
        }
        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .nombre(req.nombre())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .estado(EstadoUsuario.ACTIVO)
                .rol(Rol.LECTOR)
                .build());
        return construirRespuesta(usuario);
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        Usuario usuario = usuarioRepository.findByEmail(req.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return construirRespuesta(usuario);
    }

    private AuthResponse construirRespuesta(Usuario usuario) {
        String token = jwtService.generateToken(
                userDetailsService.loadUserByUsername(usuario.getEmail()));
        return new AuthResponse(token, "Bearer", usuario.getId(),
                usuario.getEmail(), usuario.getRol().name());
    }
}