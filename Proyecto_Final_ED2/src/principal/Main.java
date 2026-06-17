package principal;

import servicio.GestorProyecto;
import modelo.Tarea;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static GestorProyecto gestor = new GestorProyecto();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion = -1;
        do {
            try {
                mostrarMenu();
                opcion = leerOpcion();
                ejecutarOpcion(opcion);
            } catch (InputMismatchException e) {
                System.out.println("ERROR: Entrada inválida. Por favor ingrese un número.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        } while (opcion != 0);
        System.out.println("Sistema finalizado.");
        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE GESTIÓN DE PROYECTOS        ║");
        System.out.println("╠════════════════════════════════════════════╣");
        System.out.println("║ 1. Agregar tarea                         ║");
        System.out.println("║ 2. Buscar tarea                          ║");
        System.out.println("║ 3. Eliminar tarea                        ║");
        System.out.println("║ 4. Modificar tarea                       ║");
        System.out.println("║ 5. Mostrar árbol (inorden)              ║");
        System.out.println("║ 6. Mostrar árbol (preorden)             ║");
        System.out.println("║ 7. Mostrar árbol (postorden)            ║");
        System.out.println("║ 8. Agregar dependencia                  ║");
        System.out.println("║ 9. Eliminar dependencia                 ║");
        System.out.println("║ 10. Mostrar dependencias                ║");
        System.out.println("║ 11. Mostrar orden topológico            ║");
        System.out.println("║ 12. Ver estado del proyecto             ║");
        System.out.println("║ 0. Salir                                ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.print("Seleccione una opción: ");
    }

    private static int leerOpcion() {
        try {
            String entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) {
                System.out.println("ERROR: No se ingresó ningún valor.");
                return -1;
            }
            return Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Ingrese un número válido.");
            return -1;
        }
    }

    private static void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1: agregarTarea(); break;
            case 2: buscarTarea(); break;
            case 3: eliminarTarea(); break;
            case 4: modificarTarea(); break;
            case 5: gestor.mostrarArbolInorden(); break;
            case 6: gestor.mostrarArbolPreorden(); break;
            case 7: gestor.mostrarArbolPostorden(); break;
            case 8: agregarDependencia(); break;
            case 9: eliminarDependencia(); break;
            case 10: gestor.mostrarDependencias(); break;
            case 11: gestor.mostrarOrdenTopologico(); break;
            case 12: gestor.mostrarEstadoProyecto(); break;
            case 0: break;
            default: System.out.println("ERROR: Opción no válida. Seleccione un número entre 0 y 12.");
        }
    }

    private static void agregarTarea() {
        try {
            System.out.print("ID de la tarea: ");
            int id = leerEnteroPositivo();
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim();
            System.out.print("Descripción: ");
            String descripcion = scanner.nextLine().trim();
            System.out.print("Prioridad (1-5): ");
            int prioridad = leerEnteroRango(1, 5);
            gestor.agregarTarea(id, nombre, descripcion, prioridad);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void buscarTarea() {
        try {
            System.out.print("ID de la tarea a buscar: ");
            int id = leerEnteroPositivo();
            Tarea tarea = gestor.buscarTarea(id);
            if (tarea != null) {
                System.out.println("Tarea encontrada:");
                System.out.println(tarea);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void eliminarTarea() {
        try {
            System.out.print("ID de la tarea a eliminar: ");
            int id = leerEnteroPositivo();
            gestor.eliminarTarea(id);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void modificarTarea() {
        try {
            System.out.print("ID de la tarea a modificar: ");
            int id = leerEnteroPositivo();
            System.out.print("Nuevo nombre: ");
            String nombre = scanner.nextLine().trim();
            System.out.print("Nueva descripción: ");
            String descripcion = scanner.nextLine().trim();
            System.out.print("Nueva prioridad (1-5): ");
            int prioridad = leerEnteroRango(1, 5);
            gestor.modificarTarea(id, nombre, descripcion, prioridad);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void agregarDependencia() {
        try {
            System.out.print("ID de la tarea origen: ");
            int origen = leerEnteroPositivo();
            System.out.print("ID de la tarea destino: ");
            int destino = leerEnteroPositivo();
            gestor.agregarDependencia(origen, destino);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void eliminarDependencia() {
        try {
            System.out.print("ID de la tarea origen: ");
            int origen = leerEnteroPositivo();
            System.out.print("ID de la tarea destino: ");
            int destino = leerEnteroPositivo();
            gestor.eliminarDependencia(origen, destino);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static int leerEnteroPositivo() {
        try {
            String entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) {
                throw new IllegalArgumentException("El campo no puede estar vacío");
            }
            int valor = Integer.parseInt(entrada);
            if (valor <= 0) {
                throw new IllegalArgumentException("El número debe ser mayor a 0");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ingrese un número válido");
        }
    }

    private static int leerEnteroRango(int min, int max) {
        try {
            String entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) {
                throw new IllegalArgumentException("El campo no puede estar vacío");
            }
            int valor = Integer.parseInt(entrada);
            if (valor < min || valor > max) {
                throw new IllegalArgumentException("El número debe estar entre " + min + " y " + max);
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ingrese un número válido");
        }
    }
}