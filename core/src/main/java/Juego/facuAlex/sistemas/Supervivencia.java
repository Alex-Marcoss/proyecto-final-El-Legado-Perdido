package Juego.facuAlex.sistemas;

import Juego.facuAlex.jugador.Jugador;

public class Supervivencia {

    private int hambrePorHora;

    private int danoPorHambre;


    // Cada 5 segundos
    private static final float INTERVALO_INANICION = 5f;

    // Cada 5 segundos
    private static final float INTERVALO_REGENERACION = 5f;

    // Vida recuperada
    private static final int VIDA_REGENERADA = 2;

    // Hambre necesaria para regenerar
    private static final int HAMBRE_MINIMA_REGENERACION = 70;

    // Tiempo después de recibir daño
    // antes de volver a regenerar
    private static final float TIEMPO_SIN_DANO = 5f;


    private float tiempoHambre;

    private float tiempoInanicion;

    private float tiempoRegeneracion;

    private float tiempoDesdeDanio;


    public Supervivencia(
            int hambrePorHora,
            int danoPorHambre
    ) {

        this.hambrePorHora =
                hambrePorHora;

        this.danoPorHambre =
                danoPorHambre;


        tiempoHambre = 0f;

        tiempoInanicion = 0f;

        tiempoRegeneracion = 0f;

        tiempoDesdeDanio =
                TIEMPO_SIN_DANO;
    }


    // ==========================================================
    // ACTUALIZAR SUPERVIVENCIA
    // ==========================================================

    public void actualizar(
            Jugador jugador,
            float delta
    ) {


        if (
            jugador == null
            ||
            !jugador.estaVivo()
        ) {

            return;
        }


        // ======================================================
        // HAMBRE
        // ======================================================

        tiempoHambre += delta;


        float tiempoPorPuntoHambre =
                60f / hambrePorHora;


        if (
            tiempoHambre
            >= tiempoPorPuntoHambre
        ) {


            int puntosPerdidos =
                    (int) (
                        tiempoHambre
                        /
                        tiempoPorPuntoHambre
                    );


            jugador.perderHambre(
                puntosPerdidos
            );


            tiempoHambre =
                    tiempoHambre
                    -
                    (
                        puntosPerdidos
                        *
                        tiempoPorPuntoHambre
                    );
        }


        // ======================================================
        // TIEMPO DESDE EL ÚLTIMO DAÑO
        // ======================================================

        tiempoDesdeDanio += delta;


        // ======================================================
        // INANICIÓN
        // ======================================================

        if (
            jugador.getHambre()
            <= 0
        ) {


            tiempoInanicion += delta;


            if (
                tiempoInanicion
                >= INTERVALO_INANICION
            ) {


                jugador.recibirDanio(
                    danoPorHambre
                );


                tiempoInanicion =
                        0f;


                System.out.println(
                    "El jugador esta sufriendo por hambre."
                );
            }

        } else {

            tiempoInanicion =
                    0f;
        }


        // ======================================================
        // REGENERACIÓN DE VIDA
        // ======================================================

        if (
            jugador.getHambre()
            >= HAMBRE_MINIMA_REGENERACION

            &&

            jugador.getVida()
            < 100

            &&

            tiempoDesdeDanio
            >= TIEMPO_SIN_DANO
        ) {


            tiempoRegeneracion +=
                    delta;


            if (
                tiempoRegeneracion
                >= INTERVALO_REGENERACION
            ) {


                jugador.curar(
                    VIDA_REGENERADA
                );


                tiempoRegeneracion =
                        0f;
            }

        } else {

            tiempoRegeneracion =
                    0f;
        }
    }


    // ==========================================================
    // REGISTRAR DAÑO
    // ==========================================================

    public void registrarDanio() {

        tiempoDesdeDanio =
                0f;

        tiempoRegeneracion =
                0f;
    }


    // ==========================================================
    // PASAR HORA
    // ==========================================================

    public void pasarHora(
            Jugador jugador
    ) {

        jugador.perderHambre(
            hambrePorHora
        );

        jugador.comprobarSupervivencia();

        System.out.println(
            "Ha pasado una hora."
        );
    }


    // ==========================================================
    // GETTERS
    // ==========================================================

    public int getHambrePorHora() {

        return hambrePorHora;
    }


    public int getDanoPorHambre() {

        return danoPorHambre;
    }
}