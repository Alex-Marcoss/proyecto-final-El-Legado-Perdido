package Juego.facuAlex.enemigos;

import Juego.facuAlex.recursos.Comida;
import Juego.facuAlex.recursos.Recursos;

public class Vaca extends Animal {

    public Vaca() {

        super(
            "Vaca",
            100,
            10,

            new Recursos[] {

                new Comida("Carne", 3, 25),
                new Recursos("Cuero", 1)
            }
        );
    }
}