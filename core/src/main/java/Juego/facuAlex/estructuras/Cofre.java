
package Juego.facuAlex.estructuras;

import Juego.facuAlex.inventario.inventario;

public class Cofre extends Estructura {

    private final inventario inventarioCofre;

    public Cofre(float x, float y) {
        super(
                "Cofre",
                "estructuras/cofre.png",
                x, y, 64f, 64f
        );

        inventarioCofre = new inventario();
    }

    public inventario getInventarioCofre() {
        return inventarioCofre;
    }
}
