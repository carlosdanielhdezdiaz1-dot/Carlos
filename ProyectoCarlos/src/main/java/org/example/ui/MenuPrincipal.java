package org.example.ui;

import org.example.models.Producto;
import org.example.models.Venta;
import org.example.models.DetalleVenta;
import org.example.services.ProductoService;
import org.example.services.VentaService;
import org.example.services.AdminService;

import java.util.List;
import java.util.Scanner;

public class MenuPrincipal {
    private Scanner scanner;
    private ProductoService productoService;
    private VentaService ventaService;

    public MenuPrincipal() {
        scanner = new Scanner(System.in);
        productoService = new ProductoService();
        ventaService = new VentaService(productoService);
    }

    public void iniciar() {
        boolean salir = false;

        while (!salir) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    menuGestionProductos();
                    break;
                case 2:
                    menuGestionVentas();
                    break;
                case 3:
                    menuReportes();
                    break;
                case 4:
                    menuAdministracion();
                    break;
                case 0:
                    System.out.println("\n👋 ¡Gracias por usar el sistema! Hasta pronto.");
                    salir = true;
                    break;
                default:
                    System.out.println("❌ Opción no válida. Intente nuevamente.");
            }

            if (!salir) {
                System.out.println("\n⏎ Presione ENTER para continuar...");
                scanner.nextLine();
            }
        }
    }

    private void menuAdministracion() {
        AdminService adminService = new AdminService();
        boolean volver = false;

        while (!volver) {
            System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                🔧 HERRAMIENTAS DE ADMINISTRACIÓN                   ║");
            System.out.println("╠════════════════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. 🧹 Limpiar historial de ventas (ventas y detalles)              ║");
            System.out.println("║ 2. 📦 Limpiar movimientos de inventario                            ║");
            System.out.println("║ 3. 🗑️  Limpiar todos los productos                                 ║");
            System.out.println("║ 4. 💣 Limpiar toda la base de datos (reset completo)               ║");
            System.out.println("║ 0. ↩️  Volver al menú principal                                     ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝");

            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    System.out.println("\n⚠️  ADVERTENCIA: Esta acción eliminará TODAS las ventas y sus detalles.");
                    System.out.print("¿Está seguro? (S/N): ");
                    if (scanner.nextLine().equalsIgnoreCase("S")) {
                        boolean success = adminService.limpiarDetalleVentas() && adminService.limpiarVentas();
                        if (success) {
                            System.out.println("✅ Historial de ventas limpiado exitosamente.");
                        } else {
                            System.out.println("❌ Error al limpiar el historial de ventas.");
                        }
                    }
                    break;
                case 2:
                    System.out.println("\n⚠️  Esta acción eliminará TODOS los movimientos de inventario.");
                    System.out.print("¿Está seguro? (S/N): ");
                    if (scanner.nextLine().equalsIgnoreCase("S")) {
                        if (adminService.limpiarMovimientosInventario()) {
                            System.out.println("✅ Movimientos de inventario limpiados exitosamente.");
                        } else {
                            System.out.println("❌ Error al limpiar movimientos de inventario.");
                        }
                    }
                    break;
                case 3:
                    System.out.println("\n⚠️  ADVERTENCIA: Esta acción eliminará TODOS los productos.");
                    System.out.println("               También se eliminarán las ventas y movimientos asociados.");
                    System.out.print("¿Está seguro? (S/N): ");
                    if (scanner.nextLine().equalsIgnoreCase("S")) {
                        // Primero limpiar detalles y ventas por las claves foráneas
                        adminService.limpiarDetalleVentas();
                        adminService.limpiarVentas();
                        adminService.limpiarMovimientosInventario();
                        if (adminService.limpiarProductos()) {
                            // También debemos limpiar la lista en memoria en ProductoService
                            productoService.getProductos().clear();
                            System.out.println("✅ Todos los productos han sido eliminados.");
                        } else {
                            System.out.println("❌ Error al limpiar productos.");
                        }
                    }
                    break;
                case 4:
                    System.out.println("\n⚠️  ADVERTENCIA CRÍTICA: Esta acción eliminará TODOS los datos.");
                    System.out.println("               Se borrarán productos, ventas, detalles y movimientos.");
                    System.out.print("¿Está ABSOLUTAMENTE seguro? (escriba 'CONFIRMAR' para continuar): ");
                    String confirmacion = scanner.nextLine();
                    if (confirmacion.equalsIgnoreCase("CONFIRMAR")) {
                        if (adminService.limpiarTodasLasTablas()) {
                            // Limpiar las listas en memoria
                            productoService.getProductos().clear();
                            ventaService.getVentas().clear();
                            System.out.println("✅ Base de datos limpiada completamente.");
                        } else {
                            System.out.println("❌ Error al limpiar la base de datos.");
                        }
                    } else {
                        System.out.println("❌ Operación cancelada.");
                    }
                    break;
                case 0:
                    volver = true;
                    break;
                default:
                    System.out.println("❌ Opción no válida");
            }
        }
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                 🏪 MENÚ PRINCIPAL - SISTEMA INVENTARIO             ║");
        System.out.println("╠════════════════════════════════════════════════════════════════════╣");
        System.out.println("║ 1. 📦 Gestión de Productos                                         ║");
        System.out.println("║ 2. 🛒 Gestión de Ventas                                            ║");
        System.out.println("║ 3. 📊 Reportes y Consultas                                         ║");
        System.out.println("║ 4. 🔧 Herramientas de Administración                               ║");
        System.out.println("║ 0. 🚪 Salir del sistema                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
    }

    private void menuGestionProductos() {
        boolean volver = false;

        while (!volver) {
            System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    📦 GESTIÓN DE PRODUCTOS                         ║");
            System.out.println("╠════════════════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. 📝 Registrar nuevo producto                                     ║");
            System.out.println("║ 2. 🔍 Buscar producto por código                                   ║");
            System.out.println("║ 3. 🔎 Buscar producto por nombre                                   ║");
            System.out.println("║ 4. 📈 Actualizar stock de producto                                 ║");
            System.out.println("║ 5. 📊 Mostrar inventario completo                                  ║");
            System.out.println("║ 6. ⚠️  Ver productos con stock bajo                                 ║");
            System.out.println("║ 7. ↩️  Deshacer última operación                                    ║");
            System.out.println("║ 8. 📂 Mostrar categorías                                           ║");
            System.out.println("║ 0. ↩️  Volver al menú principal                                     ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝");

            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    registrarProducto();
                    break;
                case 2:
                    buscarProductoCodigo();
                    break;
                case 3:
                    buscarProductoNombre();
                    break;
                case 4:
                    actualizarStock();
                    break;
                case 5:
                    productoService.mostrarInventario();
                    break;
                case 6:
                    productoService.mostrarProductosBajoStock();
                    break;
                case 7:
                    productoService.deshacerUltimaOperacion();
                    break;
                case 8:
                    productoService.mostrarCategorias();
                    break;
                case 0:
                    volver = true;
                    break;
                default:
                    System.out.println("❌ Opción no válida");
            }
        }
    }

    private void registrarProducto() {
        System.out.println("\n════════════ REGISTRAR NUEVO PRODUCTO ════════════");

        String codigo = leerTexto("Código del producto (ej: 1234): ");

        if (productoService.buscarPorCodigo(codigo) != null) {
            System.out.println("❌ Ya existe un producto con ese código");
            return;
        }

        String nombre = leerTexto("Nombre: ");

        double precioCompra = leerDecimal("Precio de compra: ");
        double precioVenta = leerDecimal("Precio de venta: ");
        int stock = leerEntero("Stock inicial: ");
        int stockMinimo = leerEntero("Stock mínimo (10): ");
        if (stockMinimo <= 0) stockMinimo = 10;
        int stockMaximo = leerEntero("Stock máximo (100): ");
        if (stockMaximo <= 0) stockMaximo = 100;
        String categoria = leerTexto("Categoría: ");

        Producto producto = new Producto(codigo, nombre, precioVenta, stock, categoria);
        producto.setPrecioCompra(precioCompra);
        producto.setStockMinimo(stockMinimo);
        producto.setStockMaximo(stockMaximo);

        productoService.registrarProducto(producto);
    }

    private void buscarProductoCodigo() {
        System.out.println("\n════════════ BUSCAR PRODUCTO POR CÓDIGO ════════════");
        String codigo = leerTexto("Ingrese el código: ");

        Producto producto = productoService.buscarPorCodigo(codigo);
        if (producto != null) {
            System.out.println("\n✅ PRODUCTO ENCONTRADO:");
            System.out.println("Código: " + producto.getCodigo());
            System.out.println("Nombre: " + producto.getNombre());
            System.out.println("Descripción: " + producto.getDescripcion());
            System.out.println("Precio de compra: $" + producto.getPrecioCompra());
            System.out.println("Precio de venta: $" + producto.getPrecioVenta());
            System.out.println("Stock actual: " + producto.getStockActual());
            System.out.println("Stock mínimo: " + producto.getStockMinimo());
            System.out.println("Categoría: " + producto.getCategoria());
            System.out.println("Proveedor: " + producto.getProveedor());
            System.out.println("¿Necesita reposición? " + (producto.necesitaReposicion() ? "SÍ ⚠️" : "NO ✅"));
        } else {
            System.out.println("❌ Producto no encontrado");
        }
    }

    private void buscarProductoNombre() {
        System.out.println("\n════════════ BUSCAR PRODUCTO POR NOMBRE ════════════");
        String nombre = leerTexto("Ingrese parte del nombre: ");

        List<Producto> resultados = productoService.buscarPorNombre(nombre);
        if (!resultados.isEmpty()) {
            System.out.println("\n🔍 RESULTADOS DE BÚSQUEDA (" + resultados.size() + " productos):");
            for (Producto producto : resultados) {
                System.out.println("  • " + producto.toSimpleString());
            }
        } else {
            System.out.println("❌ No se encontraron productos con ese nombre");
        }
    }

    private void actualizarStock() {
        System.out.println("\n════════════ ACTUALIZAR STOCK ════════════");
        String codigo = leerTexto("Código del producto: ");

        Producto producto = productoService.buscarPorCodigo(codigo);
        if (producto == null) {
            System.out.println("❌ Producto no encontrado");
            return;
        }

        System.out.println("Producto: " + producto.getNombre());
        System.out.println("Stock actual: " + producto.getStockActual());
        System.out.println("Stock mínimo: " + producto.getStockMinimo());

        System.out.println("\n1. Aumentar stock");
        System.out.println("2. Disminuir stock");
        System.out.println("3. Establecer nuevo valor");
        int opcion = leerEntero("Seleccione: ");

        switch (opcion) {
            case 1:
                int aumento = leerEntero("Cantidad a aumentar: ");
                String motivoAumento = leerTexto("Motivo del aumento: ");
                int nuevoStockAumento = producto.getStockActual() + aumento;
                productoService.actualizarStock(codigo, nuevoStockAumento, motivoAumento);
                break;
            case 2:
                int disminucion = leerEntero("Cantidad a disminuir: ");
                if (disminucion > producto.getStockActual()) {
                    System.out.println("❌ No se puede reducir más del stock actual");
                    return;
                }
                String motivoDisminucion = leerTexto("Motivo de la disminución: ");
                int nuevoStockDisminucion = producto.getStockActual() - disminucion;
                productoService.actualizarStock(codigo, nuevoStockDisminucion, motivoDisminucion);
                break;
            case 3:
                int nuevoStock = leerEntero("Nuevo stock: ");
                String motivo = leerTexto("Motivo del cambio: ");
                productoService.actualizarStock(codigo, nuevoStock, motivo);
                break;
            default:
                System.out.println("❌ Opción no válida");
        }
    }

    private void menuGestionVentas() {
        boolean volver = false;

        while (!volver) {
            System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    🛒 GESTIÓN DE VENTAS                            ║");
            System.out.println("╠════════════════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. 🧾 Procesar nueva venta                                         ║");
            System.out.println("║ 2. 📈 Ver estadísticas de ventas                                   ║");
            System.out.println("║ 3. 📦 Procesar pedidos pendientes                                  ║");
            System.out.println("║ 0. ↩️  Volver al menú principal                                     ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝");

            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    procesarVenta();
                    break;
                case 2:
                    ventaService.mostrarEstadisticasVentas();
                    break;
                case 3:
                    productoService.procesarPedidosPendientes();
                    break;
                case 0:
                    volver = true;
                    break;
                default:
                    System.out.println("❌ Opción no válida");
            }
        }
    }

    private void procesarVenta() {
        System.out.println("\n════════════ PROCESAR NUEVA VENTA ════════════");

        String cliente = leerTexto("Nombre del cliente: ");
        Venta venta = new Venta(cliente);

        boolean agregarMas = true;
        while (agregarMas) {
            System.out.println("\n➕ AGREGAR PRODUCTO A LA VENTA");
            String codigo = leerTexto("Código del producto: ");

            Producto producto = productoService.buscarPorCodigo(codigo);
            if (producto == null) {
                System.out.println("❌ Producto no encontrado");
                continue;
            }

            System.out.println("✅ Producto: " + producto.getNombre());
            System.out.println("💰 Precio: $" + producto.getPrecioVenta());
            System.out.println("📦 Stock disponible: " + producto.getStockActual());

            int cantidad = leerEntero("Cantidad a vender: ");

            if (cantidad <= 0) {
                System.out.println("❌ Cantidad debe ser mayor a 0");
                continue;
            }

            if (!producto.hayStockSuficiente(cantidad)) {
                System.out.println("❌ Stock insuficiente. Disponible: " + producto.getStockActual());
                continue;
            }

            DetalleVenta detalle = new DetalleVenta();
            detalle.setIdProducto(producto.getId());
            detalle.setNombreProducto(producto.getNombre());
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecioVenta());

            venta.agregarDetalle(detalle);
            System.out.println("✅ Producto agregado: " + producto.getNombre() +
                    " x" + cantidad + " = $" + detalle.getSubtotal());
            System.out.println("📉 Stock después de la venta: " + (producto.getStockActual() - cantidad));

            System.out.print("\n¿Agregar otro producto? (S/N): ");
            agregarMas = scanner.nextLine().equalsIgnoreCase("S");
        }

        if (venta.getDetalles().isEmpty()) {
            System.out.println("❌ Venta cancelada - No se agregaron productos");
            return;
        }

        System.out.println("\n💳 MÉTODO DE PAGO");
        System.out.println("1. EFECTIVO");
        System.out.println("2. TARJETA BANCARIA");
        int opcionPago = leerEntero("Seleccione: ");

        String metodoPago = "EFECTIVO";
        if (opcionPago == 2) {
            metodoPago = "TARJETA BANCARIA";
        } else if (opcionPago != 1) {
            System.out.println("❌ Opción no válida, se asignará EFECTIVO por defecto");
        }

        venta.setMetodoPago(metodoPago);

        System.out.println("\n📋 RESUMEN DE LA VENTA");
        System.out.println("Cliente: " + venta.getCliente());
        System.out.println("Productos: " + venta.getDetalles().size());
        System.out.println("Total: $" + venta.getTotal());
        System.out.println("Método de pago: " + venta.getMetodoPago());

        System.out.print("\n¿Confirmar venta? (S/N): ");
        boolean confirmar = scanner.nextLine().equalsIgnoreCase("S");

        if (confirmar) {
            if (ventaService.procesarVenta(venta)) {
                System.out.println("✅ Venta procesada exitosamente");
            } else {
                System.out.println("❌ Error al procesar la venta");
            }
        } else {
            System.out.println("❌ Venta cancelada por el usuario");
        }
    }

    private void menuReportes() {
        boolean volver = false;

        while (!volver) {
            System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    📊 REPORTES Y CONSULTAS                         ║");
            System.out.println("╠════════════════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. 📦 Inventario completo                                          ║");
            System.out.println("║ 2. ⚠️ Productos con stock bajo                                     ║");
            System.out.println("║ 3. 📈 Estadísticas de ventas                                       ║");
            System.out.println("║ 4. 📂 Categorías de productos                                      ║");
            System.out.println("║ 0. ↩️ Volver al menú principal                                     ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝");

            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    productoService.mostrarInventario();
                    break;
                case 2:
                    productoService.mostrarProductosBajoStock();
                    break;
                case 3:
                    ventaService.mostrarEstadisticasVentas();
                    break;
                case 4:
                    productoService.mostrarCategorias();
                    break;
                case 0:
                    volver = true;
                    break;
                default:
                    System.out.println("❌ Opción no válida");
            }
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor ingrese un número válido");
            }
        }
    }

    private double leerDecimal(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor ingrese un número válido (ej: 99.99)");
            }
        }
    }
}