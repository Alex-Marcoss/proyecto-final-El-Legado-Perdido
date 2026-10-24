package Juego.facuAlex.enemigos;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import Juego.facuAlex.Mapa.Mapa;
import Juego.facuAlex.enemigos.CriaturaAnimacion.Vista;
import Juego.facuAlex.jugador.Jugador;
import Juego.facuAlex.recursos.Recursos;

public abstract class Criatura extends Animal {

    public enum Direccion { ARRIBA, ABAJO, IZQUIERDA, DERECHA }

    public enum Estado { QUIETO, CAMINAR, CORRER, ATACAR, MUERTO }

    // Hitbox en los pies, igual que el jugador (24 x 12)
    private static final float HITBOX_ANCHO = 24f;
    private static final float HITBOX_ALTO = 12f;

    // La posicion del jugador es la esquina de su sprite de 64 px,
    // asi que sus pies estan 32 px a la derecha
    protected static final float MITAD_JUGADOR = 32f;

    // Segundos que queda el cuerpo (se va desvaneciendo)
    private static final float TIEMPO_CUERPO = 3f;

    // Segundos que se pone rojiza al recibir un golpe
    private static final float TIEMPO_DANO = 0.2f;

    protected final Random azar = new Random();

    protected Direccion direccion = Direccion.ABAJO;
    protected Estado estado = Estado.QUIETO;

    // Ultimo lado horizontal al que miro (sirve para espejar el sprite)
    protected boolean mirandoDerecha = false;

    private final CriaturaAnimacion animacion;
    private final float escala;

    private float tiempoAnimacion;
    private float tiempoMuerte;
    private float tiempoDano;
    private boolean dropsEntregados;

    // Para deambular
    private float tiempoDeambular;
    private float deambularX;
    private float deambularY;

    // La posicion (getPosicionX/Y) son los PIES, en el centro del sprite
    public Criatura(String nombre, int vida, int daño,
                    CriaturaAnimacion animacion, float escala) {

        // Los drops se crean recien al morir (ver crearDrops)
        super(nombre, vida, daño, null);

        this.animacion = animacion;
        this.escala = escala;
    }

    // Cada criatura decide que hace en cada frame
    protected abstract void pensar(Jugador jugador, Mapa mapa, float delta);

    // Que suelta al morir
    protected abstract Recursos[] crearDrops();

    // =====================================================
    // DROPS
    // =====================================================

    @Override
    public Recursos[] obtenerDrops() {

        if (!estaVivo() && !dropsEntregados) {

            dropsEntregados = true;

            return crearDrops();
        }

        return null;
    }

    // =====================================================
    // DAÑO
    // =====================================================

