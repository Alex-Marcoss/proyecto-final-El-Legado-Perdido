package Juego.facuAlex.enemigos;

import Juego.facuAlex.Jugador;

public class Guardian extends Enemigo {

    private float velocidad;
    private float distanciaDeteccion;
    private float distanciaAtaque;

    private float tiempoEntreAtaques;
    private float tiempoAtaque;

    public Guardian() {

        super(
            "Guardian del Templo",
            250,
            35
        );

        velocidad = 80f;

        distanciaDeteccion = 400f;
        distanciaAtaque = 90f;

        tiempoEntreAtaques = 1.5f;
        tiempoAtaque = 0f;
    }

    // =========================================================
    // ACTUALIZAR
    // =========================================================

    public void actualizar(
            Jugador jugador,
            float delta) {

        if (jugador == null) {
            return;
        }

        if (!estaVivo()) {
            return;
        }

        if (tiempoAtaque > 0) {
            tiempoAtaque -= delta;
        }

        float diferenciaX =
            jugador.getPosicionX() -
            getPosicionX();

        float diferenciaY =
            jugador.getPosicionY() -
            getPosicionY();

        float distancia =
            (float)Math.sqrt(
                diferenciaX * diferenciaX +
                diferenciaY * diferenciaY
            );

        // =====================================================
        // NO DETECTA AL JUGADOR
        // =====================================================

        if (distancia > distanciaDeteccion) {
            return;
        }

        // =====================================================
        // RANGO DE ATAQUE
        // =====================================================

        if (distancia <= distanciaAtaque) {

            if (tiempoAtaque <= 0) {

                atacar(jugador);

                tiempoAtaque =
                    tiempoEntreAtaques;
            }

            return;
        }

        // =====================================================
        // PERSEGUIR
        // =====================================================

        if (distancia > 0) {

            float direccionX =
                diferenciaX / distancia;

            float direccionY =
                diferenciaY / distancia;

            float movimiento =
                velocidad * delta;

            setPosicion(
                getPosicionX() +
                direccionX * movimiento,

                getPosicionY() +
                direccionY * movimiento
            );
        }
    }

    public float getVelocidad() {
        return velocidad;
    }

    public float getDistanciaDeteccion() {
        return distanciaDeteccion;
    }

    public float getDistanciaAtaque() {
        return distanciaAtaque;
    }
}