package Juego.facuAlex.sistemas;

import Juego.facuAlex.enemigos.Enemigo;

public class combate {

    public void atacar(Enemigo enemigo, int daño) {

        enemigo.recibirDaño(daño);

        System.out.println(
            "El enemigo recibió " + daño + " de daño."
        );

        if (!enemigo.estaVivo()) {
            System.out.println("El enemigo fue derrotado.");
        }
    }
}