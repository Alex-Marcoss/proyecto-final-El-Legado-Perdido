package Juego.facuAlex.enemigos.guardian;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import Juego.facuAlex.enemigos.Enemigo;
import Juego.facuAlex.enemigos.guardian.RayoEfecto.TipoRayo;
import Juego.facuAlex.jugador.Jugador;

public class Guardian extends Enemigo {

    private float velocidad;

    private float distanciaDeteccion;

    private float distanciaAtaque;

    private float tiempoEntreAtaques;

    private float tiempoAtaque;

    // =====================================================
    // DIRECCIÓN
    // =====================================================

    public enum Direccion {

        ARRIBA,
        ABAJO,
        IZQUIERDA,
        DERECHA
    }

    private Direccion direccion;

    // =====================================================
    // ESTADOS DEL ATAQUE
    // =====================================================

    public enum EstadoAtaque {

        NORMAL,
        PREPARANDO,
        ATACANDO,
        MUERTO          // <-- NUEVO: el Guardian ya no tiene vida
    }

    private EstadoAtaque estadoAtaque;

    // =====================================================
    // TIPOS DE ATAQUE
    // =====================================================

    public enum TipoAtaque {

        RAYO_VERTICAL,
        RAYO_HORIZONTAL
    }

    private TipoAtaque tipoAtaqueActual;

    // Para que el azar no sea injusto: nunca repite el mismo ataque
    // más de MAX_REPETICIONES veces seguidas
    private static final int MAX_REPETICIONES = 2;

    private TipoAtaque ultimoTipoAtaque;

    private int repeticionesSeguidas;

    private final Random azar = new Random();

    // =====================================================
    // TIEMPOS
    // =====================================================

    private static final float TIEMPO_PREPARACION = 0.8f;

    private static final float TIEMPO_IMPACTO = 0.2f;

    // El rayo cae un instante antes de que termine la preparación
    private static final float RETARDO_RAYO = TIEMPO_PREPARACION - 0.06f;

    private static final float ESCALA_RAYO = 2f;

    // El rayo horizontal solo se usa si el jugador está a su izquierda o derecha:
    // la diferencia de altura (Y) tiene que ser menor que esto
    private static final float TOLERANCIA_ALINEACION = 45f;

    private float tiempoEstado;

    // Segundos que pasaron desde que murió (sirve para la animación de muerte)
    private float tiempoMuerte;

    // =====================================================
    // ANIMACIÓN Y RAYOS
    // =====================================================

    private GuardianAnimacion animacion;

    private final List<RayoEfecto> rayos = new ArrayList<RayoEfecto>();

    // Rayo del ataque que se está preparando / ejecutando
    private RayoEfecto rayoActual;

