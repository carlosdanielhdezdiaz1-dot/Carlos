-- Crear base de datos (ejecutar manualmente en pgAdmin)
-- CREATE DATABASE sistema_inventario;

-- Tabla de productos
CREATE TABLE IF NOT EXISTS productos (
    id_producto SERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio_compra DECIMAL(12,2),
    precio_venta DECIMAL(12,2) NOT NULL,
    stock_actual INTEGER DEFAULT 0,
    stock_minimo INTEGER DEFAULT 10,
    stock_maximo INTEGER DEFAULT 100,
    categoria VARCHAR(50),
    proveedor VARCHAR(100),
    fecha_ingreso DATE DEFAULT CURRENT_DATE,
    activo BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de ventas
CREATE TABLE IF NOT EXISTS ventas (
    id_venta SERIAL PRIMARY KEY,
    numero_factura VARCHAR(20) UNIQUE NOT NULL,
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cliente VARCHAR(100),
    tipo_documento VARCHAR(10) DEFAULT 'FACTURA',
    subtotal DECIMAL(12,2) NOT NULL,
    igv DECIMAL(12,2) NOT NULL,
    total DECIMAL(12,2) NOT NULL,
    metodo_pago VARCHAR(20),
    estado VARCHAR(20) DEFAULT 'COMPLETADA',
    vendedor VARCHAR(50),
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de detalles de venta
CREATE TABLE IF NOT EXISTS detalle_ventas (
    id_detalle SERIAL PRIMARY KEY,
    id_venta INTEGER REFERENCES ventas(id_venta) ON DELETE CASCADE,
    id_producto INTEGER REFERENCES productos(id_producto),
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(12,2) NOT NULL,
    descuento DECIMAL(12,2) DEFAULT 0,
    subtotal_detalle DECIMAL(12,2) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de movimientos de inventario
CREATE TABLE IF NOT EXISTS movimientos_inventario (
    id_movimiento SERIAL PRIMARY KEY,
    id_producto INTEGER REFERENCES productos(id_producto),
    tipo_movimiento VARCHAR(20) NOT NULL,
    cantidad INTEGER NOT NULL,
    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    motivo TEXT,
    usuario VARCHAR(50),
    referencia VARCHAR(50),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de categorías
CREATE TABLE IF NOT EXISTS categorias (
    id_categoria SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de proveedores
CREATE TABLE IF NOT EXISTS proveedores (
    id_proveedor SERIAL PRIMARY KEY,
    ruc VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    contacto VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_productos_codigo ON productos(codigo);
CREATE INDEX IF NOT EXISTS idx_productos_categoria ON productos(categoria);
CREATE INDEX IF NOT EXISTS idx_ventas_fecha ON ventas(fecha_venta);
CREATE INDEX IF NOT EXISTS idx_ventas_cliente ON ventas(cliente);
CREATE INDEX IF NOT EXISTS idx_movimientos_producto ON movimientos_inventario(id_producto);

-- Insertar datos iniciales
INSERT INTO categorias (nombre) VALUES
    ('ELECTRÓNICA'),
    ('INFORMÁTICA'),
    ('HOGAR'),
    ('OFICINA'),
    ('ROPA'),
    ('ALIMENTOS')
ON CONFLICT (nombre) DO NOTHING;

-- Insertar producto de ejemplo
INSERT INTO productos (codigo, nombre, precio_venta, stock_actual, categoria)
VALUES ('P001', 'Producto de Ejemplo', 100.00, 50, 'INFORMÁTICA')
ON CONFLICT (codigo) DO NOTHING;