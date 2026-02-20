package org.example.services;

import org.example.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminService {

    public boolean limpiarDetalleVentas() {
        String sql = "DELETE FROM detalle_ventas";
        return ejecutarDelete(sql, "detalle_ventas");
    }

    public boolean limpiarVentas() {
        String sql = "DELETE FROM ventas";
        return ejecutarDelete(sql, "ventas");
    }

    public boolean limpiarMovimientosInventario() {
        String sql = "DELETE FROM movimientos_inventario";
        return ejecutarDelete(sql, "movimientos_inventario");
    }

    public boolean limpiarProductos() {
        String sql = "DELETE FROM productos";
        return ejecutarDelete(sql, "productos");
    }

    public boolean limpiarTodasLasTablas() {
        // Orden: primero detalles de ventas, luego ventas, movimientos, y luego productos.
        boolean success = true;

        success = success && limpiarDetalleVentas();
        success = success && limpiarVentas();
        success = success && limpiarMovimientosInventario();
        success = success && limpiarProductos();

        return success;
    }

    private boolean ejecutarDelete(String sql, String tabla) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int rows = pstmt.executeUpdate();
            System.out.println("✅ Tabla '" + tabla + "' limpiada. Registros eliminados: " + rows);
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error al limpiar tabla '" + tabla + "': " + e.getMessage());
            return false;
        }
    }
}