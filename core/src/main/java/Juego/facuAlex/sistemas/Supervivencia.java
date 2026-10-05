package Juego.facuAlex.sistemas;

import Juego.facuAlex.jugador.*;

public class Supervivencia {

    private int hambrePorHora;
    private int danoPorHambre;

    public Supervivencia(int hambrePorHora, int danoPorHambre) {
        this.hambrePorHora = hambrePorHora;
        this.danoPorHambre = danoPorHambre;
    }


	public void pasarHora(Jugador jugador) {

        jugador.perderHambre(hambrePorHora);
        
        jugador.comprobarSupervivencia();

        System.out.println("Ha pasado una hora.");
    }
    
    
    
    
}