package modelo;

import java.util.ArrayList;
import java.util.List;

public class ArbolABB {
    private NodoABB raiz;

    public ArbolABB() {
        this.raiz = null;
    }

    public void insertar(Tarea tarea) {
        if (tarea == null) {
            throw new IllegalArgumentException("La tarea no puede ser nula");
        }
        if (buscar(tarea.getId()) != null) {
            throw new IllegalStateException("Ya existe una tarea con el ID " + tarea.getId());
        }
        raiz = insertarRec(raiz, tarea);
    }

    private NodoABB insertarRec(NodoABB nodo, Tarea tarea) {
        if (nodo == null) {
            return new NodoABB(tarea);
        }
        if (tarea.getId() < nodo.getTarea().getId()) {
            nodo.setIzquierdo(insertarRec(nodo.getIzquierdo(), tarea));
        } else if (tarea.getId() > nodo.getTarea().getId()) {
            nodo.setDerecho(insertarRec(nodo.getDerecho(), tarea));
        }
        return nodo;
    }

    public Tarea buscar(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        NodoABB nodo = buscarRec(raiz, id);
        return nodo != null ? nodo.getTarea() : null;
    }

    private NodoABB buscarRec(NodoABB nodo, int id) {
        if (nodo == null || nodo.getTarea().getId() == id) {
            return nodo;
        }
        return id < nodo.getTarea().getId()
                ? buscarRec(nodo.getIzquierdo(), id)
                : buscarRec(nodo.getDerecho(), id);
    }

    public boolean eliminar(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        if (buscar(id) == null) {
            throw new IllegalStateException("No existe una tarea con el ID " + id);
        }
        raiz = eliminarRec(raiz, id);
        return true;
    }

    private NodoABB eliminarRec(NodoABB nodo, int id) {
        if (nodo == null) {
            return null;
        }
        if (id < nodo.getTarea().getId()) {
            nodo.setIzquierdo(eliminarRec(nodo.getIzquierdo(), id));
        } else if (id > nodo.getTarea().getId()) {
            nodo.setDerecho(eliminarRec(nodo.getDerecho(), id));
        } else {
            if (nodo.getIzquierdo() == null && nodo.getDerecho() == null) {
                return null;
            }
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();
            }
            if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();
            }
            NodoABB sucesor = encontrarMin(nodo.getDerecho());
            nodo.setTarea(sucesor.getTarea());
            nodo.setDerecho(eliminarRec(nodo.getDerecho(), sucesor.getTarea().getId()));
        }
        return nodo;
    }

    private NodoABB encontrarMin(NodoABB nodo) {
        if (nodo == null) {
            throw new IllegalStateException("El nodo no puede ser nulo");
        }
        while (nodo.getIzquierdo() != null) {
            nodo = nodo.getIzquierdo();
        }
        return nodo;
    }

    public boolean modificar(Tarea tareaActualizada) {
        if (tareaActualizada == null) {
            throw new IllegalArgumentException("La tarea no puede ser nula");
        }
        if (tareaActualizada.getId() <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        Tarea existente = buscar(tareaActualizada.getId());
        if (existente == null) {
            throw new IllegalStateException("No existe una tarea con el ID " + tareaActualizada.getId());
        }
        existente.setNombre(tareaActualizada.getNombre());
        existente.setDescripcion(tareaActualizada.getDescripcion());
        existente.setPrioridad(tareaActualizada.getPrioridad());
        return true;
    }

    public List<Tarea> inorden() {
        List<Tarea> lista = new ArrayList<>();
        inordenRec(raiz, lista);
        return lista;
    }

    private void inordenRec(NodoABB nodo, List<Tarea> lista) {
        if (nodo != null) {
            inordenRec(nodo.getIzquierdo(), lista);
            lista.add(nodo.getTarea());
            inordenRec(nodo.getDerecho(), lista);
        }
    }

    public List<Tarea> preorden() {
        List<Tarea> lista = new ArrayList<>();
        preordenRec(raiz, lista);
        return lista;
    }

    private void preordenRec(NodoABB nodo, List<Tarea> lista) {
        if (nodo != null) {
            lista.add(nodo.getTarea());
            preordenRec(nodo.getIzquierdo(), lista);
            preordenRec(nodo.getDerecho(), lista);
        }
    }

    public List<Tarea> postorden() {
        List<Tarea> lista = new ArrayList<>();
        postordenRec(raiz, lista);
        return lista;
    }

    private void postordenRec(NodoABB nodo, List<Tarea> lista) {
        if (nodo != null) {
            postordenRec(nodo.getIzquierdo(), lista);
            postordenRec(nodo.getDerecho(), lista);
            lista.add(nodo.getTarea());
        }
    }

    public boolean estaVacio() {
        return raiz == null;
    }
}