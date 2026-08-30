package Juego.facuAlex.sistemas;

import Juego.facuAlex.Jugador;

public class CicloDia {

    private int dia;
    private int hora;
    private boolean esNoche;
    private Supervivencia sistemaSupervivencia;
    
    public CicloDia(Supervivencia sistemaSupervivencia) {
        dia = 1;
        hora = 6;
        esNoche = false;
        this.sistemaSupervivencia = sistemaSupervivencia;
    }

    public void pasarHora(Jugador jugador) {

        hora++;

        if (hora >= 24) {
            hora = 0;
            dia++;
        }

        actualizarMomento();

        sistemaSupervivencia.pasarHora(jugador);
    }

    private void actualizarMomento() {

        if (hora >= 20 || hora < 6) {
            esNoche = true;
        } else {
            esNoche = false;
        }
    }

    public int getDia() {
        return dia;
    }

    public int getHora() {
        return hora;
    }

    public boolean esNoche() {
        return esNoche;
    }

    public void mostrarEstado() {

        System.out.println(
            "Día: " + dia +
            " | Hora: " + hora +
            ":00"
        );

        if (esNoche) {
            System.out.println("Es de noche.");
        } else {
            System.out.println("Es de día.");
        }
    }
}
