 package org.example.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import org.example.models.Producto;
import org.example.models.Venta;
import org.example.services.ProductoService;
import org.example.services.VentaService;
import org.example.services.AdminService;

public class PrincipalFrame extends JFrame {
    private String usuarioActual;
    private ProductoService productoService;
    private VentaService ventaService;
    
    // Paneles
    private JPanel panelDerecho;
    private JPanel panelProductos;
    private JPanel panelVentas;
    private JPanel panelReportes;
    private JPanel panelAdmin;
    private JPanel panelActual;
    
    // Colores profesionales
    private final Color COLOR_PRIMARIO = new Color(52, 152, 219);
    private final Color COLOR_ACENTO = new Color(46, 204, 113);
    private final Color COLOR_PANEL_IZQUIERDO = new Color(44, 62, 80);
    private final Color COLOR_FONDO = new Color(236, 240, 241);
    
    public PrincipalFrame(String usuario) {
        this.usuarioActual = usuario;
        this.productoService = new ProductoService();
        this.ventaService = new VentaService(productoService);
        
        setTitle("Sistema de Gestion de Inventario - Principal");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel de contenido (crear primero ANTES del sidebar)
        panelProductos = crearPanelProductos();
        panelVentas = crearPanelVentas();
        panelReportes = crearPanelReportes();
        panelAdmin = crearPanelAdmin();
        
        // Panel izquierdo (sidebar)
        JPanel panelIzquierdo = crearPanelIzquierdo();
        add(panelIzquierdo, BorderLayout.WEST);
        
        // Panel derecho (contenido principal)
        panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBackground(COLOR_FONDO);
        
        // Header
        JPanel header = crearHeader();
        panelDerecho.add(header, BorderLayout.NORTH);
        
        panelActual = panelProductos;
        panelDerecho.add(panelActual, BorderLayout.CENTER);
        
        add(panelDerecho, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_PANEL_IZQUIERDO);
        panel.setPreferredSize(new Dimension(250, getHeight()));
        
        // Logo/Titulo
        JLabel lblTitulo = new JLabel("\uD83C\uDFE6 SISTEMA DE");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));
        
        JLabel lblSubtitulo = new JLabel("INVENTARIO");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblSubtitulo.setForeground(COLOR_ACENTO);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel tituloPanel = new JPanel();
        tituloPanel.setLayout(new BoxLayout(tituloPanel, BoxLayout.Y_AXIS));
        tituloPanel.setBackground(COLOR_PANEL_IZQUIERDO);
        tituloPanel.add(lblTitulo);
        tituloPanel.add(lblSubtitulo);
        
        // Separator
        JSeparator separator = new JSeparator();
        separator.setBackground(COLOR_PRIMARIO);
        separator.setMaximumSize(new Dimension(200, 2));
        
        // Menu buttons
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(COLOR_PANEL_IZQUIERDO);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        
        menuPanel.add(crearBotonMenu("\uD83D\uDCE6  Productos", () -> cambiarPanel(panelProductos)));
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(crearBotonMenu("\uD83D\uDED2  Ventas", () -> cambiarPanel(panelVentas)));
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(crearBotonMenu("\uD83D\uDCCA  Reportes", () -> cambiarPanel(panelReportes)));
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(crearBotonMenu("\u2699\uFE0F  Administracion", () -> cambiarPanel(panelAdmin)));
        
        // Panel de usuario
        JPanel usuarioPanel = new JPanel();
        usuarioPanel.setLayout(new BoxLayout(usuarioPanel, BoxLayout.Y_AXIS));
        usuarioPanel.setBackground(COLOR_PANEL_IZQUIERDO);
        usuarioPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JLabel lblUsuario = new JLabel("\uD83D\uDC64 " + usuarioActual);
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUsuario.setForeground(new Color(189, 195, 199));
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton btnCerrar = new JButton("\uD83D\uDEAA Cerrar Sesion");
        btnCerrar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setBackground(new Color(192, 57, 43));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCerrar.addActionListener(e -> cerrarSesion());
        
        usuarioPanel.add(lblUsuario);
        usuarioPanel.add(Box.createVerticalStrut(10));
        usuarioPanel.add(btnCerrar);
        
        panel.add(tituloPanel);
        panel.add(separator);
        panel.add(menuPanel);
        panel.add(Box.createVerticalGlue());
        panel.add(usuarioPanel);
        
        return panel;
    }
    
    private JButton crearBotonMenu(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        boton.setForeground(Color.BLACK);
        boton.setBackground(new Color(52, 73, 94));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setIconTextGap(15);
        boton.setMaximumSize(new Dimension(220, 45));
        
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(COLOR_PRIMARIO);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(new Color(52, 73, 94));
            }
        });
        
        boton.addActionListener(e -> accion.run());
        
        return boton;
    }
    
    private void cambiarPanel(JPanel nuevoPanel) {
        if (panelActual != nuevoPanel) {
            // Remover el panel actual del panelDerecho
            if (panelActual != null) {
                panelDerecho.remove(panelActual);
            }
            panelActual = nuevoPanel;
            panelDerecho.add(panelActual, BorderLayout.CENTER);
            panelDerecho.revalidate();
            panelDerecho.repaint();
        }
    }
    
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblBienvenida = new JLabel("\uD83D\uDC4B Bienvenido, " + usuarioActual);
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBienvenida.setForeground(COLOR_PANEL_IZQUIERDO);
        
        JLabel lblFecha = new JLabel("\uD83D\uDCC5 " + java.time.LocalDate.now());
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblFecha.setForeground(new Color(127, 140, 141));
        
        header.add(lblBienvenida, BorderLayout.WEST);
        header.add(lblFecha, BorderLayout.EAST);
        
        return header;
    }
    
    // ==================== PANEL DE PRODUCTOS ====================
    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Titulo
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(COLOR_FONDO);
        
        JLabel lblTitulo = new JLabel("\uD83D\uDCE6 Gestion de Productos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_PANEL_IZQUIERDO);
        
        JButton btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.setBackground(COLOR_PRIMARIO);
        btnActualizar.setForeground(Color.BLACK);
        btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(e -> actualizarTablaProductos());
        
        topPanel.add(lblTitulo, BorderLayout.WEST);
        topPanel.add(btnActualizar, BorderLayout.EAST);
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(COLOR_FONDO);
        
        JButton btnAgregar = crearBotonAccion("\u2795 Agregar", COLOR_ACENTO, e -> agregarProducto());
        JButton btnEditar = crearBotonAccion("\u270F\uFE0F Editar", COLOR_PRIMARIO, e -> editarProducto());
        JButton btnEliminar = crearBotonAccion("\uD83D\uDDD1\uFE0F Eliminar", new Color(231, 76, 60), e -> eliminarProducto());
        JButton btnBuscar = crearBotonAccion("\uD83D\uDD0D Buscar", new Color(155, 89, 182), e -> buscarProducto());
        
        JTextField txtBuscar = new JTextField(20);
        txtBuscar.setPreferredSize(new Dimension(200, 35));
        
        toolbar.add(btnAgregar);
        toolbar.add(btnEditar);
        toolbar.add(btnEliminar);
        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(new JLabel("\uD83D\uDD0DBuscar:"));
        toolbar.add(txtBuscar);
        toolbar.add(btnBuscar);
        
        // Tabla de productos
        String[] columnas = {"ID", "Codigo", "Nombre", "Descripcion", "Precio Compra", "Precio Venta", "Stock", "Stock Min", "Categoria", "Proveedor"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(25);
        tabla.getTableHeader().setBackground(COLOR_PRIMARIO);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        
        // Cargar datos
        cargarProductos(modelo);
        
        // Crear panel central con toolbar y tabla
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(COLOR_FONDO);
        centerPanel.add(toolbar, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void cargarProductos(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        List<Producto> productos = productoService.getProductos();
        for (Producto p : productos) {
            modelo.addRow(new Object[]{
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecioCompra(),
                p.getPrecioVenta(),
                p.getStockActual(),
                p.getStockMinimo(),
                p.getCategoria(),
                p.getProveedor()
            });
        }
    }
    
    private void actualizarTablaProductos() {
        productoService = new ProductoService();
        JTable tabla = findTableInPanel(panelProductos);
        if (tabla != null) {
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            cargarProductos(modelo);
        }
    }
    
    // ==================== PANEL DE VENTAS ====================
    private JPanel crearPanelVentas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Titulo
        JLabel lblTitulo = new JLabel("🛒 Gestion de Ventas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_PANEL_IZQUIERDO);
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(COLOR_FONDO);
        
        JButton btnNuevaVenta = crearBotonAccion("➕ Nueva Venta", COLOR_ACENTO, e -> nuevaVenta());
        JButton btnActualizar = crearBotonAccion("🔄 Actualizar", COLOR_PRIMARIO, e -> actualizarTablaVentas());
        
        toolbar.add(btnNuevaVenta);
        toolbar.add(btnActualizar);
        
        // Tabla de ventas
        String[] columnas = {"ID", "Factura", "Cliente", "Fecha", "Documento", "Total", "Metodo Pago", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(25);
        tabla.getTableHeader().setBackground(COLOR_PRIMARIO);
        tabla.getTableHeader().setForeground(Color.BLACK);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        
        // Cargar datos de ventas
        cargarVentas(modelo);
        
        // Panel central
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(COLOR_FONDO);
        centerPanel.add(toolbar, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void cargarVentas(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        List<Venta> ventas = ventaService.getVentas();
        for (Venta v : ventas) {
            modelo.addRow(new Object[]{
                v.getId(),
                v.getNumeroFactura(),
                v.getCliente(),
                v.getFechaVenta(),
                v.getTipoDocumento(),
                String.format("$%.2f", v.getTotal()),
                v.getMetodoPago(),
                v.getEstado()
            });
        }
    }
    
    private void actualizarTablaVentas() {
        ventaService = new VentaService(productoService);
        JTable tabla = findTableInPanel(panelVentas);
        if (tabla != null) {
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            cargarVentas(modelo);
        }
    }
    
    // ==================== PANEL DE REPORTES ====================
    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Titulo
        JLabel lblTitulo = new JLabel("📊 Reportes y Estadísticas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_PANEL_IZQUIERDO);
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(COLOR_FONDO);
        
        JButton btnInventario = crearBotonAccion("📦 Inventario", COLOR_PRIMARIO, e -> mostrarReporteInventario());
        JButton btnStockBajo = crearBotonAccion("⚠️ Stock Bajo", new Color(231, 76, 60), e -> mostrarReporteStockBajo());
        JButton btnEstadisticas = crearBotonAccion("📈 Estadísticas", new Color(155, 89, 182), e -> mostrarEstadisticas());
        
        toolbar.add(btnInventario);
        toolbar.add(btnStockBajo);
        toolbar.add(btnEstadisticas);
        
        // Area de texto para mostrar reportes
        final JTextArea txtReporte = new JTextArea();
        txtReporte.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtReporte.setEditable(false);
        txtReporte.setBackground(Color.WHITE);
        txtReporte.setForeground(COLOR_PANEL_IZQUIERDO);
        txtReporte.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Mostrar resumen inicial
        actualizarResumenReportes(txtReporte);
        
        JScrollPane scrollPane = new JScrollPane(txtReporte);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        
        // Panel central
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(COLOR_FONDO);
        centerPanel.add(toolbar, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void actualizarResumenReportes(JTextArea txtReporte) {
        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════════════════════════════════\n");
        sb.append("                        RESUMEN DEL SISTEMA                      \n");
        sb.append("════════════════════════════════════════════════════════════════\n\n");
        
        List<Producto> productos = productoService.getProductos();
        List<Venta> ventas = ventaService.getVentas();
        
        // Estadísticas de productos
        sb.append("📦 PRODUCTOS:\n");
        sb.append("   Total de productos: ").append(productos.size()).append("\n");
        long productosEnStock = productos.stream().filter(p -> p.getStockActual() > 0).count();
        sb.append("   Productos en stock: ").append(productosEnStock).append("\n");
        long productosBajoStock = productos.stream().filter(Producto::necesitaReposicion).count();
        sb.append("   Productos con stock bajo: ").append(productosBajoStock).append("\n");
        
        double valorInventario = productos.stream().mapToDouble(Producto::calcularValorInventario).sum();
        sb.append(String.format("   Valor total inventario: $%.2f\n\n", valorInventario));
        
        // Estadísticas de ventas
        sb.append("🛒 VENTAS:\n");
        sb.append("   Total de ventas: ").append(ventas.size()).append("\n");
        if (!ventas.isEmpty()) {
            double totalVentas = ventas.stream().mapToDouble(Venta::getTotal).sum();
            sb.append(String.format("   Monto total vendido: $%.2f\n", totalVentas));
            
            double promedioVentas = totalVentas / ventas.size();
            sb.append(String.format("   Promedio por venta: $%.2f\n\n", promedioVentas));
        }
        
        sb.append("════════════════════════════════════════════════════════════════\n");
        
        txtReporte.setText(sb.toString());
    }
    
    private void mostrarReporteInventario() {
        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════════════════════════════════════════════════════\n");
        sb.append("                           REPORTE DE INVENTARIO                                   \n");
        sb.append("════════════════════════════════════════════════════════════════════════════════════\n\n");
        
        List<Producto> productos = productoService.getProductos();
        
        if (productos.isEmpty()) {
            sb.append("No hay productos registrados.\n");
        } else {
            sb.append(String.format("%-10s %-25s %-10s %-10s %-15s\n", "CÓDIGO", "PRODUCTO", "PRECIO", "STOCK", "CATEGORÍA"));
            sb.append("────────────────────────────────────────────────────────────────────────────────\n");
            
            for (Producto p : productos) {
                sb.append(String.format("%-10s %-25s $%-9.2f %-10d %-15s\n",
                        p.getCodigo(),
                        p.getNombre().length() > 25 ? p.getNombre().substring(0, 22) + "..." : p.getNombre(),
                        p.getPrecioVenta(),
                        p.getStockActual(),
                        p.getCategoria()));
            }
        }
        
        sb.append("\n════════════════════════════════════════════════════════════════════════════════════\n");
        
        JTextArea txtReporte = new JTextArea(sb.toString());
        txtReporte.setEditable(false);
        txtReporte.setFont(new Font("Consolas", Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(txtReporte);
        JOptionPane.showMessageDialog(this, scrollPane, "Reporte de Inventario", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarReporteStockBajo() {
        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════════════════════════════════════════════════════\n");
        sb.append("                        PRODUCTOS CON STOCK BAJO                                   \n");
        sb.append("════════════════════════════════════════════════════════════════════════════════════\n\n");
        
        List<Producto> productos = productoService.getProductos();
        List<Producto> productosBajos = productos.stream()
                .filter(Producto::necesitaReposicion)
                .toList();
        
        if (productosBajos.isEmpty()) {
            sb.append("✅ Todos los productos tienen stock suficiente.\n");
        } else {
            sb.append(String.format("%-10s %-25s %-10s %-10s %-10s\n", "CÓDIGO", "PRODUCTO", "STOCK", "MÍNIMO", "ESTADO"));
            sb.append("────────────────────────────────────────────────────────────────────────────────\n");
            
            for (Producto p : productosBajos) {
                String estado = p.getStockActual() == 0 ? "⚠️ CRÍTICO" : "⚠️ BAJO";
                sb.append(String.format("%-10s %-25s %-10d %-10d %s\n",
                        p.getCodigo(),
                        p.getNombre().length() > 25 ? p.getNombre().substring(0, 22) + "..." : p.getNombre(),
                        p.getStockActual(),
                        p.getStockMinimo(),
                        estado));
            }
        }
        
        sb.append("\n════════════════════════════════════════════════════════════════════════════════════\n");
        
        JTextArea txtReporte = new JTextArea(sb.toString());
        txtReporte.setEditable(false);
        txtReporte.setFont(new Font("Consolas", Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(txtReporte);
        JOptionPane.showMessageDialog(this, scrollPane, "Stock Bajo", JOptionPane.WARNING_MESSAGE);
    }
    
    private void mostrarEstadisticas() {
        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════════════════════════════════════════════════════\n");
        sb.append("                          ESTADÍSTICAS DE VENTAS                                   \n");
        sb.append("════════════════════════════════════════════════════════════════════════════════════\n\n");
        
        List<Venta> ventas = ventaService.getVentas();
        
        if (ventas.isEmpty()) {
            sb.append("Sin ventas registradas.\n");
        } else {
            double totalVentas = ventas.stream().mapToDouble(Venta::getTotal).sum();
            double promedio = totalVentas / ventas.size();
            double maxVenta = ventas.stream().mapToDouble(Venta::getTotal).max().orElse(0);
            double minVenta = ventas.stream().mapToDouble(Venta::getTotal).min().orElse(0);
            
            sb.append(String.format("Total de ventas realizadas: %d\n", ventas.size()));
            sb.append(String.format("Monto total: $%.2f\n", totalVentas));
            sb.append(String.format("Promedio por venta: $%.2f\n", promedio));
            sb.append(String.format("Venta máxima: $%.2f\n", maxVenta));
            sb.append(String.format("Venta mínima: $%.2f\n\n", minVenta));
            
            // Métodos de pago
            long efectivo = ventas.stream().filter(v -> "EFECTIVO".equals(v.getMetodoPago())).count();
            long tarjeta = ventas.stream().filter(v -> "TARJETA BANCARIA".equals(v.getMetodoPago())).count();
            
            sb.append("Métodos de pago:\n");
            sb.append(String.format("   💵 Efectivo: %d ventas\n", efectivo));
            sb.append(String.format("   💳 Tarjeta bancaria: %d ventas\n", tarjeta));
        }
        
        sb.append("\n════════════════════════════════════════════════════════════════════════════════════\n");
        
        JTextArea txtReporte = new JTextArea(sb.toString());
        txtReporte.setEditable(false);
        txtReporte.setFont(new Font("Consolas", Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(txtReporte);
        JOptionPane.showMessageDialog(this, scrollPane, "Estadísticas", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ==================== PANEL DE ADMINISTRACION ====================
    private JPanel crearPanelAdmin() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Titulo
        JLabel lblTitulo = new JLabel("⚙️ Herramientas de Administración");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_PANEL_IZQUIERDO);
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(COLOR_FONDO);
        
        JButton btnInfoSistema = crearBotonAccion("ℹ️ Información", COLOR_PRIMARIO, e -> mostrarInfoSistema());
        JButton btnLimpiarVentas = crearBotonAccion("🗑️ Limpiar Ventas", new Color(231, 76, 60), e -> limpiarVentas());
        JButton btnLimpiarProductos = crearBotonAccion("🗑️ Limpiar Productos", new Color(231, 76, 60), e -> limpiarProductos());
        JButton btnLimpiarTodo = crearBotonAccion("💣 Reset Total", new Color(142, 68, 173), e -> limpiarTodo());
        
        toolbar.add(btnInfoSistema);
        toolbar.add(btnLimpiarVentas);
        toolbar.add(btnLimpiarProductos);
        toolbar.add(btnLimpiarTodo);
        
        // Área de información
        final JTextArea txtInfo = new JTextArea();
        txtInfo.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtInfo.setEditable(false);
        txtInfo.setBackground(Color.WHITE);
        txtInfo.setForeground(COLOR_PANEL_IZQUIERDO);
        txtInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Mostrar información inicial
        actualizarInfoAdmin(txtInfo);
        
        JScrollPane scrollPane = new JScrollPane(txtInfo);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        
        // Panel central
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(COLOR_FONDO);
        centerPanel.add(toolbar, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void actualizarInfoAdmin(JTextArea txtInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════════════════════════════════\n");
        sb.append("              INFORMACIÓN DEL SISTEMA DE INVENTARIO              \n");
        sb.append("════════════════════════════════════════════════════════════════\n\n");
        
        sb.append("👤 Usuario Actual: ").append(usuarioActual).append("\n");
        sb.append("📅 Fecha: ").append(java.time.LocalDate.now()).append("\n");
        sb.append("🕐 Hora: ").append(java.time.LocalTime.now().withNano(0)).append("\n\n");
        
        sb.append("📦 ESTADÍSTICAS:\n");
        List<Producto> productos = productoService.getProductos();
        List<Venta> ventas = ventaService.getVentas();
        
        sb.append("   Productos registrados: ").append(productos.size()).append("\n");
        sb.append("   Ventas realizadas: ").append(ventas.size()).append("\n");
        
        long bajoStock = productos.stream().filter(Producto::necesitaReposicion).count();
        sb.append("   Productos con stock bajo: ").append(bajoStock).append("\n\n");
        
        sb.append("🛠️ OPCIONES DE MANTENIMIENTO:\n");
        sb.append("   🗑️ Limpiar Ventas - Elimina historial de ventas\n");
        sb.append("   🗑️ Limpiar Productos - Elimina todos los productos\n");
        sb.append("   💣 Reset Total - Limpia toda la base de datos\n\n");
        
        sb.append("════════════════════════════════════════════════════════════════\n");
        
        txtInfo.setText(sb.toString());
    }
    
    private void mostrarInfoSistema() {
        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════════════════════════════════════\n");
        sb.append("            INFORMACIÓN DETALLADA DEL SISTEMA                     \n");
        sb.append("════════════════════════════════════════════════════════════════════\n\n");
        
        sb.append("👤 USUARIO:\n");
        sb.append("   Usuario actual: ").append(usuarioActual).append("\n");
        sb.append("   Hora de sesión: ").append(java.time.LocalDateTime.now().withNano(0)).append("\n\n");
        
        sb.append("📊 ESTADÍSTICAS GENERALES:\n");
        List<Producto> productos = productoService.getProductos();
        List<Venta> ventas = ventaService.getVentas();
        
        sb.append(String.format("   Total de productos: %d\n", productos.size()));
        long enStock = productos.stream().filter(p -> p.getStockActual() > 0).count();
        sb.append(String.format("   Productos con stock: %d\n", enStock));
        
        long bajoStock = productos.stream().filter(Producto::necesitaReposicion).count();
        sb.append(String.format("   Productos con stock bajo: %d\n", bajoStock));
        
        double valorInventario = productos.stream().mapToDouble(Producto::calcularValorInventario).sum();
        sb.append(String.format("   Valor total de inventario: $%.2f\n\n", valorInventario));
        
        sb.append(String.format("   Total de ventas: %d\n", ventas.size()));
        if (!ventas.isEmpty()) {
            double totalVendido = ventas.stream().mapToDouble(Venta::getTotal).sum();
            sb.append(String.format("   Monto total vendido: $%.2f\n", totalVendido));
        }
        
        sb.append("\n💾 BASE DE DATOS:\n");
        sb.append("   Motor: PostgreSQL 42.7.9\n");
        sb.append("   Base: sistema\n");
        sb.append("   Usuario: postgres\n");
        sb.append("   Estado: ✅ Conectado\n\n");
        
        sb.append("════════════════════════════════════════════════════════════════════\n");
        
        JTextArea txtInfo = new JTextArea(sb.toString());
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font("Consolas", Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(txtInfo);
        JOptionPane.showMessageDialog(this, scrollPane, "Información del Sistema", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ==================== METODOS AUXILIARES ====================
    private JButton crearBotonAccion(String texto, Color color, ActionListener action) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setForeground(Color.BLACK);
        boton.setBackground(color);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(160, 40));
        
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(color.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
            }
        });
        
        boton.addActionListener(action);
        return boton;
    }
    
    private JTable findTableInPanel(JPanel panel) {
        // Búsqueda recursiva de JTable en el panel
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                if (scrollPane.getViewport().getView() instanceof JTable) {
                    return (JTable) scrollPane.getViewport().getView();
                }
            } else if (comp instanceof JPanel) {
                // Buscar recursivamente en panneles anidados
                JTable tabla = findTableInPanel((JPanel) comp);
                if (tabla != null) {
                    return tabla;
                }
            }
        }
        return null;
    }
    
    // ==================== ACCIONES ====================
    private void agregarProducto() {
        JDialog dialogo = new JDialog(this, "➕ Agregar Nuevo Producto", true);
        dialogo.setSize(500, 450);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout(10, 10));
        
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelFormulario.setBackground(COLOR_FONDO);
        
        // Campos del formulario
        final JTextField txtCodigo = crearCampoFormulario(panelFormulario, "Codigo:");
        final JTextField txtNombre = crearCampoFormulario(panelFormulario, "Nombre:");
        final JTextField txtDescripcion = crearCampoFormulario(panelFormulario, "Descripcion:");
        final JTextField txtPrecioCompra = crearCampoFormulario(panelFormulario, "Precio Compra:");
        final JTextField txtPrecioVenta = crearCampoFormulario(panelFormulario, "Precio Venta:");
        final JTextField txtStock = crearCampoFormulario(panelFormulario, "Stock Actual:");
        final JTextField txtStockMin = crearCampoFormulario(panelFormulario, "Stock Minimo:");
        final JTextField txtCategoria = crearCampoFormulario(panelFormulario, "Categoria:");
        final JTextField txtProveedor = crearCampoFormulario(panelFormulario, "Proveedor:");
        
        JScrollPane scrollPane = new JScrollPane(panelFormulario);
        dialogo.add(scrollPane, BorderLayout.CENTER);
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(Color.WHITE);
        
        JButton btnGuardar = new JButton("💾 Guardar");
        btnGuardar.setBackground(COLOR_ACENTO);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(e -> {
            try {
                Producto producto = new Producto();
                producto.setCodigo(txtCodigo.getText());
                producto.setNombre(txtNombre.getText());
                producto.setDescripcion(txtDescripcion.getText());
                producto.setPrecioCompra(Double.parseDouble(txtPrecioCompra.getText()));
                producto.setPrecioVenta(Double.parseDouble(txtPrecioVenta.getText()));
                producto.setStockActual(Integer.parseInt(txtStock.getText()));
                producto.setStockMinimo(Integer.parseInt(txtStockMin.getText()));
                producto.setStockMaximo(Integer.parseInt(txtStock.getText()) + 20);
                producto.setCategoria(txtCategoria.getText());
                producto.setProveedor(txtProveedor.getText());
                producto.setFechaIngreso(java.time.LocalDate.now());
                producto.setActivo(true);
                
                if (productoService.registrarProducto(producto)) {
                    JOptionPane.showMessageDialog(dialogo, "✅ Producto agregado exitosamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
                    actualizarTablaProductos();
                    dialogo.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialogo, "❌ Error al agregar el producto", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, "⚠️ Verifique los datos numericos", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton btnCancelar = new JButton("❌ Cancelar");
        btnCancelar.setBackground(new Color(231, 76, 60));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        
        dialogo.setVisible(true);
    }
    
    private JTextField crearCampoFormulario(JPanel panel, String etiqueta) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lbl);
        
        JTextField txt = new JTextField();
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(txt);
        panel.add(Box.createVerticalStrut(5));
        
        return txt;
    }
    
    private void editarProducto() {
        JTable tabla = findTableInPanel(panelProductos);
        if (tabla == null || tabla.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "⚠️ Seleccione un producto para editar", "Selecccion", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idProducto = (int) tabla.getValueAt(tabla.getSelectedRow(), 0);
        List<Producto> productos = productoService.getProductos();
        
        Producto productoActualTemp = null;
        for (Producto p : productos) {
            if (p.getId() == idProducto) {
                productoActualTemp = p;
                break;
            }
        }
        
        if (productoActualTemp == null) return;
        final Producto productoActual = productoActualTemp;
        
        JDialog dialogo = new JDialog(this, "✏️ Editar Producto", true);
        dialogo.setSize(500, 450);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout(10, 10));
        
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelFormulario.setBackground(COLOR_FONDO);
        
        final JTextField txtNombre = crearCampoFormulario(panelFormulario, "Nombre:");
        txtNombre.setText(productoActual.getNombre());
        final JTextField txtDescripcion = crearCampoFormulario(panelFormulario, "Descripcion:");
        txtDescripcion.setText(productoActual.getDescripcion());
        final JTextField txtPrecioVenta = crearCampoFormulario(panelFormulario, "Precio Venta:");
        txtPrecioVenta.setText(String.valueOf(productoActual.getPrecioVenta()));
        final JTextField txtStock = crearCampoFormulario(panelFormulario, "Stock:");
        txtStock.setText(String.valueOf(productoActual.getStockActual()));
        
        JScrollPane scrollPane = new JScrollPane(panelFormulario);
        dialogo.add(scrollPane, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(Color.WHITE);
        
        JButton btnGuardar = new JButton("💾 Guardar");
        btnGuardar.setBackground(COLOR_ACENTO);
        btnGuardar.setForeground(Color.BLACK);
        btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(e -> {
            try {
                productoActual.setNombre(txtNombre.getText());
                productoActual.setDescripcion(txtDescripcion.getText());
                productoActual.setPrecioVenta(Double.parseDouble(txtPrecioVenta.getText()));
                productoActual.setStockActual(Integer.parseInt(txtStock.getText()));
                
                if (productoService.actualizarProducto(productoActual)) {
                    JOptionPane.showMessageDialog(dialogo, "✅ Producto actualizado", "Exito", JOptionPane.INFORMATION_MESSAGE);
                    actualizarTablaProductos();
                    dialogo.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialogo, "❌ Error al actualizar", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, "⚠️ Datos invalidos", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton btnCancelarEdit = new JButton("❌ Cancelar");
        btnCancelarEdit.setBackground(new Color(231, 76, 60));
        btnCancelarEdit.setForeground(Color.BLACK);
        btnCancelarEdit.setFocusPainted(false);
        btnCancelarEdit.addActionListener(e -> dialogo.dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelarEdit);
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        
        dialogo.setVisible(true);
    }
    
    private void eliminarProducto() {
        JTable tabla = findTableInPanel(panelProductos);
        if (tabla == null || tabla.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "⚠️ Seleccione un producto para eliminar", "Selecccion", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idProducto = (int) tabla.getValueAt(tabla.getSelectedRow(), 0);
        String nombreProducto = (String) tabla.getValueAt(tabla.getSelectedRow(), 2);
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "🗑️ ¿Desea eliminar el producto: " + nombreProducto + "?",
            "Confirmar Eliminacion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (productoService.eliminiarProducto(idProducto)) {
                JOptionPane.showMessageDialog(this, "✅ Producto eliminado", "Exito", JOptionPane.INFORMATION_MESSAGE);
                actualizarTablaProductos();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error al eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void buscarProducto() {
        JTable tabla = findTableInPanel(panelProductos);
        if (tabla == null) return;
        
        String termino = JOptionPane.showInputDialog(this, "🔍 Buscar producto por nombre:");
        if (termino == null || termino.trim().isEmpty()) return;
        
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);
        
        List<Producto> productos = productoService.getProductos();
        for (Producto p : productos) {
            if (p.getNombre().toLowerCase().contains(termino.toLowerCase())) {
                modelo.addRow(new Object[]{
                    p.getId(), p.getCodigo(), p.getNombre(), p.getDescripcion(),
                    p.getPrecioCompra(), p.getPrecioVenta(), p.getStockActual(),
                    p.getStockMinimo(), p.getCategoria(), p.getProveedor()
                });
            }
        }
        
        JOptionPane.showMessageDialog(this, "✅ Busqueda completada", "resultado", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void nuevaVenta() {
        if (productoService.getProductos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ No hay productos disponibles para realizar una venta", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog dialogo = new JDialog(this, "🛒 Nueva Venta", true);
        dialogo.setSize(600, 500);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout(10, 10));
        
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelForm.setBackground(COLOR_FONDO);
        
        final JTextField txtCliente = crearCampoFormulario(panelForm, "Nombre Cliente:");
        final JTextField txtDocumento = crearCampoFormulario(panelForm, "Documento:");
        final JTextField txtCantidad = crearCampoFormulario(panelForm, "Cantidad:");
        final JTextField txtDescuento = crearCampoFormulario(panelForm, "Descuento (%):");
        txtDescuento.setText("0");
        
        final JComboBox<String> cmbMetodo = new JComboBox<>(new String[]{"EFECTIVO", "TARJETA BANCARIA"});
        JLabel lblMetodo = new JLabel("Metodo de Pago:");
        lblMetodo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelForm.add(lblMetodo);
        panelForm.add(cmbMetodo);
        panelForm.add(Box.createVerticalStrut(10));
        
        JScrollPane scrollPane = new JScrollPane(panelForm);
        dialogo.add(scrollPane, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(Color.WHITE);
        
        JButton btnProcesar = new JButton("✅ Procesar Venta");
        btnProcesar.setBackground(COLOR_ACENTO);
        btnProcesar.setForeground(Color.BLACK);
        btnProcesar.addActionListener(e -> {
            try {
                String cliente = txtCliente.getText();
                String documento = txtDocumento.getText();
                int cantidad = Integer.parseInt(txtCantidad.getText());
                double descuento = Double.parseDouble(txtDescuento.getText());
                String metodo = (String) cmbMetodo.getSelectedItem();
                
                if (cliente.isEmpty() || documento.isEmpty() || cantidad <= 0) {
                    JOptionPane.showMessageDialog(dialogo, "⚠️ Completa todos los campos", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                JOptionPane.showMessageDialog(dialogo, "✅ Venta registrada exitosamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
                dialogo.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, "⚠️ Datos invalidos", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton btnCancelar = new JButton("❌ Cancelar");
        btnCancelar.setBackground(new Color(231, 76, 60));
        btnCancelar.setForeground(Color.BLACK);
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        panelBotones.add(btnProcesar);
        panelBotones.add(btnCancelar);
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        
        dialogo.setVisible(true);
    }
    
    private void verVentas() {
        JDialog dialogo = new JDialog(this, "📋 Historial de Ventas", false);
        dialogo.setSize(800, 500);
        dialogo.setLocationRelativeTo(this);
        
        String[] columnas = {"ID", "Factura", "Cliente", "Fecha", "Total", "Metodo", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(25);
        tabla.getTableHeader().setBackground(COLOR_PRIMARIO);
        tabla.getTableHeader().setForeground(Color.WHITE);
        
        List<Venta> ventas = ventaService.getVentas();
        for (Venta v : ventas) {
            modelo.addRow(new Object[]{
                v.getId(), v.getNumeroFactura(), v.getCliente(), 
                v.getFechaVenta(), v.getTotal(), v.getMetodoPago(), v.getEstado()
            });
        }
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        dialogo.add(scrollPane);
        dialogo.setVisible(true);
    }
    
    private void limpiarVentas() {
        int opcion = JOptionPane.showConfirmDialog(this, 
            "⚠️ ADVERTENCIA: Esto eliminara TODAS las ventas y detalles.\n¿Esta seguro?",
            "Confirmar Limpieza", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (opcion == JOptionPane.YES_OPTION) {
            AdminService adminService = new AdminService();
            if (adminService.limpiarDetalleVentas() && adminService.limpiarVentas()) {
                ventaService = new VentaService(productoService);
                JOptionPane.showMessageDialog(this, "✅ Historial de ventas limpiado", "Exito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error al limpiar ventas", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void limpiarProductos() {
        int opcion = JOptionPane.showConfirmDialog(this, 
            "⚠️ ADVERTENCIA: Esto eliminara TODOS los productos.\n¿Esta seguro?",
            "Confirmar Limpieza", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (opcion == JOptionPane.YES_OPTION) {
            AdminService adminService = new AdminService();
            adminService.limpiarDetalleVentas();
            adminService.limpiarVentas();
            adminService.limpiarMovimientosInventario();
            if (adminService.limpiarProductos()) {
                productoService = new ProductoService();
                actualizarTablaProductos();
                JOptionPane.showMessageDialog(this, "✅ Todos los productos han sido eliminados", "Exito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error al limpiar productos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void limpiarTodo() {
        int opcion = JOptionPane.showConfirmDialog(this, 
            "🗑️ ADVERTENCIA CRITICA: Esto eliminara TODOS los datos.\n¿Esta ABSOLUTAMENTE seguro?",
            "Confirmar Limpieza Total", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE);
        
        if (opcion == JOptionPane.YES_OPTION) {
            AdminService adminService = new AdminService();
            if (adminService.limpiarTodasLasTablas()) {
                productoService = new ProductoService();
                ventaService = new VentaService(productoService);
                actualizarTablaProductos();
                JOptionPane.showMessageDialog(this, "✅ Base de datos limpiada completamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error al limpiar base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this, 
            "\uD83D\uDE4F Esta seguro que desea cerrar sesion?",
            "Cerrar Sesion", 
            JOptionPane.YES_NO_OPTION);
        
        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
    }
}
