package Juego.facuAlex.receta;

public class Ingrediente {

    private String nombreRecurso;
    private int cantidad;

    public Ingrediente(String nombreRecurso, int cantidad) {
        this.nombreRecurso = nombreRecurso;
        this.cantidad = cantidad;
    }

    public String getNombreRecurso() {
        return nombreRecurso;
    }

    public int getCantidad() {
        return cantidad;
    }
    
    
}