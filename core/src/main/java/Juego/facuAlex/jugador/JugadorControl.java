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
        MINAR,
        TALAR,
        GOLPEAR
    }

    // =========================
    // ESTADO DE MINADO
    // =========================


    private roca rocaObjetivo;


    private float tiempoMinado;


    private boolean golpeAplicado;

    // =========================
    // ESTADO DE TALADO
    // =========================


    private arbol arbolObjetivo;


    private float tiempoTalado;


    private boolean hachazoAplicado;

    // =========================
    // ESTADO DE GOLPE 
    // =========================


    private float tiempoGolpe;


    private boolean punoAplicado;

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
        // GOLPEANDO: el jugador queda quieto hasta terminar el golpe
        // =========================

        if (estado == Estado.GOLPEAR) {
            return;
        }

        // =========================
        // TALANDO: el jugador queda quieto hasta terminar el golpe
        // =========================

        if (estado == Estado.TALAR) {

            actualizarTalado(delta, mapa);
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

                // Si se puede talar, arranca la animacion.
                // La madera se entrega en el momento del golpe.
                if (jugador.puedeTalar(arbolCercano)) {

                    iniciarTalado(arbolCercano);

                    // Cortar aca: si no, el codigo de abajo
                    // ("si no se mueve") vuelve el estado a IDLE.
                    return;
                }

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

    // ==========================================================
    // TALADO
    // ==========================================================

    private void iniciarTalado(arbol arbol) {

        arbolObjetivo = arbol;
        tiempoTalado = 0f;
        hachazoAplicado = false;

        // El sprite de talar solo existe de costado, asi que el
        // jugador mira hacia el lado donde esta el arbol.
        float centroJugadorX = jugador.getPosicionX() + 32f;

        if (arbol.getPosicionX() < centroJugadorX) {
            direccion = Direccion.IZQUIERDA;
        } else {
            direccion = Direccion.DERECHA;
        }

        estado = Estado.TALAR;
    }

    private void actualizarTalado(float delta, Mapa mapa) {

        tiempoTalado += delta;

        // Momento del golpe: el hacha toca el tronco
        if (!hachazoAplicado
                && tiempoTalado >= JugadorAnimacion.getTiempoImpactoTalar()) {

            hachazoAplicado = true;

            jugador.talarArbol(arbolObjetivo, mapa);
        }

        // Fin de la animacion: vuelve a quedar quieto
        if (tiempoTalado >= JugadorAnimacion.getDuracionTalar()) {

            arbolObjetivo = null;
            tiempoTalado = 0f;
            estado = Estado.IDLE;
        }
    }

    public boolean estaGolpeando() {
        return estado == Estado.GOLPEAR;
    }

    public float getTiempoGolpe() {
        return tiempoGolpe;
    }

    public boolean estaTalando() {
        return estado == Estado.TALAR;
    }

    public float getTiempoTalado() {
        return tiempoTalado;
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
            float delta,
            combate sistemaCombate,
            Guardian guardian) {

        // =========================
        // GOLPEANDO: avanza la animacion y pega en el impacto.
        // Va antes de los chequeos de abajo para que la animacion
        // termine siempre, aunque el guardian muera en el medio.
        // =========================

        if (estado == Estado.GOLPEAR) {

            actualizarGolpe(delta, sistemaCombate, guardian);
            return;
        }

        if (sistemaCombate == null) {
            return;
        }

        if (guardian == null) {
            return;
        }

        if (!guardian.estaVivo()) {
            return;
        }

        // No se puede atacar en medio de un golpe de pico o hacha
        if (estado == Estado.MINAR || estado == Estado.TALAR) {
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {

            // Sin energia no hay golpe: solo avisa (atacar() lo imprime)
            if (jugador.getEnergia() < sistemaCombate.getEnergiaAtaque()) {

                sistemaCombate.atacar(
                    jugador,
                    guardian,
                    direccion
                );

                return;
            }

            iniciarGolpe();
        }
    }

    private void iniciarGolpe() {

        tiempoGolpe = 0f;
        punoAplicado = false;
        estado = Estado.GOLPEAR;
    }

    private void actualizarGolpe(
            float delta,
            combate sistemaCombate,
            Guardian guardian) {

        tiempoGolpe += delta;

        // Momento del impacto: el punio llega al frente
        if (!punoAplicado
                && tiempoGolpe >= JugadorAnimacion.getTiempoImpactoGolpear()) {

            punoAplicado = true;

            if (sistemaCombate != null
                    && guardian != null
                    && guardian.estaVivo()) {

                sistemaCombate.atacar(
                    jugador,
                    guardian,
                    direccion
                );
            }
        }

        // Fin de la animacion: vuelve a quedar quieto
        if (tiempoGolpe >= JugadorAnimacion.getDuracionGolpear()) {

            tiempoGolpe = 0f;
            estado = Estado.IDLE;
        }
    }
    
}
