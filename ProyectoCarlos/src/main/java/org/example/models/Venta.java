package org.example.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private int id;
    private String numeroFactura;
    private LocalDateTime fechaVenta;
    private String cliente;
    private String tipoDocumento;
    private double total;
    private String metodoPago;
    private String estado;
    private String vendedor;
    private List<DetalleVenta> detalles;

    public Venta() {
        this.fechaVenta = LocalDateTime.now();
        this.tipoDocumento = "FACTURA";
        this.estado = "COMPLETADA";
        this.vendedor = "SISTEMA";
        this.detalles = new ArrayList<>();
        this.numeroFactura = generarNumeroFactura();
    }

    public Venta(String cliente) {
        this();
        this.cliente = cliente;
    }

    private String generarNumeroFactura() {
        return "FAC-" + System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }

    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getVendedor() { return vendedor; }
    public void setVendedor(String vendedor) { this.vendedor = vendedor; }

    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }

    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        total += detalle.getSubtotal();
    }

    public int getCantidadProductos() {
        return detalles.stream().mapToInt(DetalleVenta::getCantidad).sum();
    }

    @Override
    public String toString() {
        return String.format("🧾 Factura: %s | Cliente: %s | Total: 💲%.2f | Productos: %d",
                numeroFactura, cliente, total, detalles.size());
    }
}