package org.example.models;

import java.time.LocalDate;

public class Producto {
    private int id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private double precioCompra;
    private double precioVenta;
    private int stockActual;
    private int stockMinimo;
    private int stockMaximo;
    private String categoria;
    private String proveedor;
    private LocalDate fechaIngreso;
    private boolean activo;


    public Producto() {
        this.stockMinimo = 10;
        this.stockMaximo = 100;
        this.fechaIngreso = LocalDate.now();
        this.activo = true;
    }


    public Producto(String codigo, String nombre, double precioVenta, int stock, String categoria) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.stockActual = stock;
        this.categoria = categoria;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public int getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(int stockMaximo) { this.stockMaximo = stockMaximo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }


    public boolean necesitaReposicion() {
        return stockActual <= stockMinimo;
    }

    public boolean hayStockSuficiente(int cantidad) {
        return stockActual >= cantidad;
    }

    public void reducirStock(int cantidad) {
        if (hayStockSuficiente(cantidad)) {
            stockActual -= cantidad;
        }
    }

    public void aumentarStock(int cantidad) {
        stockActual += cantidad;
    }

    public double calcularValorInventario() {
        return precioCompra * stockActual;
    }

    @Override
    public String toString() {
        return String.format("📦 %-10s %-25s 💲%-8.2f 📦%-5d 🏷️ %-15s",
                codigo, nombre.length() > 25 ? nombre.substring(0, 22) + "..." : nombre,
                precioVenta, stockActual, categoria);
    }

    public String toSimpleString() {
        return codigo + " - " + nombre + " ($" + precioVenta + ")";
    }
}