package Juego;

public class CicloDia {

    private int dia;
    private int hora;
    private boolean esNoche;

    public CicloDia() {
        dia = 1;
        hora = 6;
        esNoche = false;
    }

    public void pasarHora() {

        hora++;

        if (hora >= 24) {
            hora = 0;
            dia++;
        }

        actualizarMomento();
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
