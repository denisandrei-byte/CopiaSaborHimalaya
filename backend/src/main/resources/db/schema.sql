
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS plato (
    id_plato BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    precio NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
    categoria VARCHAR(50) NOT NULL CHECK (categoria IN ('ENTRANTES', 'ENSALADAS', 'PRINCIPALES', 'ESPECIALIDADES', 'POSTRES', 'BEBIDAS')),
    en_carta BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS pedido(
    id_pedido BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    importe_total NUMERIC(10,2) NOT NULL CHECK (importe_total >= 0),
    estado VARCHAR(50) NOT NULL DEFAULT 'Recibido',
    fecha_hora_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);

CREATE TABLE IF NOT EXISTS menu(
    id_menu BIGSERIAL PRIMARY KEY,
    fecha DATE NOT NULL UNIQUE,
    precio NUMERIC(10,2) NOT NULL DEFAULT 15.00 CHECK (precio >= 0),
    configurado BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS opcion_menu (
    id_opcion BIGSERIAL PRIMARY KEY,
    id_menu BIGINT NOT NULL,
    id_plato BIGINT NOT NULL,
    tipo_opcion VARCHAR(20) NOT NULL CHECK (tipo_opcion IN ('Entrante', 'Principal', 'Postre')),
    orden INTEGER NOT NULL CHECK (orden BETWEEN 1 AND 3),
    FOREIGN KEY (id_menu) REFERENCES menu (id_menu) ON DELETE CASCADE,
    FOREIGN KEY (id_plato) REFERENCES plato (id_plato),
    CONSTRAINT unico_menu_tipo_orden UNIQUE (id_menu, tipo_opcion, orden),
    CONSTRAINT unico_menu_tipo_plato UNIQUE (id_menu, tipo_opcion, id_plato)
);

CREATE TABLE IF NOT EXISTS linea_pedido (
    id_linea BIGSERIAL PRIMARY KEY,
    id_pedido BIGINT NOT NULL,
    id_plato BIGINT,
    id_menu BIGINT,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10,2) NOT NULL CHECK (precio_unitario >= 0),
    FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido) ON DELETE CASCADE,
    FOREIGN KEY (id_plato) REFERENCES plato (id_plato),
    FOREIGN KEY (id_menu) REFERENCES menu (id_menu),
    CONSTRAINT chk_coherencia_linea CHECK (
        (id_plato IS NOT NULL AND id_menu IS NULL) OR 
        (id_plato IS NULL AND id_menu IS NOT NULL)
    )
);

CREATE TABLE IF NOT EXISTS eleccion_menu (
    id_eleccion BIGSERIAL PRIMARY KEY,
    id_linea BIGINT NOT NULL,
    id_plato BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('Entrante', 'Principal', 'Postre', 'Bebida')),
    FOREIGN KEY (id_linea) REFERENCES linea_pedido (id_linea) ON DELETE CASCADE,
    FOREIGN KEY (id_plato) REFERENCES plato (id_plato),
    CONSTRAINT unico_linea_tipo UNIQUE (id_linea, tipo)
);