    @Override
    public void recibirDaño(int cantidad) {

        super.recibirDaño(cantidad);

        tiempoDano = TIEMPO_DANO;
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================

    public void actualizar(Jugador jugador, Mapa mapa, float delta) {

        tiempoAnimacion += delta;

        if (!estaVivo()) {

            if (estado != Estado.MUERTO) {

                morir(jugador);
            }

            tiempoMuerte += delta;
            
            return;
        }

        if (tiempoDano > 0f) {

            tiempoDano -= delta;
        }

        pensar(jugador, mapa, delta);
    }

    private void morir(Jugador jugador) {

        estado = Estado.MUERTO;

        tiempoAnimacion = 0f;

        tiempoMuerte = 0f;

        // Entrega los drops directo al inventario
        if (jugador != null) {

            jugador.recogerDropsAnimal(this);
        }
    }

    public boolean haDesaparecido() {

        return estado == Estado.MUERTO
                && tiempoMuerte >= TIEMPO_CUERPO;
    }

    protected void cambiarEstado(Estado nuevo) {

        if (estado != nuevo) {

            estado = nuevo;

            tiempoAnimacion = 0f;
        }
    }

    // =====================================================
    // DISTANCIA AL JUGADOR (pies contra pies)
    // =====================================================

    protected float diferenciaX(Jugador jugador) {

        return jugador.getPosicionX() + MITAD_JUGADOR - getPosicionX();
    }

    protected float diferenciaY(Jugador jugador) {

        return jugador.getPosicionY() - getPosicionY();
    }

    // =====================================================
    // MOVIMIENTO
    // =====================================================

    protected void mirarHacia(float x, float y) {

        if (Math.abs(x) > Math.abs(y)) {

            direccion = x > 0 ? Direccion.DERECHA : Direccion.IZQUIERDA;

            mirandoDerecha = x > 0;

        } else {

            direccion = y > 0 ? Direccion.ARRIBA : Direccion.ABAJO;
        }
    }

    private Rectangle crearHitbox(float x, float y) {

        return new Rectangle(
            x - HITBOX_ANCHO / 2f,
            y,
            HITBOX_ANCHO,
            HITBOX_ALTO
        );
    }

    public Rectangle getHitbox() {

        return crearHitbox(getPosicionX(), getPosicionY());
    }

    // Se mueve hacia (dirX, dirY). Devuelve true si logro avanzar.
    // Mueve X e Y por separado para deslizarse por obstaculos.
    protected boolean moverHacia(float dirX, float dirY, float velocidad,
                                 float delta, Mapa mapa) {

        float largo = (float) Math.sqrt(dirX * dirX + dirY * dirY);

        if (largo == 0f) {

            return false;
        }

        dirX /= largo;
        dirY /= largo;

        mirarHacia(dirX, dirY);

        float paso = velocidad * delta;

        boolean seMovio = false;

        float nuevaX = getPosicionX() + dirX * paso;

        if (mapa.puedeCaminar(crearHitbox(nuevaX, getPosicionY()))) {

            setPosicion(nuevaX, getPosicionY());

            seMovio = true;
        }

        float nuevaY = getPosicionY() + dirY * paso;

        if (mapa.puedeCaminar(crearHitbox(getPosicionX(), nuevaY))) {

            setPosicion(getPosicionX(), nuevaY);

            seMovio = true;
        }

        return seMovio;
    }

    // Camina un rato en una direccion al azar, se frena, y vuelve a elegir
    protected void deambular(float delta, Mapa mapa, float velocidad) {

        tiempoDeambular -= delta;

        if (tiempoDeambular <= 0f) {

            tiempoDeambular = 1f + azar.nextFloat() * 2f;

            if (azar.nextInt(3) == 0) {

                // Se queda quieta un rato
                deambularX = 0f;
                deambularY = 0f;

            } else {

                float angulo = azar.nextFloat() * (float) (Math.PI * 2);

                deambularX = (float) Math.cos(angulo);
                deambularY = (float) Math.sin(angulo);
            }
        }

        if (deambularX == 0f && deambularY == 0f) {

            cambiarEstado(Estado.QUIETO);

            return;
        }

        if (moverHacia(deambularX, deambularY, velocidad, delta, mapa)) {

            cambiarEstado(Estado.CAMINAR);

        } else {

            // Choco con algo: elige otra direccion enseguida
            cambiarEstado(Estado.QUIETO);

            tiempoDeambular = 0f;
        }
    }

    // =====================================================
    // DIBUJO
    // =====================================================

    public void dibujar(SpriteBatch batch) {

        Vista vista;

        switch (direccion) {

            case ARRIBA: vista = Vista.ESPALDA; break;
            case ABAJO:  vista = Vista.FRENTE;  break;
            default:     vista = Vista.LADO;    break;
        }

        if (estado == Estado.MUERTO) {

            // El cuerpo se va desvaneciendo
            batch.setColor(
                1f, 1f, 1f,
                Math.max(0f, 1f - tiempoMuerte / TIEMPO_CUERPO)
            );

        } else if (tiempoDano > 0f) {

            // Destello rojizo al recibir un golpe
            batch.setColor(1f, 0.45f, 0.45f, 1f);
        }

        animacion.dibujar(
            batch,
            estado,
            vista,
            mirandoDerecha,
            tiempoAnimacion,
            getPosicionX(),
            getPosicionY(),
            escala
        );

        batch.setColor(1f, 1f, 1f, 1f);
    }
}