    private boolean moviendose;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public Guardian() {

        super(
            "Guardian del Templo",
            250,
            35
        );

        velocidad = 80f;

        distanciaDeteccion = 400f;

        distanciaAtaque = 90f;

        tiempoEntreAtaques = 1.5f;

        tiempoAtaque = 0f;

        estadoAtaque = EstadoAtaque.NORMAL;

        tipoAtaqueActual = TipoAtaque.RAYO_VERTICAL;

        ultimoTipoAtaque = null;

        repeticionesSeguidas = 0;

        tiempoEstado = 0f;

        tiempoMuerte = 0f;

        direccion = Direccion.ABAJO;

        animacion = new GuardianAnimacion();

        moviendose = false;
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================

    public void actualizar(
            Jugador jugador,
            float delta) {

        // La animación siempre avanza
        animacion.actualizar(delta);

        // =================================================
        // MUERTO: no persigue, no ataca, no se mueve
        // =================================================

        if (!estaVivo()) {

            procesarMuerte(delta);

            return;
        }

        // =================================================
        // RAYOS EN PANTALLA
        // =================================================

        for (RayoEfecto r : rayos) {

            r.actualizar(delta);
        }

        rayos.removeIf(RayoEfecto::isTerminado);

        if (jugador == null) {
            return;
        }

        // =================================================
        // COOLDOWN
        // =================================================

        if (tiempoAtaque > 0) {

            tiempoAtaque -= delta;
        }

        // =================================================
        // PREPARANDO ATAQUE
        // =================================================

        if (estadoAtaque == EstadoAtaque.PREPARANDO) {

            moviendose = false;

            tiempoEstado -= delta;

            // Durante la preparación no se mueve

            if (tiempoEstado <= 0) {

                estadoAtaque = EstadoAtaque.ATACANDO;

                tiempoEstado = TIEMPO_IMPACTO;

                animacion.reiniciar();

                ejecutarAtaque(jugador);
            }

            return;
        }

        // =================================================
        // ATACANDO
        // =================================================

        if (estadoAtaque == EstadoAtaque.ATACANDO) {

            moviendose = false;

            tiempoEstado -= delta;

            if (tiempoEstado <= 0) {

                estadoAtaque = EstadoAtaque.NORMAL;

                animacion.reiniciar();
            }

            return;
        }

        // =================================================
        // DISTANCIA AL JUGADOR
        // =================================================

        moviendose = false;

        float diferenciaX =
                jugador.getPosicionX()
                - getPosicionX();

        float diferenciaY =
                jugador.getPosicionY()
                - getPosicionY();

        float distancia =
                (float) Math.sqrt(
                    diferenciaX * diferenciaX +
                    diferenciaY * diferenciaY
                );

        // =================================================
        // FUERA DEL RANGO DE DETECCIÓN
        // =================================================

        if (distancia > distanciaDeteccion) {

            return;
        }

        // =================================================
        // RANGO DE ATAQUE
        // =================================================

        if (distancia <= distanciaAtaque) {

            if (tiempoAtaque <= 0) {

                comenzarAtaque(
                    diferenciaX,
                    diferenciaY
                );
            }

            return;
        }

        // =================================================
        // PERSEGUIR AL JUGADOR
        // =================================================

        if (distancia > 0) {

            float direccionX =
                    diferenciaX / distancia;

            float direccionY =
                    diferenciaY / distancia;

            // Actualizamos hacia dónde está mirando

            actualizarDireccion(
                diferenciaX,
                diferenciaY
            );

            moviendose = true;

            float movimiento =
                    velocidad * delta;

            setPosicion(
                getPosicionX()
                    + direccionX * movimiento,

                getPosicionY()
                    + direccionY * movimiento
            );
        }
    }

    // =====================================================
    // MUERTE
    // =====================================================

    private void procesarMuerte(float delta) {

        // Solo la primera vez que se detecta la muerte
        if (estadoAtaque != EstadoAtaque.MUERTO) {

            estadoAtaque = EstadoAtaque.MUERTO;

            moviendose = false;

            // Cancela cualquier ataque que estuviera en curso
            rayos.clear();

            rayoActual = null;

            tiempoMuerte = 0f;

            // La futura animación de muerte arranca desde su primer frame
            animacion.reiniciar();

            System.out.println(
                "El Guardian ha sido derrotado."
            );
        }

        tiempoMuerte += delta;
    }

    // =====================================================
    // ACTUALIZAR DIRECCIÓN
    // =====================================================

    private void actualizarDireccion(
            float diferenciaX,
            float diferenciaY) {

        /*
         * Elegimos el eje en el que el jugador
         * está más lejos.
         *
         * Esto evita que el Guardian cambie
         * constantemente entre direcciones.
         */

        if (Math.abs(diferenciaX) >
                Math.abs(diferenciaY)) {

            if (diferenciaX > 0) {

                direccion = Direccion.DERECHA;

            } else {

                direccion = Direccion.IZQUIERDA;
            }

        } else {

            if (diferenciaY > 0) {

                direccion = Direccion.ARRIBA;

            } else {

                direccion = Direccion.ABAJO;
            }
        }
    }

    // =====================================================
    // ELEGIR TIPO DE ATAQUE
    // =====================================================

    private TipoAtaque elegirTipoAtaque(boolean puedeHorizontal) {

        // 50% de probabilidad para cada uno
        TipoAtaque elegido = azar.nextBoolean()
                ? TipoAtaque.RAYO_VERTICAL
                : TipoAtaque.RAYO_HORIZONTAL;

        // Si ya repitió demasiadas veces el mismo, cambia al otro
        if (elegido == ultimoTipoAtaque
                && repeticionesSeguidas >= MAX_REPETICIONES) {

            elegido = (elegido == TipoAtaque.RAYO_VERTICAL)
                    ? TipoAtaque.RAYO_HORIZONTAL
                    : TipoAtaque.RAYO_VERTICAL;
        }

        // Si el jugador no está a su costado, el horizontal no se puede usar
        if (elegido == TipoAtaque.RAYO_HORIZONTAL && !puedeHorizontal) {

            elegido = TipoAtaque.RAYO_VERTICAL;
        }

        if (elegido == ultimoTipoAtaque) {

            repeticionesSeguidas++;

        } else {

            repeticionesSeguidas = 1;
        }

        ultimoTipoAtaque = elegido;

        return elegido;
    }

    // =====================================================
    // COMENZAR ATAQUE
    // =====================================================

    private void comenzarAtaque(
            float diferenciaX,
            float diferenciaY) {

        // Primero fija la dirección del ataque

        actualizarDireccion(
            diferenciaX,
            diferenciaY
        );

        // ¿El jugador está a la derecha o a la izquierda (casi a la misma altura)?
        boolean alCostado =
                Math.abs(diferenciaX) > Math.abs(diferenciaY)
                && Math.abs(diferenciaY) <= TOLERANCIA_ALINEACION;

        tipoAtaqueActual = elegirTipoAtaque(alCostado);

        estadoAtaque =
                EstadoAtaque.PREPARANDO;

        tiempoEstado =
                TIEMPO_PREPARACION;

        animacion.reiniciar();

        // Posición del jugador en este momento
        float objetivoX =
                getPosicionX() + diferenciaX;

        float objetivoY =
                getPosicionY() + diferenciaY;

        if (tipoAtaqueActual == TipoAtaque.RAYO_HORIZONTAL) {

            // El Guardian mira hacia el lado del jugador y dispara hacia allá
            boolean haciaDerecha = diferenciaX >= 0;

            direccion = haciaDerecha
                    ? Direccion.DERECHA
                    : Direccion.IZQUIERDA;

            // El rayo sale del Guardian y recorre la altura (Y) del jugador.
            // El jugador lo esquiva moviéndose hacia arriba o hacia abajo.
            rayoActual = new RayoEfecto(
                getPosicionX(),
                objetivoY,
                RETARDO_RAYO,
                ESCALA_RAYO,
                TipoRayo.HORIZONTAL,
                haciaDerecha
            );

        } else {

            // El rayo cae donde está el jugador AHORA.
            // El círculo de aviso le da casi 0.8 s para salirse.
            rayoActual = new RayoEfecto(
                objetivoX,
                objetivoY,
                RETARDO_RAYO,
                ESCALA_RAYO,
                TipoRayo.VERTICAL
            );
        }

        rayos.add(rayoActual);

        System.out.println(
            "¡El Guardian esta preparando un ataque! ("
            + tipoAtaqueActual + ")"
        );
    }

    // =====================================================
    // EJECUTAR ATAQUE
    // =====================================================

    private void ejecutarAtaque(
            Jugador jugador) {

        if (jugador == null) {
            return;
        }

        if (!jugador.estaVivo()) {
            return;
        }

        tiempoAtaque =
                tiempoEntreAtaques;

        // =================================================
        // ¿EL JUGADOR SIGUE DENTRO DEL ÁREA DEL RAYO?
        // (sirve igual para el vertical y el horizontal,
        //  porque cada RayoEfecto sabe cuál es su área)
        // =================================================

        if (rayoActual != null
                && rayoActual.getAreaImpacto().contains(
                    jugador.getPosicionX(),
                    jugador.getPosicionY())) {

            // ATAQUE ACERTADO
            atacar(jugador);

        } else {

            // EL JUGADOR ESCAPÓ
            System.out.println(
                "El ataque del Guardian fallo."
            );
        }
    }

    // =====================================================
    // DIBUJO
    // =====================================================

    private Animation<TextureRegion> getIdleSegunDireccion() {

        switch (direccion) {
            case ARRIBA:    return animacion.getIdleUp();
            case IZQUIERDA: return animacion.getIdleLeft();
            case DERECHA:   return animacion.getIdleRight();
            default:        return animacion.getIdleDown();
        }
    }

    public TextureRegion getFrameActual() {

        Animation<TextureRegion> anim;

        switch (estadoAtaque) {

            case MUERTO:

                // Cuando exista el sprite de muerte se usa ese.
                // Mientras tanto se muestra el idle como placeholder.
                if (animacion.tieneAnimacionMuerte()) {

                    return animacion.obtenerFrameMuerte(direccion);
                }

                anim = getIdleSegunDireccion();
                break;

            case PREPARANDO:

                switch (direccion) {
                    case ARRIBA:    anim = animacion.getPrepararUp();    break;
                    case IZQUIERDA: anim = animacion.getPrepararLeft();  break;
                    case DERECHA:   anim = animacion.getPrepararRight(); break;
                    default:        anim = animacion.getPrepararDown();  break;
                }
                break;

            case ATACANDO:

                switch (direccion) {
                    case ARRIBA:    anim = animacion.getAtacarUp();    break;
                    case IZQUIERDA: anim = animacion.getAtacarLeft();  break;
                    case DERECHA:   anim = animacion.getAtacarRight(); break;
                    default:        anim = animacion.getAtacarDown();  break;
                }
                break;

            default:

                if (moviendose) {

                    switch (direccion) {
                        case ARRIBA:    anim = animacion.getWalkUp();    break;
                        case IZQUIERDA: anim = animacion.getWalkLeft();  break;
                        case DERECHA:   anim = animacion.getWalkRight(); break;
                        default:        anim = animacion.getWalkDown();  break;
                    }

                } else {

                    anim = getIdleSegunDireccion();
                }
        }

        return animacion.getFrame(anim);
    }

    /*
     * Todos los frames se dibujan con el MISMO tamaño y la
     * MISMA posición (la celda del PNG es de 96 x 96).
     *
     * Se asume que getPosicionX() / getPosicionY() son los
     * pies del Guardian. Si en tu juego son la esquina
     * inferior izquierda, sacá el "- tam / 2f" de la x.
     */
    public void dibujar(SpriteBatch batch, float escala) {

        float tam = GuardianAnimacion.CELDA * escala;

        batch.draw(
            getFrameActual(),
            getPosicionX() - tam / 2f,
            getPosicionY(),
            tam,
            tam
        );
    }

    // Llamar DESPUÉS de dibujar al Guardian, así el brillo queda encima
    public void dibujarRayos(SpriteBatch batch) {

        for (RayoEfecto r : rayos) {

            r.dibujar(batch);
        }
    }

    public void dispose() {

        animacion.dispose();
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public float getVelocidad() {

        return velocidad;
    }

    public float getDistanciaDeteccion() {

        return distanciaDeteccion;
    }

    public float getDistanciaAtaque() {

        return distanciaAtaque;
    }

    public EstadoAtaque getEstadoAtaque() {

        return estadoAtaque;
    }

    public TipoAtaque getTipoAtaque() {

        return tipoAtaqueActual;
    }

    public Direccion getDireccion() {

        return direccion;
    }

    public boolean estaPreparandoAtaque() {

        return estadoAtaque ==
                EstadoAtaque.PREPARANDO;
    }

    public boolean estaMuerto() {

        return estadoAtaque ==
                EstadoAtaque.MUERTO;
    }

    public float getTiempoMuerte() {

        return tiempoMuerte;
    }

    public float getTiempoPreparacion() {

        return tiempoEstado;
    }
}