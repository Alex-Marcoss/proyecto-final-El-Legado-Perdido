package Juego.facuAlex.enemigos;

import Juego.facuAlex.enemigos.CriaturaAnimacion.Vista;
import Juego.facuAlex.recursos.Comida;
import Juego.facuAlex.recursos.Recursos;

public class Lobo extends CriaturaHostil {

    public Lobo() {

        super(
            "Lobo",
            60,                  // vida
            12,                  // daño
            crearAnimacion(),
            1f,                  // escala de dibujo
            95f,                 // velocidad
            300f,                // distancia de deteccion
            40f,                 // distancia de ataque
            1.2f                 // segundos entre ataques
        );
    }

    // sprites/lobo.png: 7 columnas x 11 filas
    private static CriaturaAnimacion crearAnimacion() {

        return new CriaturaAnimacion("sprites/lobo.png", 7, 11)
                .quieto(Vista.LADO,      0, 2, 0.5f)
                .quieto(Vista.FRENTE,    1, 2, 0.5f)
                .quieto(Vista.ESPALDA,   2, 1, 0.5f)
                .caminar(Vista.LADO,     3, 5, 0.12f)
                .caminar(Vista.FRENTE,   4, 3, 0.15f)
                .caminar(Vista.ESPALDA,  5, 4, 0.15f)
                .correr(Vista.LADO,      6, 5, 0.08f)
                .correr(Vista.FRENTE,    7, 3, 0.10f)
                .correr(Vista.ESPALDA,   8, 4, 0.10f)
                .atacar(Vista.LADO,      9, 5, 0.10f)
                .muerte(Vista.LADO,     10, 7, 0.12f);
    }

    @Override
    protected Recursos[] crearDrops() {

        Comida carne = new Comida("Carne", 2, 25);

        carne.cargarIcono("objetos/carne.png");

        return new Recursos[] { carne };
    }
}