package servicio;

import modelo.ArbolABB;
import modelo.GrafoDirigido;
import modelo.Tarea;

import java.util.List;

public class GestorProyecto {
    private ArbolABB arbol;
    private GrafoDirigido grafo;

    public GestorProyecto() {
        this.arbol = new ArbolABB();
        this.grafo = new GrafoDirigido();
    }

    public boolean agregarTarea(int id, String nombre, String descripcion, int prioridad) {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("El ID debe ser un número positivo");
            }
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío");
            }
            if (descripcion == null || descripcion.trim().isEmpty()) {
                throw new IllegalArgumentException("La descripción no puede estar vacía");
            }
            if (prioridad < 1 || prioridad > 5) {
                throw new IllegalArgumentException("La prioridad debe estar entre 1 y 5");
            }
            if (arbol.buscar(id) != null) {
                throw new IllegalStateException("Ya existe una tarea con el ID " + id);
            }
            Tarea tarea = new Tarea(id, nombre.trim(), descripcion.trim(), prioridad);
            arbol.insertar(tarea);
            grafo.agregarVertice(id);
            System.out.println("Tarea agregada exitosamente.");
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
    }

    public Tarea buscarTarea(int id) {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("El ID debe ser un número positivo");
            }
            Tarea tarea = arbol.buscar(id);
            if (tarea == null) {
                throw new IllegalStateException("No existe una tarea con el ID " + id);
            }
            return tarea;
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }
    }

    public boolean eliminarTarea(int id) {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("El ID debe ser un número positivo");
            }
            if (arbol.buscar(id) == null) {
                throw new IllegalStateException("No existe una tarea con el ID " + id);
            }
            grafo.eliminarVertice(id);
            arbol.eliminar(id);
            System.out.println("Tarea eliminada exitosamente.");
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarTarea(int id, String nombre, String descripcion, int prioridad) {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("El ID debe ser un número positivo");
            }
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío");
            }
            if (descripcion == null || descripcion.trim().isEmpty()) {
                throw new IllegalArgumentException("La descripción no puede estar vacía");
            }
            if (prioridad < 1 || prioridad > 5) {
                throw new IllegalArgumentException("La prioridad debe estar entre 1 y 5");
            }
            if (arbol.buscar(id) == null) {
                throw new IllegalStateException("No existe una tarea con el ID " + id);
            }
            Tarea tareaModificada = new Tarea(id, nombre.trim(), descripcion.trim(), prioridad);
            arbol.modificar(tareaModificada);
            System.out.println("Tarea modificada exitosamente.");
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
    }

    public boolean agregarDependencia(int origen, int destino) {
        try {
            if (origen <= 0 || destino <= 0) {
                throw new IllegalArgumentException("Los IDs deben ser números positivos");
            }
            if (arbol.buscar(origen) == null) {
                throw new IllegalStateException("No existe la tarea origen con ID " + origen);
            }
            if (arbol.buscar(destino) == null) {
                throw new IllegalStateException("No existe la tarea destino con ID " + destino);
            }
            grafo.agregarArista(origen, destino);
            System.out.println("Dependencia agregada: " + origen + " -> " + destino);
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarDependencia(int origen, int destino) {
        try {
            if (origen <= 0 || destino <= 0) {
                throw new IllegalArgumentException("Los IDs deben ser números positivos");
            }
            grafo.eliminarArista(origen, destino);
            System.out.println("Dependencia eliminada exitosamente.");
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
    }

    public void mostrarDependencias() {
        try {
            grafo.mostrarAdyacencias();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    public void mostrarOrdenTopologico() {
        try {
            List<Integer> orden = grafo.ordenTopologico();
            if (orden.isEmpty()) {
                System.out.println("No hay tareas para ordenar.");
                return;
            }
            System.out.println("=== ORDEN TOPOLÓGICO DE EJECUCIÓN ===");
            int posicion = 1;
            for (int id : orden) {
                Tarea tarea = arbol.buscar(id);
                String nombre = tarea != null ? tarea.getNombre() : "[Tarea no encontrada]";
                System.out.println(posicion + ". ID:" + id + " - " + nombre);
                posicion++;
            }
        } catch (IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    public void mostrarArbolInorden() {
        try {
            System.out.println("=== ÁRBOL (INORDEN) ===");
            List<Tarea> lista = arbol.inorden();
            if (lista.isEmpty()) {
                System.out.println("No hay tareas registradas.");
            } else {
                for (Tarea t : lista) {
                    System.out.println(t);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    public void mostrarArbolPreorden() {
        try {
            System.out.println("=== ÁRBOL (PREORDEN) ===");
            List<Tarea> lista = arbol.preorden();
            if (lista.isEmpty()) {
                System.out.println("No hay tareas registradas.");
            } else {
                for (Tarea t : lista) {
                    System.out.println(t);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    public void mostrarArbolPostorden() {
        try {
            System.out.println("=== ÁRBOL (POSTORDEN) ===");
            List<Tarea> lista = arbol.postorden();
            if (lista.isEmpty()) {
                System.out.println("No hay tareas registradas.");
            } else {
                for (Tarea t : lista) {
                    System.out.println(t);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    public boolean tieneCiclos() {
        try {
            return grafo.tieneCiclo();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
    }

    public void mostrarEstadoProyecto() {
        try {
            System.out.println("=== ESTADO DEL PROYECTO ===");
            System.out.println("Tareas registradas: " + arbol.inorden().size());
            System.out.println("Dependencias: " + contarDependencias());
            System.out.println("Ciclos detectados: " + (tieneCiclos() ? "SÍ" : "NO"));
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private int contarDependencias() {
        int contador = 0;
        for (int v : grafo.getVertices()) {
            contador += grafo.getAdyacentes(v).size();
        }
        return contador;
    }
}