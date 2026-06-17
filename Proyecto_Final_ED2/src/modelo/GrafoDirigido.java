package modelo;

import java.util.*;

public class GrafoDirigido {
    private Map<Integer, Set<Integer>> adyacencia;
    private Set<Integer> vertices;

    public GrafoDirigido() {
        this.adyacencia = new HashMap<>();
        this.vertices = new HashSet<>();
    }

    public void agregarVertice(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        if (!vertices.contains(id)) {
            vertices.add(id);
            adyacencia.put(id, new HashSet<>());
        }
    }

    public boolean eliminarVertice(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        if (!vertices.contains(id)) {
            throw new IllegalStateException("No existe el vértice con ID " + id);
        }
        for (int origen : adyacencia.keySet()) {
            adyacencia.get(origen).remove(id);
        }
        adyacencia.remove(id);
        vertices.remove(id);
        return true;
    }

    public boolean existeVertice(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        return vertices.contains(id);
    }

    public boolean agregarArista(int origen, int destino) {
        if (origen <= 0 || destino <= 0) {
            throw new IllegalArgumentException("Los IDs deben ser mayores a 0");
        }
        if (!existeVertice(origen)) {
            throw new IllegalStateException("No existe el vértice origen con ID " + origen);
        }
        if (!existeVertice(destino)) {
            throw new IllegalStateException("No existe el vértice destino con ID " + destino);
        }
        if (origen == destino) {
            throw new IllegalArgumentException("Una tarea no puede depender de sí misma");
        }
        if (existeArista(origen, destino)) {
            throw new IllegalStateException("La dependencia ya existe: " + origen + " -> " + destino);
        }
        if (creaCiclo(origen, destino)) {
            throw new IllegalStateException("La dependencia crearía un ciclo en el grafo");
        }
        adyacencia.get(origen).add(destino);
        return true;
    }

    public boolean eliminarArista(int origen, int destino) {
        if (origen <= 0 || destino <= 0) {
            throw new IllegalArgumentException("Los IDs deben ser mayores a 0");
        }
        if (!existeVertice(origen) || !existeVertice(destino)) {
            throw new IllegalStateException("Una o ambas tareas no existen");
        }
        if (!existeArista(origen, destino)) {
            throw new IllegalStateException("La dependencia " + origen + " -> " + destino + " no existe");
        }
        return adyacencia.get(origen).remove(destino);
    }

    public boolean existeArista(int origen, int destino) {
        if (origen <= 0 || destino <= 0) {
            throw new IllegalArgumentException("Los IDs deben ser mayores a 0");
        }
        if (!existeVertice(origen) || !existeVertice(destino)) {
            return false;
        }
        return adyacencia.get(origen).contains(destino);
    }

    public Set<Integer> getAdyacentes(int vertice) {
        if (vertice <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        if (!existeVertice(vertice)) {
            throw new IllegalStateException("No existe el vértice con ID " + vertice);
        }
        return Collections.unmodifiableSet(adyacencia.get(vertice));
    }

    public boolean tieneCiclo() {
        Map<Integer, Integer> estado = new HashMap<>();
        for (int v : vertices) {
            estado.put(v, 0);
        }
        for (int v : vertices) {
            if (estado.get(v) == 0) {
                if (dfsCiclo(v, estado)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfsCiclo(int v, Map<Integer, Integer> estado) {
        estado.put(v, 1);
        for (int vecino : adyacencia.get(v)) {
            if (estado.get(vecino) == 1) {
                return true;
            }
            if (estado.get(vecino) == 0) {
                if (dfsCiclo(vecino, estado)) {
                    return true;
                }
            }
        }
        estado.put(v, 2);
        return false;
    }

    private boolean creaCiclo(int origen, int destino) {
        adyacencia.get(origen).add(destino);
        boolean ciclo = tieneCiclo();
        adyacencia.get(origen).remove(destino);
        return ciclo;
    }

    public List<Integer> ordenTopologico() {
        if (tieneCiclo()) {
            throw new IllegalStateException("El grafo tiene ciclos, no se puede obtener el orden topológico");
        }
        if (vertices.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Integer, Integer> gradoEntrada = new HashMap<>();
        for (int v : vertices) {
            gradoEntrada.put(v, 0);
        }
        for (int v : vertices) {
            for (int vecino : adyacencia.get(v)) {
                gradoEntrada.put(vecino, gradoEntrada.get(vecino) + 1);
            }
        }
        Queue<Integer> cola = new LinkedList<>();
        for (int v : vertices) {
            if (gradoEntrada.get(v) == 0) {
                cola.add(v);
            }
        }
        List<Integer> orden = new ArrayList<>();
        while (!cola.isEmpty()) {
            int actual = cola.poll();
            orden.add(actual);
            for (int vecino : adyacencia.get(actual)) {
                gradoEntrada.put(vecino, gradoEntrada.get(vecino) - 1);
                if (gradoEntrada.get(vecino) == 0) {
                    cola.add(vecino);
                }
            }
        }
        if (orden.size() != vertices.size()) {
            throw new IllegalStateException("Error en el ordenamiento topológico");
        }
        return orden;
    }

    public Set<Integer> getVertices() {
        return Collections.unmodifiableSet(vertices);
    }

    public void mostrarAdyacencias() {
        System.out.println("=== DEPENDENCIAS DEL GRAFO ===");
        if (vertices.isEmpty()) {
            System.out.println("No hay tareas registradas.");
            return;
        }
        for (int v : vertices) {
            System.out.print("Tarea " + v + " -> ");
            Set<Integer> destinos = adyacencia.get(v);
            if (destinos.isEmpty()) {
                System.out.println("(sin dependencias)");
            } else {
                System.out.println(destinos);
            }
        }
    }
}