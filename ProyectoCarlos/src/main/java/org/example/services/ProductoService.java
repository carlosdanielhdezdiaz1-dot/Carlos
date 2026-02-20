package org.example.services;

import org.example.models.Producto;
import org.example.database.DatabaseConnection;
import org.example.structures.CustomStack;
import org.example.structures.CustomQueue;
import org.example.structures.BinaryTree;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoService {
    private List<Producto> productos;
    private BinaryTree categoriasTree;
    private CustomStack<String> undoStack;
    private CustomQueue<String> pedidosQueue;

    public ProductoService() {
        productos = new ArrayList<>();
        categoriasTree = new BinaryTree();
        undoStack = new CustomStack<>(100);
        pedidosQueue = new CustomQueue<>();

        cargarProductosDesdeBD();
        inicializarCategorias();
    }

    private void cargarProductosDesdeBD() {
        String sql = "SELECT * FROM productos WHERE activo = true ORDER BY codigo";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("id_producto"));
                producto.setCodigo(rs.getString("codigo"));
                producto.setNombre(rs.getString("nombre"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setPrecioCompra(rs.getDouble("precio_compra"));
                producto.setPrecioVenta(rs.getDouble("precio_venta"));
                producto.setStockActual(rs.getInt("stock_actual"));
                producto.setStockMinimo(rs.getInt("stock_minimo"));
                producto.setStockMaximo(rs.getInt("stock_maximo"));
                producto.setCategoria(rs.getString("categoria"));
                producto.setProveedor(rs.getString("proveedor"));
                producto.setFechaIngreso(rs.getDate("fecha_ingreso").toLocalDate());
                producto.setActivo(rs.getBoolean("activo"));

                productos.add(producto);
                categoriasTree.insert(producto.getCategoria());
            }


        } catch (SQLException e) {
            System.err.println("❌ Error al cargar productos: " + e.getMessage());
        }
    }

    private void inicializarCategorias() {
        String[] categoriasBase = {"ELECTRONICA", "INFORMATICA", "HOGAR", "OFICINA", "ROPA", "ALIMENTOS"};
        for (String categoria : categoriasBase) {
            categoriasTree.insert(categoria);
        }
    }

    public boolean registrarProducto(Producto producto) {
        String sql = "INSERT INTO productos (codigo, nombre, descripcion, precio_compra, " +
                "precio_venta, stock_actual, stock_minimo, stock_maximo, categoria, " +
                "proveedor, fecha_ingreso) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, producto.getCodigo());
            pstmt.setString(2, producto.getNombre());
            pstmt.setString(3, producto.getDescripcion());
            pstmt.setDouble(4, producto.getPrecioCompra());
            pstmt.setDouble(5, producto.getPrecioVenta());
            pstmt.setInt(6, producto.getStockActual());
            pstmt.setInt(7, producto.getStockMinimo());
            pstmt.setInt(8, producto.getStockMaximo());
            pstmt.setString(9, producto.getCategoria());
            pstmt.setString(10, producto.getProveedor());
            pstmt.setDate(11, Date.valueOf(producto.getFechaIngreso()));

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        producto.setId(generatedKeys.getInt(1));
                    }
                }

                productos.add(producto);
                categoriasTree.insert(producto.getCategoria());
                undoStack.push("REGISTRO_PRODUCTO:" + producto.getCodigo());

                verificarReposicion(producto);

                System.out.println("✅ Producto registrado exitosamente: " + producto.getNombre());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al registrar producto: " + e.getMessage());
        }

        return false;
    }

    private void verificarReposicion(Producto producto) {
        if (producto.necesitaReposicion()) {
            String pedido = "Reponer " + producto.getCodigo() + " - " + producto.getNombre() +
                    " (Stock: " + producto.getStockActual() + ", Mínimo: " + producto.getStockMinimo() + ")";
            pedidosQueue.enqueue(pedido);
            System.out.println("⚠️  Alerta: Producto necesita reposición - " + producto.getNombre());
        }
    }

    public Producto buscarPorCodigo(String codigo) {
        for (Producto producto : productos) {
            if (producto.getCodigo().equalsIgnoreCase(codigo)) {
                return producto;
            }
        }
        return null;
    }
    public Producto buscarPorId(int id) {
        for (Producto producto : productos) {
            if (producto.getId() == id) {
                return producto;
            }
        }
        return null;
    }

    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> resultados = new ArrayList<>();
        for (Producto producto : productos) {
            if (producto.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                resultados.add(producto);
            }
        }
        return resultados;
    }

    public boolean actualizarStock(String codigo, int nuevoStock, String motivo) {
        Producto producto = buscarPorCodigo(codigo);
        if (producto != null) {
            try {
                int stockAnterior = producto.getStockActual();
                producto.setStockActual(nuevoStock);

                String sql = "UPDATE productos SET stock_actual = ?, actualizado_en = CURRENT_TIMESTAMP WHERE codigo = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, nuevoStock);
                    pstmt.setString(2, codigo);
                    pstmt.executeUpdate();
                }

                registrarMovimientoInventario(producto.getId(), "AJUSTE", nuevoStock - stockAnterior, motivo);
                undoStack.push("UPDATE_STOCK:" + codigo + ":" + stockAnterior);

                verificarReposicion(producto);

                System.out.println("✅ Stock actualizado: " + producto.getNombre() + " -> " + nuevoStock);
                return true;

            } catch (SQLException e) {
                System.err.println("❌ Error al actualizar stock: " + e.getMessage());
            }
        }
        return false;
    }

    public boolean venderProducto(String codigo, int cantidad, String motivo) {
        Producto producto = buscarPorCodigo(codigo);
        if (producto != null) {

            if (!producto.hayStockSuficiente(cantidad)) {
                System.err.println("❌ Stock insuficiente para " + producto.getNombre() +
                        ". Disponible: " + producto.getStockActual() +
                        ", Solicitado: " + cantidad);
                return false;
            }

            try {
                int nuevoStock = producto.getStockActual() - cantidad;


                String sql = "UPDATE productos SET stock_actual = ?, actualizado_en = CURRENT_TIMESTAMP WHERE codigo = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, nuevoStock);
                    pstmt.setString(2, codigo);
                    pstmt.executeUpdate();
                }

                producto.setStockActual(nuevoStock);

                registrarMovimientoInventario(producto.getId(), "SALIDA", cantidad, motivo);
                verificarReposicion(producto);

                System.out.println("✅ Venta registrada: " + producto.getNombre() +
                        " x" + cantidad + " | Nuevo stock: " + nuevoStock);
                return true;

            } catch (SQLException e) {
                System.err.println("❌ Error al registrar venta: " + e.getMessage());
            }
        } else {
            System.err.println("❌ Producto con código " + codigo + " no encontrado");
        }
        return false;
    }

    private void registrarMovimientoInventario(int idProducto, String tipo, int cantidad, String motivo) {
        String sql = "INSERT INTO movimientos_inventario (id_producto, tipo_movimiento, " +
                "cantidad, motivo, usuario) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProducto);
            pstmt.setString(2, tipo);
            pstmt.setInt(3, cantidad);
            pstmt.setString(4, motivo);
            pstmt.setString(5, "SISTEMA");
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Error al registrar movimiento: " + e.getMessage());
        }
    }

    public void mostrarInventario() {
        System.out.println("\n📊 INVENTARIO ACTUAL");
        System.out.println("════════════════════════════════════════════════════════════════════════════════════");
        System.out.printf("%-10s %-25s %-10s %-8s %-15s %-10s%n",
                "CÓDIGO", "PRODUCTO", "PRECIO", "STOCK", "CATEGORÍA", "ESTADO");
        System.out.println("════════════════════════════════════════════════════════════════════════════════════");

        double valorTotal = 0;
        for (Producto producto : productos) {
            String estado = producto.necesitaReposicion() ? "⚠️ BAJO" : "✅ OK";
            System.out.printf("%-10s %-25s 💲%-9.2f %-8d %-15s %-10s%n",
                    producto.getCodigo(),
                    producto.getNombre().length() > 25 ? producto.getNombre().substring(0, 22) + "..." : producto.getNombre(),
                    producto.getPrecioVenta(),
                    producto.getStockActual(),
                    producto.getCategoria(),
                    estado);

            valorTotal += producto.calcularValorInventario();
        }

        System.out.println("════════════════════════════════════════════════════════════════════════════════════");
        System.out.printf("💰 VALOR TOTAL DEL INVENTARIO: 💲%.2f\n", valorTotal);
        System.out.println("📦 TOTAL DE PRODUCTOS: " + productos.size());
    }

    public void mostrarProductosBajoStock() {
        System.out.println("\n⚠️  PRODUCTOS CON STOCK BAJO");
        System.out.println("════════════════════════════════════════════════════════════");

        boolean hayBajos = false;
        for (Producto producto : productos) {
            if (producto.necesitaReposicion()) {
                System.out.println("🔴 " + producto.getCodigo() + " - " + producto.getNombre() +
                        " (Stock: " + producto.getStockActual() + ", Mínimo: " + producto.getStockMinimo() + ")");
                hayBajos = true;
            }
        }

        if (!hayBajos) {
            System.out.println("✅ Todos los productos tienen stock suficiente");
        }
    }

    public void mostrarCategorias() {
        categoriasTree.inOrder();
    }

    public void procesarPedidosPendientes() {
        if (pedidosQueue.isEmpty()) {
            System.out.println("✅ No hay pedidos pendientes");
            return;
        }

        System.out.println("\n🔄 PROCESANDO PEDIDOS PENDIENTES");
        System.out.println("════════════════════════════════");
        System.out.println("Pedidos en cola: " + pedidosQueue.size());

        int contador = 1;
        while (!pedidosQueue.isEmpty()) {
            String pedido = pedidosQueue.dequeue();
            System.out.println("📦 [" + contador + "] " + pedido);
            contador++;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("✅ Todos los pedidos han sido procesados");
    }

    public void deshacerUltimaOperacion() {
        if (undoStack.isEmpty()) {
            System.out.println("📭 No hay operaciones para deshacer");
            return;
        }

        String operacion = undoStack.pop();
        System.out.println("↩️  Deshaciendo: " + operacion);

        if (operacion.startsWith("REGISTRO_PRODUCTO:")) {
            String codigo = operacion.split(":")[1];
            desactivarProducto(codigo);
        } else if (operacion.startsWith("UPDATE_STOCK:")) {
            String[] partes = operacion.split(":");
            String codigo = partes[1];
            int stockAnterior = Integer.parseInt(partes[2]);
            actualizarStock(codigo, stockAnterior, "DESHACER OPERACIÓN");
        }
    }

    private void desactivarProducto(String codigo) {
        String sql = "UPDATE productos SET activo = false WHERE codigo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigo);
            pstmt.executeUpdate();

            productos.removeIf(p -> p.getCodigo().equals(codigo));
            System.out.println("🗑️  Producto desactivado: " + codigo);

        } catch (SQLException e) {
            System.err.println("❌ Error al desactivar producto: " + e.getMessage());
        }
    }

    public boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, precio_venta = ?, " +
                "stock_actual = ? WHERE id_producto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getDescripcion());
            pstmt.setDouble(3, producto.getPrecioVenta());
            pstmt.setInt(4, producto.getStockActual());
            pstmt.setInt(5, producto.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Producto actualizado: " + producto.getNombre());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar producto: " + e.getMessage());
        }

        return false;
    }

    public boolean eliminiarProducto(int idProducto) {
        String sql = "UPDATE productos SET activo = false WHERE id_producto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProducto);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                productos.removeIf(p -> p.getId() == idProducto);
                System.out.println("✅ Producto eliminado");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar producto: " + e.getMessage());
        }

        return false;
    }

    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }
}