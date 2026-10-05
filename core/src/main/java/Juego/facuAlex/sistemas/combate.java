package Juego.facuAlex.sistemas;

import Juego.facuAlex.jugador.*;
import Juego.facuAlex.enemigos.Enemigo;

public class combate {

    private static final int ENERGIA_ATAQUE = 5;

    // Distancia máxima a la que llega el ataque
    private static final float RANGO_ATAQUE = 90f;

    // Ancho del área de ataque
    private static final float ANCHO_ATAQUE = 45f;

    // Daño del jugador
    private static final int DAÑO_JUGADOR = 20;

    public boolean atacar(
            Jugador jugador,
            Enemigo enemigo,
            JugadorControl.Direccion direccion) {

        if (jugador == null || enemigo == null) {
            return false;
        }

        if (direccion == null) {
            return false;
        }

        if (!jugador.estaVivo()) {
            return false;
        }

        if (!enemigo.estaVivo()) {
            return false;
        }

        if (jugador.getEnergia() < ENERGIA_ATAQUE) {

            System.out.println(
                "No tenes suficiente energia para atacar."
            );

            return false;
        }

        float diferenciaX =
                enemigo.getPosicionX()
                - jugador.getPosicionX();

        float diferenciaY =
                enemigo.getPosicionY()
                - jugador.getPosicionY();

        // =====================================================
        // COMPROBAR DIRECCIÓN
        // =====================================================

        boolean estaEnZonaDeAtaque = false;

        switch (direccion) {

            case ARRIBA:

                estaEnZonaDeAtaque =
                        diferenciaY > 0 &&
                        Math.abs(diferenciaX) <= ANCHO_ATAQUE &&
                        diferenciaY <= RANGO_ATAQUE;

                break;

            case ABAJO:

                estaEnZonaDeAtaque =
                        diferenciaY < 0 &&
                        Math.abs(diferenciaX) <= ANCHO_ATAQUE &&
                        Math.abs(diferenciaY) <= RANGO_ATAQUE;

                break;

            case IZQUIERDA:

                estaEnZonaDeAtaque =
                        diferenciaX < 0 &&
                        Math.abs(diferenciaY) <= ANCHO_ATAQUE &&
                        Math.abs(diferenciaX) <= RANGO_ATAQUE;

                break;

            case DERECHA:

                estaEnZonaDeAtaque =
                        diferenciaX > 0 &&
                        Math.abs(diferenciaY) <= ANCHO_ATAQUE &&
                        diferenciaX <= RANGO_ATAQUE;

                break;
        }

        // =====================================================
        // EL ATAQUE NO ALCANZÓ AL ENEMIGO
        // =====================================================

        if (!estaEnZonaDeAtaque) {

            jugador.gastarEnergia(ENERGIA_ATAQUE);

            System.out.println(
                "El ataque fallo."
            );

            return false;
        }

        // =====================================================
        // ATAQUE EXITOSO
        // =====================================================

        enemigo.recibirDaño(DAÑO_JUGADOR);

        jugador.gastarEnergia(ENERGIA_ATAQUE);

        System.out.println(
            "Atacaste a "
            + enemigo.getNombre()
            + " y causaste "
            + DAÑO_JUGADOR
            + " de daño."
        );

        if (!enemigo.estaVivo()) {

            System.out.println(
                enemigo.getNombre()
                + " fue derrotado."
            );
        }

        return true;
    }

    public int getEnergiaAtaque() {
        return ENERGIA_ATAQUE;
    }

    public float getRangoAtaque() {
        return RANGO_ATAQUE;
    }

    public int getDañoJugador() {
        return DAÑO_JUGADOR;
    }
}