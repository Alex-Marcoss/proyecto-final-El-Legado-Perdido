package Juego.facuAlex.recursos;

public class Recursos extends Item {

    protected int cantidad;

    public Recursos(String nombre, int cantidad) {
        super(nombre);
        this.cantidad = cantidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    @Override
    public void agregarCantidad(int cantidad) {
        this.cantidad += cantidad;
    }
}