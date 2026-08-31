package Juego.facuAlex.enemigos;

import Juego.facuAlex.recursos.Comida;
import Juego.facuAlex.recursos.Recursos;

public class Gallina extends Animal {

    public Gallina() {

        super(
            "Gallina",
            30,
            5,

            new Recursos[] {

                new Comida("Carne", 1, 15),
                new Recursos("Plumas", 3)
            }
        );
    }
}