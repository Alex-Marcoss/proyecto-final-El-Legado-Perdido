package Juego.facuAlex.sistemas;

import Juego.facuAlex.recursos.Recursos;

public class Comida extends Recursos {

    private int hambreRecuperada;

    public Comida(
        String nombre,
        int cantidad,
        int hambreRecuperada
    ) {

        super(nombre, cantidad);

        this.hambreRecuperada = hambreRecuperada;
    }

    public int getHambreRecuperada() {
        return hambreRecuperada;
    }
}