INSERT INTO usuario (nombre, email, password, estado, rol) VALUES
('Administrador', 'admin@biblioteca.com', '$2a$10$givjdWSRIeGlUp7CmA96vOIIHOjpfOaMGmCutQX906LP9HSKI8kjm', 'ACTIVO', 'ADMIN'),
('Bibliotecario', 'biblio@biblioteca.com', '$2a$10$givjdWSRIeGlUp7CmA96vOIIHOjpfOaMGmCutQX906LP9HSKI8kjm', 'ACTIVO', 'BIBLIOTECARIO'),
('Lector Demo', 'lector@biblioteca.com', '$2a$10$givjdWSRIeGlUp7CmA96vOIIHOjpfOaMGmCutQX906LP9HSKI8kjm', 'ACTIVO', 'LECTOR')
ON CONFLICT (email) DO NOTHING;

INSERT INTO libro (isbn, titulo, autor, categoria, stock_total, stock_disponible) VALUES
('978-0132350884', 'Clean Code', 'Robert C. Martin', 'Programación', 3, 3),
('978-0134685991', 'Effective Java', 'Joshua Bloch', 'Programación', 2, 2),
('978-0307474728', 'Cien años de soledad', 'Gabriel García Márquez', 'Novela', 4, 4)
ON CONFLICT (isbn) DO NOTHING;