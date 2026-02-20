package org.example.database;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseConnection {
    private static Connection connection = null;
    private static final Properties config = new Properties();

    static {
        cargarConfiguracion();
    }



    private static void cargarConfiguracion() {
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                config.load(input);
            } else {
                System.err.println("⚠️  No se encontró application.properties, usando valores por defecto");
                config.setProperty("database.url", "jdbc:postgresql://localhost:5432/sistema");
                config.setProperty("database.username", "postgres");
                config.setProperty("database.password", "8080");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al cargar configuración: " + e.getMessage());
        }
    }

    public static boolean inicializar() {
        try {
            // Primero intentar crear la base de datos y tablas si no existen
            if (!inicializarBaseDeDatos()) {
                return false;
            }

            String url = config.getProperty("database.url");
            String username = config.getProperty("database.username");
            String password = config.getProperty("database.password");

            System.out.println("🔗 Conectando a PostgreSQL...");


            Class.forName("org.postgresql.Driver");


            connection = DriverManager.getConnection(url, username, password);

            if (connection != null && !connection.isClosed()) {

                // Verificar tablas básicas
                if (!verificarTablas()) {
                    System.err.println("⚠️  Algunas tablas no existen. Ejecuta schema.sql en pgAdmin");
                }

                return true;
            }

        } catch (ClassNotFoundException e) {
            System.err.println("\n❌ ERROR: Driver PostgreSQL no encontrado");
            System.err.println("Solución: Maven descargará automáticamente la dependencia.");
            System.err.println("Ejecuta: mvn clean compile");

        } catch (SQLException e) {
            System.err.println("\n❌ ERROR DE CONEXIÓN:");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("\n🔧 Posibles soluciones:");
            System.err.println("1. Verifica que PostgreSQL esté ejecutándose");
            System.err.println("2. Usuario/Contraseña en application.properties");
            System.err.println("3. Base de datos 'sistema_inventario' debe existir");
            System.err.println("4. Puerto 5432 debe estar abierto");
        }

        return false;
    }

    /**
     * Inicializa la base de datos: crea la base de datos 'sistema' y todas las tablas necesarias
     * si no existen. También inserta datos de ejemplo.
     * @return true si la inicialización fue exitosa, false en caso contrario
     */
    public static boolean inicializarBaseDeDatos() {
        String username = config.getProperty("database.username");
        String password = config.getProperty("database.password");
        
        try {
            // Primero conectar a la base de datos 'postgres' por defecto para crear la base de datos 'sistema'
            System.out.println("🔄 Verificando base de datos 'sistema'...");
            
            Class.forName("org.postgresql.Driver");
            
            // Conectar a la base de datos 'postgres' para crear 'sistema' si no existe
            String urlDefault = "jdbc:postgresql://localhost:5432/postgres";
            Connection connDefault = DriverManager.getConnection(urlDefault, username, password);
            
            // Verificar si la base de datos 'sistema' existe
            ResultSet rs = connDefault.createStatement().executeQuery(
                "SELECT 1 FROM pg_database WHERE datname = 'sistema'");
            boolean dbExists = rs.next();
            rs.close();
            
            if (!dbExists) {
                System.out.println("📦 Creando base de datos 'sistema'...");
                connDefault.createStatement().execute("CREATE DATABASE sistema");
                System.out.println("✅ Base de datos 'sistema' creada correctamente");
            }
            
            connDefault.close();
            
            // Ahora conectar a la base de datos 'sistema' y crear las tablas
            String urlSistema = "jdbc:postgresql://localhost:5432/sistema";
            Connection connSistema = DriverManager.getConnection(urlSistema, username, password);
            
            System.out.println("🔄 Verificando tablas...");
            
            // Crear tabla de productos
            connSistema.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS productos (" +
                "    id_producto SERIAL PRIMARY KEY," +
                "    codigo VARCHAR(50) UNIQUE NOT NULL," +
                "    nombre VARCHAR(100) NOT NULL," +
                "    descripcion TEXT," +
                "    precio_compra DECIMAL(12,2) DEFAULT 0," +
                "    precio_venta DECIMAL(12,2) NOT NULL," +
                "    stock_actual INTEGER DEFAULT 0," +
                "    stock_minimo INTEGER DEFAULT 10," +
                "    stock_maximo INTEGER DEFAULT 100," +
                "    categoria VARCHAR(50)," +
                "    proveedor VARCHAR(100)," +
                "    fecha_ingreso DATE DEFAULT CURRENT_DATE," +
                "    activo BOOLEAN DEFAULT true," +
                "    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            // Crear tabla de ventas
            connSistema.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS ventas (" +
                "    id_venta SERIAL PRIMARY KEY," +
                "    numero_factura VARCHAR(20) UNIQUE NOT NULL," +
                "    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    cliente VARCHAR(100)," +
                "    tipo_documento VARCHAR(10) DEFAULT 'FACTURA'," +
                "    subtotal DECIMAL(12,2) NOT NULL," +
                "    igv DECIMAL(12,2) NOT NULL," +
                "    total DECIMAL(12,2) NOT NULL," +
                "    metodo_pago VARCHAR(20)," +
                "    estado VARCHAR(20) DEFAULT 'COMPLETADA'," +
                "    vendedor VARCHAR(50)," +
                "    observaciones TEXT," +
                "    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            // Crear tabla de detalles de venta
            connSistema.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS detalle_ventas (" +
                "    id_detalle SERIAL PRIMARY KEY," +
                "    id_venta INTEGER REFERENCES ventas(id_venta) ON DELETE CASCADE," +
                "    id_producto INTEGER REFERENCES productos(id_producto)," +
                "    cantidad INTEGER NOT NULL," +
                "    precio_unitario DECIMAL(12,2) NOT NULL," +
                "    descuento DECIMAL(12,2) DEFAULT 0," +
                "    subtotal_detalle DECIMAL(12,2) NOT NULL," +
                "    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            // Crear tabla de movimientos de inventario
            connSistema.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS movimientos_inventario (" +
                "    id_movimiento SERIAL PRIMARY KEY," +
                "    id_producto INTEGER REFERENCES productos(id_producto)," +
                "    tipo_movimiento VARCHAR(20) NOT NULL," +
                "    cantidad INTEGER NOT NULL," +
                "    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    motivo TEXT," +
                "    usuario VARCHAR(50)," +
                "    referencia VARCHAR(50)," +
                "    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            // Crear tabla de categorías
            connSistema.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS categorias (" +
                "    id_categoria SERIAL PRIMARY KEY," +
                "    nombre VARCHAR(50) UNIQUE NOT NULL," +
                "    descripcion TEXT," +
                "    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            // Crear tabla de proveedores
            connSistema.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS proveedores (" +
                "    id_proveedor SERIAL PRIMARY KEY," +
                "    ruc VARCHAR(20) UNIQUE NOT NULL," +
                "    nombre VARCHAR(100) NOT NULL," +
                "    contacto VARCHAR(100)," +
                "    telefono VARCHAR(20)," +
                "    email VARCHAR(100)," +
                "    direccion TEXT," +
                "    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            System.out.println("✅ Tablas creadas correctamente");
            
            // Insertar datos de ejemplo si las tablas están vacías
            ResultSet rsCategorias = connSistema.createStatement().executeQuery("SELECT COUNT(*) as cnt FROM categorias");
            if (rsCategorias.next() && rsCategorias.getInt("cnt") == 0) {
                System.out.println("📝 Insertando datos de ejemplo...");
                
                // Insertar categorías
                connSistema.createStatement().execute(
                    "INSERT INTO categorias (nombre, descripcion) VALUES" +
                    "('ELECTRÓNICA', 'Productos electrónicos y tecnología')," +
                    "('INFORMÁTICA', 'Computadoras y accesorios')," +
                    "('HOGAR', 'Artículos para el hogar')," +
                    "('OFICINA', 'Suministros de oficina')," +
                    "('ROPA', 'Ropa y accesorios')," +
                    "('ALIMENTOS', 'Productos alimenticios')"
                );
                
                // Insertar proveedores de ejemplo
                connSistema.createStatement().execute(
                    "INSERT INTO proveedores (ruc, nombre, contacto, telefono, email, direccion) VALUES" +
                    "('12345678901', 'Proveedor Principal S.A.C.', 'Juan Pérez', '999888777', 'juan@proveedor.com', 'Av. Principal 123')," +
                    "('12345678902', 'Distribuidora Tech E.I.R.L.', 'Maria García', '999888776', 'maria@tech.com', 'Av. Tech 456')"
                );
                
                // Insertar productos de ejemplo
                connSistema.createStatement().execute(
                    "INSERT INTO productos (codigo, nombre, descripcion, precio_compra, precio_venta, stock_actual, stock_minimo, stock_maximo, categoria, proveedor) VALUES" +
                    "('PROD001', 'Laptop HP 15', 'Laptop HP 15 pulgadas 8GB RAM', 2500.00, 3299.00, 15, 5, 30, 'INFORMÁTICA', 'Distribuidora Tech E.I.R.L.')," +
                    "('PROD002', 'Mouse Inalámbrico', 'Mouse inalámbrico USB', 25.00, 45.00, 50, 10, 100, 'INFORMÁTICA', 'Proveedor Principal S.A.C.')," +
                    "('PROD003', 'Teclado Mecánico', 'Teclado mecánico RGB', 120.00, 189.00, 30, 5, 50, 'INFORMÁTICA', 'Distribuidora Tech E.I.R.L.')," +
                    "('PROD004', 'Monitor 24 pulgadas', 'Monitor Full HD 24 pulgadas', 450.00, 599.00, 20, 5, 40, 'ELECTRÓNICA', 'Distribuidora Tech E.I.R.L.')," +
                    "('PROD005', 'Auriculares Bluetooth', 'Auriculares inalámbricos', 80.00, 129.00, 40, 10, 80, 'ELECTRÓNICA', 'Proveedor Principal S.A.C.')," +
                    "('PROD006', 'Escritorio de Oficina', 'Escritorio de melamina 1.5m', 250.00, 399.00, 10, 3, 20, 'OFICINA', 'Proveedor Principal S.A.C.')," +
                    "('PROD007', 'Silla Ergonomica', 'Silla ergonómica con soporte lumbar', 300.00, 499.00, 8, 2, 15, 'OFICINA', 'Proveedor Principal S.A.C.')," +
                    "('PROD008', 'Impresora Epson', 'Impresora tinta continua', 350.00, 499.00, 12, 3, 25, 'OFICINA', 'Distribuidora Tech E.I.R.L.')"
                );
                
                // Insertar una venta de ejemplo
                connSistema.createStatement().execute(
                    "INSERT INTO ventas (numero_factura, cliente, subtotal, igv, total, metodo_pago, estado, vendedor) VALUES" +
                    "('F001-00001', 'Cliente General', 100.00, 18.00, 118.00, 'EFECTIVO', 'COMPLETADA', 'Vendedor1')"
                );
                
                // Insertar detalle de venta
                connSistema.createStatement().execute(
                    "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, descuento, subtotal_detalle) VALUES" +
                    "(1, 2, 2, 45.00, 0, 90.00)"
                );
                
                // Actualizar stock después de la venta
                connSistema.createStatement().execute(
                    "UPDATE productos SET stock_actual = stock_actual - 2 WHERE id_producto = 2"
                );
                
                System.out.println("✅ Datos de ejemplo insertados correctamente");
            }
            rsCategorias.close();
            
            connSistema.close();
            
            System.out.println("✅ Base de datos inicializada correctamente");
            return true;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: Driver PostgreSQL no encontrado");
            return false;
        }
    }

    private static boolean verificarTablas() {
        String[] tablas = {"productos", "ventas", "detalle_ventas"};
        boolean todasExisten = true;

        try {
            DatabaseMetaData metaData = connection.getMetaData();

            for (String tabla : tablas) {
                ResultSet rs = metaData.getTables(null, null, tabla, new String[]{"TABLE"});
                if (!rs.next()) {
                    System.err.println("   ⚠️  Tabla '" + tabla + "' no encontrada");
                    todasExisten = false;
                }
                rs.close();
            }

            return todasExisten;

        } catch (SQLException e) {
            System.err.println("Error al verificar tablas: " + e.getMessage());
            return false;
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                inicializar();
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("Error al obtener conexión: " + e.getMessage());
            return null;
        }
    }

    public static void cerrar() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Conexión a PostgreSQL cerrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }


    public static ResultSet ejecutarConsulta(String sql) throws SQLException {
        Statement stmt = getConnection().createStatement();
        return stmt.executeQuery(sql);
    }

    public static int ejecutarActualizacion(String sql) throws SQLException {
        Statement stmt = getConnection().createStatement();
        return stmt.executeUpdate(sql);
    }

    public static PreparedStatement prepararStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    public static int obtenerUltimoId(String tabla) throws SQLException {
        String sql = "SELECT MAX(id_" + tabla + ") as max_id FROM " + tabla;
        ResultSet rs = ejecutarConsulta(sql);
        return rs.next() ? rs.getInt("max_id") : 0;
    }
}