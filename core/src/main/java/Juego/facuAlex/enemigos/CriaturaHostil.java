
package Juego.facuAlex.enemigos;

import Juego.facuAlex.Mapa.Mapa;
import Juego.facuAlex.estructuras.Fogata;
import Juego.facuAlex.jugador.Jugador;

public abstract class CriaturaHostil extends Criatura {

    private static final float DURACION_ATAQUE = 0.5f;
    private static final float DISTANCIA_HUIDA_FOGATA = 180f;
    private static final float DISTANCIA_FIN_HUIDA = 240f;
    private boolean huyendoDeFogata = false;
    private static final float MULTIPLICADOR_HUIDA = 1.3f;

    private final float velocidad;
    private final float distanciaDeteccion;
    private final float distanciaAtaque;
    private final float tiempoEntreAtaques;

    private float cooldown;
    private float tiempoAtaqueRestante;

    public CriaturaHostil(
            String nombre,
            int vida,
            int daño,
            CriaturaAnimacion animacion,
            float escala,
            float velocidad,
            float distanciaDeteccion,
            float distanciaAtaque,
            float tiempoEntreAtaques) {

        super(nombre, vida, daño, animacion, escala);

        this.velocidad = velocidad;
        this.distanciaDeteccion = distanciaDeteccion;
        this.distanciaAtaque = distanciaAtaque;
        this.tiempoEntreAtaques = tiempoEntreAtaques;
    }

    @Override
    protected void pensar(Jugador jugador, Mapa mapa, float delta) {

        if (cooldown > 0f) {
            cooldown -= delta;
        }

        // Primero comprobamos si hay una fogata encendida cerca.
        // Esto tiene prioridad sobre perseguir o atacar al jugador.

        if (mapa != null) {

            Fogata fogata = mapa.obtenerFogataEncendidaCercana(
                    getPosicionX(),
                    getPosicionY(),
                    DISTANCIA_FIN_HUIDA
            );

            if (fogata != null) {

                float fogataX = fogata.getX()
                        + fogata.getAncho() / 2f;

                float fogataY = fogata.getY()
                        + fogata.getAlto() / 2f;

                float dx = getPosicionX() - fogataX;
                float dy = getPosicionY() - fogataY;

                float distancia = (float) Math.sqrt(dx * dx + dy * dy);

                // Entra en modo huida al acercarse demasiado.
                if (distancia <= DISTANCIA_HUIDA_FOGATA) {
                    huyendoDeFogata = true;
                }

                // Una vez que huye, mantiene ese comportamiento
                // hasta alejarse lo suficiente.
                if (huyendoDeFogata) {

                    if (distancia >= DISTANCIA_FIN_HUIDA) {
                        huyendoDeFogata = false;

                    } else {

                        if (dx == 0f && dy == 0f) {
                            dx = 1f;
                        }

                        tiempoAtaqueRestante = 0f;

                        boolean seMovio = moverHacia(
                                dx,
                                dy,
                                velocidad * MULTIPLICADOR_HUIDA,
                                delta,
                                mapa
                        );

                        cambiarEstado(
                                seMovio ? Estado.CORRER : Estado.QUIETO
                        );

                        return;
                    }
                }

            } else {
                // Si la fogata desaparece o se apaga, deja de huir.
                huyendoDeFogata = false;
            }
        }

        

        // Mientras ataca, se queda quieto hasta terminar la animación.
        if (estado == Estado.ATACAR) {

            tiempoAtaqueRestante -= delta;

            if (tiempoAtaqueRestante <= 0f) {
                cambiarEstado(Estado.QUIETO);
            }

            return;
        }

        // Si el jugador murió o no existe, la criatura deambula.
        if (jugador == null || !jugador.estaVivo()) {

            deambular(delta, mapa, velocidad * 0.4f);
            return;
        }

        float dx = diferenciaX(jugador);
        float dy = diferenciaY(jugador);

        float distancia = (float) Math.sqrt(dx * dx + dy * dy);

        // Si no detecta al jugador, deambula.
        if (distancia > distanciaDeteccion) {

            deambular(delta, mapa, velocidad * 0.4f);
            return;
        }

        mirarHacia(dx, dy);

        // Si está a distancia de ataque, ataca cuando termina el cooldown.
        if (distancia <= distanciaAtaque) {

            if (cooldown <= 0f) {

                atacar(jugador);

                cooldown = tiempoEntreAtaques;
                tiempoAtaqueRestante = DURACION_ATAQUE;

                cambiarEstado(Estado.ATACAR);

            } else {

                cambiarEstado(Estado.QUIETO);
            }

            return;
        }

        // Si está lejos, persigue al jugador.
        if (moverHacia(dx, dy, velocidad, delta, mapa)) {

            cambiarEstado(Estado.CORRER);

        } else {

            cambiarEstado(Estado.QUIETO);
        }
    }
}
