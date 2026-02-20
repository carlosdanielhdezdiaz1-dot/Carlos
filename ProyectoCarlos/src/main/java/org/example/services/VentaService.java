package org.example.services;

import org.example.models.Venta;
import org.example.models.DetalleVenta;
import org.example.models.Producto;
import org.example.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaService {
    private ProductoService productoService;
    private List<Venta> ventas;

    public VentaService(ProductoService productoService) {
        this.productoService = productoService;
        this.ventas = new ArrayList<>();
        cargarVentasDesdeBD();
    }

    private void cargarVentasDesdeBD() {
        String sql = "SELECT * FROM ventas ORDER BY fecha_venta DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Venta venta = new Venta();
                venta.setId(rs.getInt("id_venta"));
                venta.setNumeroFactura(rs.getString("numero_factura"));
                venta.setFechaVenta(rs.getTimestamp("fecha_venta").toLocalDateTime());
                venta.setCliente(rs.getString("cliente"));
                venta.setTipoDocumento(rs.getString("tipo_documento"));
                venta.setTotal(rs.getDouble("total"));
                venta.setMetodoPago(rs.getString("metodo_pago"));
                venta.setEstado(rs.getString("estado"));
                venta.setVendedor(rs.getString("vendedor"));

                cargarDetallesVenta(venta);
                ventas.add(venta);
            }


        } catch (SQLException e) {
            System.err.println("❌ Error al cargar ventas: " + e.getMessage());
        }
    }

    private void cargarDetallesVenta(Venta venta) {
        String sql = "SELECT dv.*, p.nombre as nombre_producto FROM detalle_ventas dv " +
                "JOIN productos p ON dv.id_producto = p.id_producto " +
                "WHERE dv.id_venta = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, venta.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setId(rs.getInt("id_detalle"));
                detalle.setIdVenta(rs.getInt("id_venta"));
                detalle.setIdProducto(rs.getInt("id_producto"));
                detalle.setNombreProducto(rs.getString("nombre_producto"));
                detalle.setCantidad(rs.getInt("cantidad"));
                detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
                detalle.setDescuento(rs.getDouble("descuento"));
                detalle.setSubtotal(rs.getDouble("subtotal_detalle"));

                venta.agregarDetalle(detalle);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al cargar detalles: " + e.getMessage());
        }
    }

    public boolean procesarVenta(Venta venta) {
        if (!venta.getMetodoPago().equals("EFECTIVO") && !venta.getMetodoPago().equals("TARJETA BANCARIA")) {
            System.err.println("❌ Método de pago no válido. Solo se permite EFECTIVO o TARJETA BANCARIA.");
            return false;
        }

        String sqlVenta = "INSERT INTO ventas (numero_factura, cliente, tipo_documento, " +
                "total, metodo_pago, estado, vendedor) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, venta.getNumeroFactura());
            pstmt.setString(2, venta.getCliente());
            pstmt.setString(3, venta.getTipoDocumento());
            pstmt.setDouble(4, venta.getTotal());
            pstmt.setString(5, venta.getMetodoPago());
            pstmt.setString(6, venta.getEstado());
            pstmt.setString(7, venta.getVendedor());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        venta.setId(generatedKeys.getInt(1));
                    }
                }

                guardarDetallesVenta(venta);

                actualizarStockProductos(venta);

                ventas.add(venta);

                imprimirFactura(venta);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al procesar venta: " + e.getMessage());
        }

        return false;
    }

    private void guardarDetallesVenta(Venta venta) throws SQLException {
        String sqlDetalle = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, " +
                "precio_unitario, descuento, subtotal_detalle) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlDetalle)) {

            for (DetalleVenta detalle : venta.getDetalles()) {
                pstmt.setInt(1, venta.getId());
                pstmt.setInt(2, detalle.getIdProducto());
                pstmt.setInt(3, detalle.getCantidad());
                pstmt.setDouble(4, detalle.getPrecioUnitario());
                pstmt.setDouble(5, detalle.getDescuento());
                pstmt.setDouble(6, detalle.getSubtotal());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
        }
    }

    private void actualizarStockProductos(Venta venta) {
        for (DetalleVenta detalle : venta.getDetalles()) {

            Producto producto = productoService.buscarPorId(detalle.getIdProducto());
            if (producto != null) {

                int nuevoStock = producto.getStockActual() - detalle.getCantidad();

                try {

                    String sql = "UPDATE productos SET stock_actual = ?, actualizado_en = CURRENT_TIMESTAMP WHERE id_producto = ?";
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {

                        pstmt.setInt(1, nuevoStock);
                        pstmt.setInt(2, producto.getId());
                        pstmt.executeUpdate();


                        producto.setStockActual(nuevoStock);


                        registrarMovimientoInventario(producto.getId(), "SALIDA",
                                detalle.getCantidad(), "VENTA #" + venta.getNumeroFactura());

                        System.out.println("✅ Stock actualizado: " + producto.getNombre() +
                                " - Nuevo stock: " + nuevoStock);
                    }

                } catch (SQLException e) {
                    System.err.println("❌ Error al actualizar stock: " + e.getMessage());
                }
            } else {
                System.err.println("❌ Producto con ID " + detalle.getIdProducto() + " no encontrado");
            }
        }
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

    private void imprimirFactura(Venta venta) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                         🧾 FACTURA DE VENTA                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════════════╣");
        System.out.printf ("║ Factura: %-58s ║\n", venta.getNumeroFactura());
        System.out.printf ("║ Fecha:   %-58s ║\n", venta.getFechaVenta().toString());
        System.out.printf ("║ Cliente: %-58s ║\n", venta.getCliente());
        System.out.printf ("║ Vendedor:%-58s ║\n", venta.getVendedor());
        System.out.println("╠════════════════════════════════════════════════════════════════════╣");
        System.out.println("║ PRODUCTO                     CANT.   PRECIO UNIT.   SUBTOTAL       ║");
        System.out.println("╠════════════════════════════════════════════════════════════════════╣");

        for (DetalleVenta detalle : venta.getDetalles()) {
            String nombre = detalle.getNombreProducto();
            if (nombre.length() > 25) {
                nombre = nombre.substring(0, 22) + "...";
            }
            System.out.printf("║ %-25s %6d   💲%10.2f   💲%10.2f ║\n",
                    nombre, detalle.getCantidad(), detalle.getPrecioUnitario(), detalle.getSubtotal());
        }

        System.out.println("╠════════════════════════════════════════════════════════════════════╣");
        System.out.printf ("║ TOTAL:                                               💲%12.2f ║\n", venta.getTotal());
        System.out.println("╠════════════════════════════════════════════════════════════════════╣");
        System.out.printf ("║ Método de pago: %-48s ║\n", venta.getMetodoPago());
        System.out.println("║                                                                    ║");
        System.out.println("║                    ¡GRACIAS POR SU COMPRA! 🛒                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");
    }

    public void mostrarEstadisticasVentas() {
        System.out.println("\n📈 ESTADÍSTICAS DE VENTAS");
        System.out.println("══════════════════════════");

        if (ventas.isEmpty()) {
            System.out.println("No hay ventas registradas");
            return;
        }

        double totalVentas = 0;
        int totalProductos = 0;

        for (Venta venta : ventas) {
            totalVentas += venta.getTotal();
            totalProductos += venta.getCantidadProductos();
        }

        System.out.println("Total de ventas: " + ventas.size());
        System.out.printf("Ingreso total: 💲%.2f\n", totalVentas);
        System.out.println("Productos vendidos: " + totalProductos);
        System.out.printf("Promedio por venta: 💲%.2f\n", totalVentas / ventas.size());

        int limite = Math.min(5, ventas.size());
        System.out.println("\nÚltimas " + limite + " ventas:");
        for (int i = 0; i < limite; i++) {
            Venta venta = ventas.get(i);
            System.out.printf("  %s - %s - 💲%.2f\n",
                    venta.getNumeroFactura(), venta.getCliente(), venta.getTotal());
        }
    }

    public List<Venta> getVentas() {
        return new ArrayList<>(ventas);
    }
}