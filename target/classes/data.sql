-- Crear usuario de ejemplo si no existe
INSERT INTO Usuario(id, email, password, rol, activo)
SELECT null, 'test@unlam.edu.ar', 'test', 'ADMIN', true
    WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email = 'test@unlam.edu.ar');

-- San Justo
INSERT INTO Locker (descripcion, latitud, longitud, seleccionado, tipo, codigo_postal)
SELECT 'Locker San Justo 1', -34.6853, -58.5588, true, 'PEQUENIO', '1754'
    WHERE NOT EXISTS (SELECT 1 FROM Locker WHERE descripcion = 'Locker San Justo 1' AND latitud = -34.6853 AND longitud = -58.5588);

INSERT INTO Locker (descripcion, latitud, longitud, seleccionado, tipo, codigo_postal)
SELECT 'Locker San Justo 2', -34.6860, -58.5631, true, 'MEDIANO', '1754'
    WHERE NOT EXISTS (SELECT 1 FROM Locker WHERE descripcion = 'Locker San Justo 2' AND latitud = -34.6860 AND longitud = -58.5631);

-- Ramos Mejía
INSERT INTO Locker (descripcion, latitud, longitud, seleccionado, tipo, codigo_postal)
SELECT 'Locker Ramos Mejía 1', -34.6485, -58.5617, true, 'GRANDE', '1704'
    WHERE NOT EXISTS (SELECT 1 FROM Locker WHERE descripcion = 'Locker Ramos Mejía 1' AND latitud = -34.6485 AND longitud = -58.5617);

INSERT INTO Locker (descripcion, latitud, longitud, seleccionado, tipo, codigo_postal)
SELECT 'Locker Ramos Mejía 2', -34.6440, -58.5683, true, 'PEQUENIO', '1704'
    WHERE NOT EXISTS (SELECT 1 FROM Locker WHERE descripcion = 'Locker Ramos Mejía 2' AND latitud = -34.6440 AND longitud = -58.5683);

-- Luis Guillon
INSERT INTO Locker (descripcion, latitud, longitud, seleccionado, tipo, codigo_postal)
SELECT 'Locker Luis Guillon', -34.7954444, -58.4492554, true, 'PEQUENIO', '1838'
    WHERE NOT EXISTS (SELECT 1 FROM Locker WHERE descripcion = 'Locker Luis Guillon' AND latitud = -34.7954444 AND longitud = -58.4492554);

INSERT INTO Locker (descripcion, latitud, longitud, seleccionado, tipo, codigo_postal)
SELECT 'Locker Luis Guillon 1', -34.8006186, -58.4555308, true, 'GRANDE', '1838'
    WHERE NOT EXISTS (SELECT 1 FROM Locker WHERE descripcion = 'Locker Luis Guillon 1' AND latitud = -34.8006186 AND longitud = -58.4555308);

INSERT INTO Locker (descripcion, latitud, longitud, seleccionado, tipo, codigo_postal)
SELECT 'Locker Luis Guillon 2', -34.7974037, -58.4476196, true, 'GRANDE', '1838'
    WHERE NOT EXISTS (SELECT 1 FROM Locker WHERE descripcion = 'Locker Luis Guillon 2' AND latitud = -34.7974037 AND longitud = -58.4476196);
