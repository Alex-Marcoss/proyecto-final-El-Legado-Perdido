package Juego.facuAlex.jugador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import Juego.facuAlex.Mapa.Mapa;
import Juego.facuAlex.enemigos.guardian.*;
import Juego.facuAlex.recursos.arbol;
import Juego.facuAlex.recursos.roca;
import Juego.facuAlex.sistemas.combate;

public class JugadorControl {

    private Jugador jugador;
    private Direccion direccion;
    private Estado estado;

    public enum Direccion {
        ARRIBA,
        ABAJO,
        IZQUIERDA,
        DERECHA
    }

    public enum Estado {
        IDLE,
        CAMINAR,
        CORRER,
        MINAR
    }

    // =========================
    // ESTADO DE MINADO
    // =========================

    // Roca que se esta picando (null si no esta minando)
    private roca rocaObjetivo;

    // Segundos que pasaron desde que empezo el golpe de pico
    private float tiempoMinado;

    // Para entregar la piedra una sola vez por golpe
    private boolean golpeAplicado;

    public JugadorControl(Jugador jugador) {
        this.jugador = jugador;
        this.direccion = Direccion.ABAJO;
        this.estado = Estado.IDLE;
    }

    public void actualizar(float delta, Mapa mapa) {

        // =========================
        // MINANDO: el jugador queda quieto hasta terminar el golpe
        // =========================

        if (estado == Estado.MINAR) {

            actualizarMinado(delta, mapa);
            return;
        }

        // =========================
        // ENTRADA DEL JUGADOR
        // =========================

        boolean arriba = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean abajo = Gdx.input.isKeyPressed(Input.Keys.S);
        boolean izquierda = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean derecha = Gdx.input.isKeyPressed(Input.Keys.D);

        boolean correr = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);


        // =========================
        // INTERACCIONES
        // =========================

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {

            arbol arbolCercano = mapa.obtenerArbolCercano(
                    jugador.getPosicionX(),
                    jugador.getPosicionY(),
                    100f
            );

            if (arbolCercano != null) {

                jugador.talarArbol(arbolCercano, mapa);

            } else {

                roca rocaCercana = mapa.obtenerRocaCercana(
                        jugador.getPosicionX(),
                        jugador.getPosicionY(),
                        100f
                );

                if (rocaCercana != null) {

                    // Si se puede minar, arranca la animacion.
                    // La piedra se entrega en el momento del golpe.
                    if (jugador.puedeMinar(rocaCercana)) {

                        iniciarMinado(rocaCercana);

                        // Importante: cortar aca, si no el codigo de abajo
                        // ("si no se mueve") vuelve el estado a IDLE.
                        return;
                    }

                } else {

                    System.out.println("No hay ningun recurso cerca.");
                }
            }
        }


        // =========================
        // SI NO SE MUEVE
        // =========================

        if (!arriba && !abajo && !izquierda && !derecha) {

            estado = Estado.IDLE;
            return;
        }


        // =========================
        // DIRECCIÓN DEL JUGADOR
        // =========================

        float direccionX = 0f;
        float direccionY = 0f;

        if (arriba) {
            direccionY += 1f;
        }

        if (abajo) {
            direccionY -= 1f;
        }

        if (izquierda) {
            direccionX -= 1f;
        }

        if (derecha) {
            direccionX += 1f;
        }


        // Determinar dirección visual
        if (Math.abs(direccionX) > Math.abs(direccionY)) {

            if (direccionX > 0) {
                direccion = Direccion.DERECHA;
            } else {
                direccion = Direccion.IZQUIERDA;
            }

        } else if (Math.abs(direccionY) > 0) {

            if (direccionY > 0) {
                direccion = Direccion.ARRIBA;
            } else {
                direccion = Direccion.ABAJO;
            }
        }


        // =========================
        // VELOCIDAD
        // =========================

        float velocidadCaminar = 100f;
        float velocidadCorrer = 170f;

        float velocidad;


        // =========================
        // CORRER
        // =========================

        if (correr && jugador.getEnergia() > 0) {

            velocidad = velocidadCorrer;

            float movimientoX = direccionX * velocidad * delta;
            float movimientoY = direccionY * velocidad * delta;

            boolean pudoCorrer = jugador.correr(
                    movimientoX,
                    movimientoY,
                    mapa,
                    delta
            );

            if (pudoCorrer) {

                estado = Estado.CORRER;
                return;
            }
        }


        // =========================
        // CAMINAR
        // =========================

        velocidad = velocidadCaminar;

        float movimientoX = direccionX * velocidad * delta;
        float movimientoY = direccionY * velocidad * delta;

        jugador.mover(
                movimientoX,
                movimientoY,
                mapa
        );

        estado = Estado.CAMINAR;
    }


    // ==========================================================
    // MINADO
    // ==========================================================

    private void iniciarMinado(roca roca) {

        rocaObjetivo = roca;
        tiempoMinado = 0f;
        golpeAplicado = false;

        // El sprite de minar solo existe de costado, asi que el
        // jugador mira hacia el lado donde esta la roca.
        float centroJugadorX = jugador.getPosicionX() + 32f;

        if (roca.getPosicionX() < centroJugadorX) {
            direccion = Direccion.IZQUIERDA;
        } else {
            direccion = Direccion.DERECHA;
        }

        estado = Estado.MINAR;
    }

    private void actualizarMinado(float delta, Mapa mapa) {

        tiempoMinado += delta;

        // Momento del golpe: el pico toca la roca
        if (!golpeAplicado
                && tiempoMinado >= JugadorAnimacion.getTiempoImpactoMinar()) {

            golpeAplicado = true;

            jugador.minarRoca(rocaObjetivo, mapa);
        }

        // Fin de la animacion: vuelve a quedar quieto
        if (tiempoMinado >= JugadorAnimacion.getDuracionMinar()) {

            rocaObjetivo = null;
            tiempoMinado = 0f;
            estado = Estado.IDLE;
        }
    }

    public boolean estaMinando() {
        return estado == Estado.MINAR;
    }

    public float getTiempoMinado() {
        return tiempoMinado;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public Estado getEstado() {
        return estado;
    }
    
    public void actualizarAtaque(
            combate sistemaCombate,
            Guardian guardian) {

        if (sistemaCombate == null) {
            return;
        }

        if (guardian == null) {
            return;
        }

        if (!guardian.estaVivo()) {
            return;
        }

        // No se puede atacar en medio de un golpe de pico
        if (estado == Estado.MINAR) {
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {

            sistemaCombate.atacar(
                jugador,
                guardian,
                direccion
            );
        }
    }
    
}
