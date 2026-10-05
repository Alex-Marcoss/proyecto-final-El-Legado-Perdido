package Juego.facuAlex.enemigos;

import Juego.facuAlex.jugador.*;

public class Enemigo {

    private String nombre;
    private int vida;
    private int daño;

    private float posicionX;
    private float posicionY;

    public Enemigo(String nombre, int vida, int daño) {

        this.nombre = nombre;
        this.vida = vida;
        this.daño = daño;

        posicionX = 0;
        posicionY = 0;
    }

    // =========================================================
    // INFORMACIÓN
    // =========================================================

    public String getNombre() {
        return nombre;
    }

    public int getVida() {
        return vida;
    }

    public int getDaño() {
        return daño;
    }

    // =========================================================
    // POSICIÓN
    // =========================================================

    public float getPosicionX() {
        return posicionX;
    }

    public float getPosicionY() {
        return posicionY;
    }

    public void setPosicion(float x, float y) {

        posicionX = x;
        posicionY = y;
    }

    // =========================================================
    // DAÑO
    // =========================================================

    public void recibirDaño(int cantidad) {

        vida -= cantidad;

        if (vida < 0) {
            vida = 0;
        }
    }

    // =========================================================
    // ESTADO
    // =========================================================

    public boolean estaVivo() {
        return vida > 0;
    }

    public void mostrarEstado() {

        System.out.println(
            "Enemigo: " + nombre
        );

        System.out.println(
            "Vida: " + vida
        );

        System.out.println(
            "Daño: " + daño
        );
    }

    // =========================================================
    // ATAQUE
    // =========================================================

    public void atacar(Jugador jugador) {

        if (jugador == null) {
            return;
        }

        if (!estaVivo()) {
            return;
        }

        if (!jugador.estaVivo()) {
            return;
        }

        jugador.recibirDanio(daño);

        System.out.println(
            getNombre() +
            " te atacó y causó " +
            daño +
            " de daño."
        );
    }
}