package Juego.facuAlex.sistemas;

import Juego.facuAlex.jugador.*;

public class Energia {

    // Cantidad de energía que se recupera por segundo
    private float energiaPorSegundo;

    // Tiempo acumulado desde el último gasto
    private float tiempoSinGastar;

    public Energia(float energiaPorSegundo) {

        this.energiaPorSegundo = energiaPorSegundo;
        this.tiempoSinGastar = 0;
    }

    // ==========================================================
    // ACTUALIZAR
    // ==========================================================

    public void actualizar(Jugador jugador, float segundos) {

        if (jugador == null) {
            return;
        }

        if (!jugador.estaVivo()) {
            return;
        }

        // Si ya está al máximo no necesitamos recuperar
        if (jugador.getEnergia() >= 100) {
            tiempoSinGastar = 0;
            return;
        }

        // Acumulamos el tiempo
        tiempoSinGastar += segundos;

        // Recuperamos energía poco a poco
        if (tiempoSinGastar > 3) {

            jugador.recuperarEnergia(
                energiaPorSegundo * segundos
            );
        }
    }

    // ==========================================================
    // REGISTRAR GASTO
    // ==========================================================

    public void registrarGasto() {

        tiempoSinGastar = 0;
    }

    // ==========================================================
    // GETTERS
    // ==========================================================

    public float getTiempoSinGastar() {

        return tiempoSinGastar;
    }

    public float getEnergiaPorSegundo() {

        return energiaPorSegundo;
    }
}