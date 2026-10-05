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
        CORRER
    }

    public JugadorControl(Jugador jugador) {
        this.jugador = jugador;
        this.direccion = Direccion.ABAJO;
        this.estado = Estado.IDLE;
    }

    public void actualizar(float delta, Mapa mapa) {

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

                    jugador.minarRoca(rocaCercana, mapa);

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

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {

            sistemaCombate.atacar(
                jugador,
                guardian,
                direccion
            );
        }
    }
    
}