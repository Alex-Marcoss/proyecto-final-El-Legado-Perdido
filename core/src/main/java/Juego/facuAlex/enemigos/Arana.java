package Juego.facuAlex.enemigos;

import Juego.facuAlex.enemigos.CriaturaAnimacion.Vista;
import Juego.facuAlex.recursos.Recursos;

public class Arana extends CriaturaHostil {

    public Arana() {

        super(
            "Araña",
            40,                  // vida
            8,                   // daño
            crearAnimacion(),
            1f,                  // escala de dibujo
            70f,                 // velocidad
            220f,                // distancia de deteccion
            35f,                 // distancia de ataque
            1f                   // segundos entre ataques
        );
    }

    // sprites/arana.png: 8 columnas x 4 filas.
    // La araña se ve de frente y es simetrica, asi que sirve para todas las direcciones.
    private static CriaturaAnimacion crearAnimacion() {

        return new CriaturaAnimacion("sprites/arana.png", 8, 4)
                .quieto(Vista.FRENTE,   0, 1, 0.5f)
                .caminar(Vista.FRENTE,  1, 8, 0.08f)
                .atacar(Vista.FRENTE,   2, 4, 0.125f)
                .muerte(Vista.FRENTE,   3, 4, 0.20f);
    }

    @Override
    protected Recursos[] crearDrops() {

        Recursos hilo = new Recursos("Hilo", 1);

        hilo.cargarIcono("objetos/hilo.png");

        return new Recursos[] { hilo };
    }
}