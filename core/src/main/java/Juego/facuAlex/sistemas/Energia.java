package Juego.facuAlex.sistemas;

import Juego.facuAlex.Jugador;

public class Energia {

    private int energiaRecuperada;
    private float segundosParaRecuperar;

    private float tiempoSinGastar;

    public Energia(int energiaRecuperada, float segundosParaRecuperar) {
        this.energiaRecuperada = energiaRecuperada;
        this.segundosParaRecuperar = segundosParaRecuperar;
        this.tiempoSinGastar = 0;
    }

    public void actualizar(Jugador jugador, float segundos) {

        if (jugador == null) {
            return;
        }

        if (!jugador.estaVivo()) {
            return;
        }

        tiempoSinGastar += segundos;

        if (tiempoSinGastar >= segundosParaRecuperar) {

            jugador.recuperarEnergia(energiaRecuperada);

            tiempoSinGastar = 0;

            System.out.println(
                "Recuperaste " +
                energiaRecuperada +
                " de energia."
            );
        }
    }

    public void registrarGasto() {
        tiempoSinGastar = 0;
    }

    public float getTiempoSinGastar() {
        return tiempoSinGastar;
    }

    public int getEnergiaRecuperada() {
        return energiaRecuperada;
    }

    public float getSegundosParaRecuperar() {
        return segundosParaRecuperar;
    }
}