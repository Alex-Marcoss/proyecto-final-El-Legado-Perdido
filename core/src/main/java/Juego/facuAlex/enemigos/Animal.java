package Juego.facuAlex.enemigos;

import Juego.facuAlex.recursos.Recursos;

public class Animal extends Enemigo {

    private Recursos[] drops;
    private boolean dropsObtenidos;

    public Animal(String nombre, int vida, int daño, Recursos[] drops) {

        super(nombre, vida, daño);

        this.drops = drops;
        this.dropsObtenidos = false;
    }

    public Recursos[] obtenerDrops() {

        if (!estaVivo() && !dropsObtenidos) {

            dropsObtenidos = true;

            return drops;
        }

        return null;
    }
}
