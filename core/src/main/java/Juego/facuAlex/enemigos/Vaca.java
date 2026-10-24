package Juego.facuAlex.enemigos;

import Juego.facuAlex.Mapa.Mapa;
import Juego.facuAlex.enemigos.CriaturaAnimacion.Vista;
import Juego.facuAlex.jugador.Jugador;
import Juego.facuAlex.recursos.Comida;
import Juego.facuAlex.recursos.Recursos;

public class Vaca extends Criatura {

    private static final float VELOCIDAD_CAMINAR = 35f;

    private static final float VELOCIDAD_HUIR = 120f;

    private static final float TIEMPO_HUIDA = 4f;

    private boolean huyendo;

    private float tiempoHuida;

    public Vaca() {

        super("Vaca", 100, 10, crearAnimacion(), 1f);
    }

    // sprites/vaca.png: 9 columnas x 4 filas. Solo tiene vista de LADO.
    private static CriaturaAnimacion crearAnimacion() {

        return new CriaturaAnimacion("sprites/vaca.png", 9, 4)
                .quieto(Vista.LADO,  0, 5, 0.4f)
                .caminar(Vista.LADO, 1, 9, 0.10f)
                .correr(Vista.LADO,  2, 9, 0.06f)
                .muerte(Vista.LADO,  3, 8, 0.12f);
    }

    @Override
    protected Recursos[] crearDrops() {

        Comida carne = new Comida("Carne", 3, 25);

        carne.cargarIcono("objetos/carne.png");

        Recursos cuero = new Recursos("Cuero", 1);

        cuero.cargarIcono("objetos/cuero.png");

        return new Recursos[] { carne, cuero };
    }

    // Cuando le pegan, sale corriendo un rato
    @Override
    public void recibirDaño(int cantidad) {

        super.recibirDaño(cantidad);

        if (estaVivo()) {

            huyendo = true;

            tiempoHuida = TIEMPO_HUIDA;
        }
    }

    @Override
    protected void pensar(Jugador jugador, Mapa mapa, float delta) {

        if (huyendo && jugador != null) {

            tiempoHuida -= delta;

            if (tiempoHuida <= 0f) {

                huyendo = false;

                return;
            }

            // Se aleja del jugador
            float dx = -diferenciaX(jugador);
            float dy = -diferenciaY(jugador);

            boolean avanzo =
                    moverHacia(dx, dy, VELOCIDAD_HUIR, delta, mapa)
                    // Si esta acorralada contra un arbol, prueba de costado
                    || moverHacia(-dy, dx, VELOCIDAD_HUIR, delta, mapa);

            cambiarEstado(avanzo ? Estado.CORRER : Estado.QUIETO);

            return;
        }

        deambular(delta, mapa, VELOCIDAD_CAMINAR);
    }
}