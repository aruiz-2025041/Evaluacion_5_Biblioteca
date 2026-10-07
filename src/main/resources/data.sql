INSERT INTO usuario (nombre, email, password, estado, rol) VALUES
('Administrador', 'admin@biblioteca.com', '$2a$10$GDU1gnJ4ogJx6nRRlsDzCOlXAZm/xLOJofjEVGyTG/O.Fh.HEd0hK', 'ACTIVO', 'ADMIN'),
('Bibliotecario', 'biblio@biblioteca.com', '$2a$10$GDU1gnJ4ogJx6nRRlsDzCOlXAZm/xLOJofjEVGyTG/O.Fh.HEd0hK', 'ACTIVO', 'BIBLIOTECARIO'),
('Lector Demo', 'lector@biblioteca.com', '$2a$10$LTHqU8.wCR/Rn4k1L5aIFe/0nv0L8YMLIax5Ie5wD.HAZrJAt1ySq', 'ACTIVO', 'LECTOR')
ON CONFLICT (email) DO NOTHING;

INSERT INTO libro (isbn, titulo, autor, categoria, stock_total, stock_disponible) VALUES
('978-0132350884', 'Clean Code', 'Robert C. Martin', 'Programación', 3, 3),
('978-0134685991', 'Effective Java', 'Joshua Bloch', 'Programación', 2, 2),
('978-0307474728', 'Cien años de soledad', 'Gabriel García Márquez', 'Novela', 4, 4)
ON CONFLICT (isbn) DO NOTHING;