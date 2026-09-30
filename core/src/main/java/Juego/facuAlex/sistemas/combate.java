package Juego.facuAlex.sistemas;

import Juego.facuAlex.Jugador;
import Juego.facuAlex.enemigos.Enemigo;

public class combate {

    private static final int ENERGIA_ATAQUE = 5;
    private static final float RANGO_ATAQUE = 90f;

    public boolean atacar(Jugador jugador, Enemigo enemigo) {

        if (jugador == null || enemigo == null) {
            return false;
        }

        if (!jugador.estaVivo()) {
            return false;
        }

        if (!enemigo.estaVivo()) {
            System.out.println(
                enemigo.getNombre() +
                " ya esta derrotado."
            );
            return false;
        }

        float diferenciaX =
            enemigo.getPosicionX() - jugador.getPosicionX();

        float diferenciaY =
            enemigo.getPosicionY() - jugador.getPosicionY();

        float distancia =
            (float) Math.sqrt(
                diferenciaX * diferenciaX +
                diferenciaY * diferenciaY
            );

        if (distancia > RANGO_ATAQUE) {
            System.out.println(
                "El Guardian esta demasiado lejos."
            );
            return false;
        }

        if (jugador.getEnergia() < ENERGIA_ATAQUE) {
            System.out.println(
                "No tenes suficiente energia para atacar."
            );
            return false;
        }

        jugador.atacar(enemigo);

        jugador.gastarEnergia(ENERGIA_ATAQUE);

        return true;
    }

    public int getEnergiaAtaque() {
        return ENERGIA_ATAQUE;
    }

    public float getRangoAtaque() {
        return RANGO_ATAQUE;
    }
}