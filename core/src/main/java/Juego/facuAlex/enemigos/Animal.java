package Juego.facuAlex.enemigos;

import Juego.facuAlex.recursos.Comida;

public class Animal extends Enemigo {

    private Comida comida;
    private boolean comidaObtenida;

    public Animal(String nombre, int vida, int daño, Comida comida) {

        super(nombre, vida, daño);
        this.comida = comida;
        this.comidaObtenida = false;
    }

    public Comida obtenerComida() {

        if (!estaVivo() && !comidaObtenida) {

            comidaObtenida = true;

            return comida;
        }

        return null;
    }
}