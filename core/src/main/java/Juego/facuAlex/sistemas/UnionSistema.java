package Juego.facuAlex.sistemas;

import Juego.facuAlex.Jugador;

public class UnionSistema {

    private Jugador jugador;
    private CicloDia cicloDia;
    private Supervivencia supervivencia;

    public UnionSistema(Jugador jugador) {

        this.jugador = jugador;

        this.supervivencia = new Supervivencia(10,10);

        this.cicloDia = new CicloDia(supervivencia);
    }

    public void pasarHora() {

        cicloDia.pasarHora(jugador);
    }

    public void mostrarEstado() {

        cicloDia.mostrarEstado();

        System.out.println();

        jugador.mostrarEstado();
    }
    
    
}

