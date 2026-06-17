package modelo;

public class NodoABB {
    private Tarea tarea;
    private NodoABB izquierdo;
    private NodoABB derecho;

    public NodoABB(Tarea tarea) {
        this.tarea = tarea;
        this.izquierdo = null;
        this.derecho = null;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    public NodoABB getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoABB izquierdo) {
        this.izquierdo = izquierdo;
    }

    public NodoABB getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoABB derecho) {
        this.derecho = derecho;
    }
}