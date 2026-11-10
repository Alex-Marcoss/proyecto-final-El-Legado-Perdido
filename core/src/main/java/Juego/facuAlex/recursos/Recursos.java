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

    // Separa "n" unidades de este stack y las devuelve como un stack
    // nuevo del mismo tipo (con el mismo icono). Devuelve null si no
    // se puede (n invalido, o no quedaria al menos 1 en este stack).
    public Recursos separar(int n) {

        if (n <= 0 || n >= cantidad) {
            return null;
        }

        Recursos nuevo = crearCopia(n);
        nuevo.copiarIconoDe(this);

        cantidad -= n;

        return nuevo;
    }

    // Las subclases con datos propios (ej: Comida) la redefinen
    protected Recursos crearCopia(int n) {
        return new Recursos(nombre, n);
    }
}
