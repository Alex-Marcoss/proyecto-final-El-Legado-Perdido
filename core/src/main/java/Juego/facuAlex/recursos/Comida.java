package Juego.facuAlex.recursos;

public class Comida extends Recursos {

    private int hambreRecuperada;

    public Comida(String nombre, int cantidad, int hambreRecuperada) {

        super(nombre, cantidad);
        this.hambreRecuperada = hambreRecuperada;
    }

    public int getHambreRecuperada() {
        return hambreRecuperada;
    }

    @Override
    protected Recursos crearCopia(int n) {
        return new Comida(nombre, n, hambreRecuperada);
    }

    @Override
    public void mostrarInfo() {
        System.out.println(
            nombre + ": " + getCantidad()
            + " | Hambre: +" + hambreRecuperada
        );
    }
}

