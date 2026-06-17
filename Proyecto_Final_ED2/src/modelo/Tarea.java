package modelo;

public class Tarea {
    private int id;
    private String nombre;
    private String descripcion;
    private int prioridad;

    public Tarea(int id, String nombre, String descripcion, int prioridad) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    @Override
    public String toString() {
        return "Tarea{id=" + id + ", nombre='" + nombre + "', descripcion='" + descripcion + "', prioridad=" + prioridad + "}";
    }
}