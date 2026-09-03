package Juego.facuAlex.sistemas;

import Juego.facuAlex.Jugador;
import Juego.facuAlex.enemigos.Enemigo;

public class combate {

    private static final int ENERGIA_ATAQUE = 5;

    public void atacar(Jugador jugador, Enemigo enemigo) {

        if (jugador == null || enemigo == null) {
            return;
        }

        if (!jugador.estaVivo()) {
            return;
        }

        if (!enemigo.estaVivo()) {
            System.out.println(
                enemigo.getNombre() +
                " ya esta derrotado."
            );
            return;
        }

        if (jugador.getEnergia() < ENERGIA_ATAQUE) {
            System.out.println(
                "No tenes suficiente energia para atacar."
            );
            return;
        }

        jugador.atacar(enemigo);

        // El enemigo SOLO ataca si ambos siguen vivos
        if (jugador.estaVivo() && enemigo.estaVivo()) {
            enemigo.atacar(jugador);
        }
    }
}