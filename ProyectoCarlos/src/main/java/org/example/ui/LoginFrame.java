package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar, btnSalir;
    
    // Colores profesionales
    private final Color COLOR_PRIMARIO = new Color(52, 152, 219);
    private final Color COLOR_SECUNDARIO = new Color(41, 128, 185);
    private final Color COLOR_ACENTO = new Color(46, 204, 113);
    
    public LoginFrame() {
        setTitle("Sistema de Gestion de Inventario - Login");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        initComponents();
    }
    
    private void initComponents() {
        // Panel principal con fondo degradado
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, COLOR_PRIMARIO, 0, getHeight(), COLOR_SECUNDARIO);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        panelPrincipal.setLayout(null);
        
        // Panel de login blanco
        JPanel panelLogin = new JPanel();
        panelLogin.setLayout(null);
        panelLogin.setBackground(new Color(255, 255, 255, 230));
        panelLogin.setBounds(75, 50, 350, 280);
        panelLogin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        
        // Titulo
        JLabel lblTitulo = new JLabel("INICIAR SESION");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_PRIMARIO);
        lblTitulo.setBounds(80, 10, 250, 40);
        panelLogin.add(lblTitulo);
        
        // Icono de usuario
        JLabel lblIconoUsuario = new JLabel("\uD83D\uDC64"); // Emoji usuario
        lblIconoUsuario.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        lblIconoUsuario.setBounds(30, 70, 40, 40);
        panelLogin.add(lblIconoUsuario);
        
        // Campo de usuario
        txtUsuario = new JTextField();
        txtUsuario.setBounds(70, 75, 250, 35);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelLogin.add(txtUsuario);
        
        // Icono de contrasena
        JLabel lblIconoContrasena = new JLabel("\uD83D\uDD11"); // Emoji candado
        lblIconoContrasena.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        lblIconoContrasena.setBounds(30, 120, 40, 40);
        panelLogin.add(lblIconoContrasena);
        
        // Campo de contrasena
        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(70, 125, 250, 35);
        txtContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtContrasena.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelLogin.add(txtContrasena);
        
        // Boton ingresar
        btnIngresar = new JButton("INGRESAR \uD83D\uDD11");
        btnIngresar.setBounds(30, 180, 140, 40);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setBackground(COLOR_ACENTO);
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.setBorderPainted(false);
        btnIngresar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnIngresar.setBackground(new Color(39, 174, 96));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnIngresar.setBackground(COLOR_ACENTO);
            }
        });
        panelLogin.add(btnIngresar);
        
        // Boton salir
        btnSalir = new JButton("SALIR \uD83D\uDDED");
        btnSalir.setBounds(180, 180, 140, 40);
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalir.setBackground(new Color(231, 76, 60));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.setBorderPainted(false);
        btnSalir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSalir.setBackground(new Color(192, 57, 43));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnSalir.setBackground(new Color(231, 76, 60));
            }
        });
        panelLogin.add(btnSalir);
        
        // Label de version
        JLabel lblVersion = new JLabel("Version 1.0 - Sistema de Inventario");
        lblVersion.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblVersion.setForeground(new Color(127, 140, 141));
        lblVersion.setBounds(80, 230, 250, 20);
        panelLogin.add(lblVersion);
        
        panelPrincipal.add(panelLogin);
        add(panelPrincipal);
        
        // Eventos
        btnIngresar.addActionListener(e -> autenticarUsuario());
        btnSalir.addActionListener(e -> System.exit(0));
        
        txtContrasena.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    autenticarUsuario();
                }
            }
        });
    }
    
    private void autenticarUsuario() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());
        
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese usuario y contrasena", 
                "Error de Validacion", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Autenticacion simple (en un sistema real, esto seria contra la base de datos)
        if (usuario.equals("admin") && contrasena.equals("admin123")) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                new PrincipalFrame(usuario).setVisible(true);
            });
        } else if (usuario.equals("carlos") && contrasena.equals("carlos")) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                new PrincipalFrame(usuario).setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, 
                "Usuario o contrasena incorrectos", 
                "Error de Autenticacion", 
                JOptionPane.ERROR_MESSAGE);
            txtContrasena.setText("");
            txtUsuario.requestFocus();
        }
    }
}
