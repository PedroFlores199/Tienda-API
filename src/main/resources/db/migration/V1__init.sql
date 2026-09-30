CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    direccion VARCHAR(255) NOT NULL,
    nif VARCHAR(255) NOT NULL
);

CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    precio NUMERIC(10, 2) NOT NULL,
    tipo_iva VARCHAR(50) NOT NULL,
    stock INTEGER NOT NULL
);

CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    estado VARCHAR(50) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL,
    total_base NUMERIC(10, 2) NOT NULL,
    total_iva NUMERIC(10, 2) NOT NULL,
    total NUMERIC(10, 2) NOT NULL
);

CREATE TABLE lineas_pedido (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL REFERENCES pedidos(id),
    producto_id BIGINT NOT NULL REFERENCES productos(id),
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(10, 2) NOT NULL,
    tipo_iva VARCHAR(50) NOT NULL,
    importe_base NUMERIC(10, 2) NOT NULL,
    importe_iva NUMERIC(10, 2) NOT NULL,
    importe_total NUMERIC(10, 2) NOT NULL
);

CREATE TABLE facturas (
    id BIGSERIAL PRIMARY KEY,
    numero_factura VARCHAR(255) NOT NULL UNIQUE,
    pedido_id BIGINT REFERENCES pedidos(id),
    fecha_emision TIMESTAMP NOT NULL,
    nombre_cliente VARCHAR(255) NOT NULL,
    nif_cliente VARCHAR(255) NOT NULL,
    direccion_cliente VARCHAR(255) NOT NULL,
    email_cliente VARCHAR(255) NOT NULL,
    total_base NUMERIC(10, 2) NOT NULL,
    total_iva NUMERIC(10, 2) NOT NULL,
    total NUMERIC(10, 2) NOT NULL
);

CREATE TABLE lineas_factura (
    id BIGSERIAL PRIMARY KEY,
    factura_id BIGINT NOT NULL REFERENCES facturas(id),
    nombre_producto VARCHAR(255) NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(10, 2) NOT NULL,
    tipo_iva VARCHAR(50) NOT NULL,
    importe_base NUMERIC(10, 2) NOT NULL,
    importe_iva NUMERIC(10, 2) NOT NULL,
    importe_total NUMERIC(10, 2) NOT NULL
);

CREATE TABLE secuencias_factura (
    serie VARCHAR(10) NOT NULL,
    anio INTEGER NOT NULL,
    siguiente_valor BIGINT NOT NULL,
    PRIMARY KEY (serie, anio)
);
INSERT INTO secuencias_factura (serie, anio, siguiente_valor) VALUES ('A', EXTRACT(YEAR FROM CURRENT_DATE), 1);
