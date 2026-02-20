package org.example.ui;

import org.example.database.DatabaseConnection;
import javax.swing.*;


public class SistemaInventario {
    public static void main(String[] args) {
        // Configurar look and feel moderno
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Inicializar la base de datos
        if (DatabaseConnection.inicializar()) {
            System.out.println("Base de datos inicializada correctamente");
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(null,
                    "Error al conectar a la base de datos.\nVerifica que PostgreSQL este ejecutandose.",
                    "Error de Conexion",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
