package Juego.facuAlex.enemigos;

import Juego.facuAlex.Jugador;

public class Guardian extends Enemigo {

    private float velocidad;

    private float distanciaDeteccion;

    private float distanciaAtaque;

    private float tiempoEntreAtaques;

    private float tiempoAtaque;

    // =====================================================
    // ESTADOS DEL ATAQUE
    // =====================================================

    public enum EstadoAtaque {
        NORMAL,
        PREPARANDO,
        ATACANDO
    }

    private EstadoAtaque estadoAtaque;

    // Tiempo que tarda en preparar el ataque
    private static final float TIEMPO_PREPARACION = 0.8f;

    // Duración del momento de impacto
    private static final float TIEMPO_IMPACTO = 0.2f;

    // Tiempo restante del estado actual
    private float tiempoEstado;

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

        estadoAtaque = EstadoAtaque.NORMAL;

        tiempoEstado = 0f;
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================

    public void actualizar(
            Jugador jugador,
            float delta) {

        if (jugador == null) {
            return;
        }

        if (!estaVivo()) {
            return;
        }

        // =================================================
        // COOLDOWN ENTRE ATAQUES
        // =================================================

        if (tiempoAtaque > 0) {

            tiempoAtaque -= delta;
        }

        // =================================================
        // SI ESTÁ PREPARANDO ATAQUE
        // =================================================

        if (estadoAtaque == EstadoAtaque.PREPARANDO) {

            tiempoEstado -= delta;

            // Mientras prepara el ataque NO se mueve
            if (tiempoEstado <= 0) {

                estadoAtaque =
                        EstadoAtaque.ATACANDO;

                tiempoEstado =
                        TIEMPO_IMPACTO;

                ejecutarAtaque(jugador);
            }

            return;
        }

        // =================================================
        // SI ESTÁ EN EL MOMENTO DE IMPACTO
        // =================================================

        if (estadoAtaque == EstadoAtaque.ATACANDO) {

            tiempoEstado -= delta;

            if (tiempoEstado <= 0) {

                estadoAtaque =
                        EstadoAtaque.NORMAL;
            }

            return;
        }

        // =================================================
        // DISTANCIA AL JUGADOR
        // =================================================

        float diferenciaX =
                jugador.getPosicionX()
                - getPosicionX();

        float diferenciaY =
                jugador.getPosicionY()
                - getPosicionY();

        float distancia =
                (float) Math.sqrt(
                    diferenciaX * diferenciaX +
                    diferenciaY * diferenciaY
                );

        // =================================================
        // FUERA DEL RANGO DE DETECCIÓN
        // =================================================

        if (distancia > distanciaDeteccion) {

            return;
        }

        // =================================================
        // RANGO DE ATAQUE
        // =================================================

        if (distancia <= distanciaAtaque) {

            if (tiempoAtaque <= 0) {

                comenzarAtaque();
            }

            return;
        }

        // =================================================
        // PERSEGUIR AL JUGADOR
        // =================================================

        if (distancia > 0) {

            float direccionX =
                    diferenciaX / distancia;

            float direccionY =
                    diferenciaY / distancia;

            float movimiento =
                    velocidad * delta;

            setPosicion(
                getPosicionX()
                    + direccionX * movimiento,

                getPosicionY()
                    + direccionY * movimiento
            );
        }
    }

    // =====================================================
    // COMENZAR ATAQUE
    // =====================================================

    private void comenzarAtaque() {

        estadoAtaque =
                EstadoAtaque.PREPARANDO;

        tiempoEstado =
                TIEMPO_PREPARACION;

        System.out.println(
            "¡El Guardian esta preparando un ataque!"
        );
    }

    // =====================================================
    // EJECUTAR ATAQUE
    // =====================================================

    private void ejecutarAtaque(Jugador jugador) {

        if (jugador == null) {
            return;
        }

        if (!jugador.estaVivo()) {
            return;
        }

        float diferenciaX =
                jugador.getPosicionX()
                - getPosicionX();

        float diferenciaY =
                jugador.getPosicionY()
                - getPosicionY();

        float distancia =
                (float) Math.sqrt(
                    diferenciaX * diferenciaX +
                    diferenciaY * diferenciaY
                );

        // ================================================
        // EL JUGADOR SE ESCAPÓ
        // ================================================

        if (distancia > distanciaAtaque) {

            System.out.println(
                "El ataque del Guardian fallo."
            );

            tiempoAtaque =
                    tiempoEntreAtaques;

            return;
        }

        // ================================================
        // ATAQUE ACERTADO
        // ================================================

        atacar(jugador);

        tiempoAtaque =
                tiempoEntreAtaques;
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public float getVelocidad() {

        return velocidad;
    }

    public float getDistanciaDeteccion() {

        return distanciaDeteccion;
    }

    public float getDistanciaAtaque() {

        return distanciaAtaque;
    }

    public EstadoAtaque getEstadoAtaque() {

        return estadoAtaque;
    }

    public boolean estaPreparandoAtaque() {

        return estadoAtaque ==
                EstadoAtaque.PREPARANDO;
    }

    public float getTiempoPreparacion() {

        return tiempoEstado;
    }
}