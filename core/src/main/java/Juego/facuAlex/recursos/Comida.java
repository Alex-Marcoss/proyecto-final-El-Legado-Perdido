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
    public void mostrarInfo() {
        System.out.println(
            nombre + ": " + getCantidad()
            + " | Hambre: +" + hambreRecuperada
        );
    }
}