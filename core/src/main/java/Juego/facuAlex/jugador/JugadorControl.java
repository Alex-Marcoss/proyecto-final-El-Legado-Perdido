package Juego.facuAlex.jugador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import Juego.facuAlex.Mapa.Mapa;
import Juego.facuAlex.enemigos.guardian.Guardian;
import Juego.facuAlex.recursos.Comida;
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
        GOLPEAR,
        RECOLECTAR,
        COMER,
        HERIDO
    }


    // ==========================================================
    // ESTADO DE MINADO
    // ==========================================================

    private roca rocaObjetivo;

    private float tiempoMinado;

    private boolean golpeAplicado;


    // ==========================================================
    // ESTADO DE TALADO
    // ==========================================================

    private arbol arbolObjetivo;

    private float tiempoTalado;

    private boolean hachazoAplicado;


    // ==========================================================
    // ESTADO DE GOLPE
    // ==========================================================

    private float tiempoGolpe;

    private boolean punoAplicado;


    // ==========================================================
    // ESTADO DE RECOLECCION (bayas y fibra)
    // ==========================================================

    private float tiempoRecoleccion;

    private boolean recoleccionAplicada;


    private Runnable accionRecoleccion;


    // ==========================================================
    // ESTADO DE COMER
    // ==========================================================

    private float tiempoComer;

    private boolean comidaAplicada;


    private Runnable accionComer;


    // ==========================================================
    // ESTADO HERIDO
    // ==========================================================

    private float tiempoHerido;


    // ==========================================================
    // CONSTRUCTOR
    // ==========================================================

    public JugadorControl(Jugador jugador) {

        this.jugador = jugador;

        this.direccion = Direccion.ABAJO;

        this.estado = Estado.IDLE;
    }


    // ==========================================================
    // ACTUALIZAR JUGADOR
    // ==========================================================

    public void actualizar(
            float delta,
            Mapa mapa) {


        // ======================================================
        // RECIBIO UN GOLPE
        // ======================================================

        if (jugador.consumirGolpeRecibido()
                && jugador.estaVivo()) {

            iniciarHerido();
        }


        // ======================================================
        // HERIDO
        // ======================================================

        if (estado == Estado.HERIDO) {

            tiempoHerido += delta;

            if (tiempoHerido >= JugadorAnimacion.getDuracionDano()) {

                tiempoHerido = 0f;

                estado = Estado.IDLE;
            }

            return;
        }


        // ======================================================
        // MINANDO
        // ======================================================

        if (estado == Estado.MINAR) {

            actualizarMinado(delta, mapa);

            return;
        }


        // ======================================================
        // GOLPEANDO
        // ======================================================

        if (estado == Estado.GOLPEAR) {

            // El ataque se actualiza desde actualizarAtaque()
            return;
        }


        // ======================================================
        // TALANDO
        // ======================================================

        if (estado == Estado.TALAR) {

            actualizarTalado(delta, mapa);

            return;
        }


        // ======================================================
        // RECOLECTANDO (bayas / fibra)
        // ======================================================

        if (estado == Estado.RECOLECTAR) {

            actualizarRecoleccion(delta);

            return;
        }


        // ======================================================
        // COMIENDO
        // ======================================================

        if (estado == Estado.COMER) {

            actualizarComer(delta);

            return;
        }


        // ======================================================
        // ENTRADA DEL JUGADOR
        // ======================================================

        boolean arriba =
                Gdx.input.isKeyPressed(
                    Input.Keys.W
                );

        boolean abajo =
                Gdx.input.isKeyPressed(
                    Input.Keys.S
                );

        boolean izquierda =
                Gdx.input.isKeyPressed(
                    Input.Keys.A
                );

        boolean derecha =
                Gdx.input.isKeyPressed(
                    Input.Keys.D
                );

        boolean correr =
                Gdx.input.isKeyPressed(
                    Input.Keys.SHIFT_LEFT
                )
                ||
                Gdx.input.isKeyPressed(
                    Input.Keys.SHIFT_RIGHT
                );


        // ======================================================
        // COMER
        // ======================================================

        if (Gdx.input.isKeyJustPressed(
                Input.Keys.F)) {


            if (comerBayas()) {

                return;
            }
        }


        // ======================================================
        // INTERACCIONES
        // ======================================================

        if (Gdx.input.isKeyJustPressed(
                Input.Keys.E)) {


            arbol arbolCercano =
                    mapa.obtenerArbolCercano(
                        jugador.getPosicionX(),
                        jugador.getPosicionY(),
                        100f
                    );


            // ==================================================
            // TALADO
            // ==================================================

            if (arbolCercano != null) {

                if (jugador.puedeTalar(arbolCercano)) {

                    iniciarTalado(arbolCercano);

                    return;
                }

            } else {


                // ==============================================
                // MINADO
                // ==============================================

                roca rocaCercana =
                        mapa.obtenerRocaCercana(
                            jugador.getPosicionX(),
                            jugador.getPosicionY(),
                            100f
                        );


                if (rocaCercana != null) {

                    if (jugador.puedeMinar(rocaCercana)) {

                        iniciarMinado(rocaCercana);

                        return;
                    }

                } else {

                    System.out.println(
                        "No hay ningun recurso cerca."
                    );
                }
            }
        }


        // ======================================================
        // SI NO SE MUEVE
        // ======================================================

        if (
            !arriba &&
            !abajo &&
            !izquierda &&
            !derecha
        ) {

            estado = Estado.IDLE;

            return;
        }


        // ======================================================
        // DIRECCION DEL MOVIMIENTO
        // ======================================================

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


        // ======================================================
        // DIRECCION VISUAL
        // ======================================================

        if (
            Math.abs(direccionX)
            >
            Math.abs(direccionY)
        ) {

            if (direccionX > 0) {

                direccion =
                        Direccion.DERECHA;

            } else {

                direccion =
                        Direccion.IZQUIERDA;
            }

        } else if (
            Math.abs(direccionY) > 0
        ) {

            if (direccionY > 0) {

                direccion =
                        Direccion.ARRIBA;

            } else {

                direccion =
                        Direccion.ABAJO;
            }
        }


        // ======================================================
        // VELOCIDADES
        // ======================================================

        float velocidadCaminar =
                100f;

        float velocidadCorrer =
                170f;

        float velocidad;


        // ======================================================
        // CORRER
        // ======================================================

        if (
            correr
            &&
            jugador.getEnergia() > 0
        ) {

            velocidad =
                    velocidadCorrer;


            float movimientoX =
                    direccionX
                    *
                    velocidad
                    *
                    delta;


            float movimientoY =
                    direccionY
                    *
                    velocidad
                    *
                    delta;


            boolean pudoCorrer =
                    jugador.correr(
                        movimientoX,
                        movimientoY,
                        mapa,
                        delta
                    );


            if (pudoCorrer) {

                estado =
                        Estado.CORRER;

                return;
            }
        }


        // ======================================================
        // CAMINAR
        // ======================================================

        velocidad =
                velocidadCaminar;


        float movimientoX =
                direccionX
                *
                velocidad
                *
                delta;


        float movimientoY =
                direccionY
                *
                velocidad
                *
                delta;


        jugador.mover(
            movimientoX,
            movimientoY,
            mapa
        );


        estado =
                Estado.CAMINAR;
    }


    // ==========================================================
    // MINADO
    // ==========================================================

    private void iniciarMinado(roca roca) {

        rocaObjetivo = roca;

        tiempoMinado = 0f;

        golpeAplicado = false;


        // El sprite de minar es lateral.
        // El jugador mira hacia la roca.

        float centroJugadorX =
                jugador.getPosicionX() + 32f;


        if (roca.getPosicionX() < centroJugadorX) {

            direccion =
                    Direccion.IZQUIERDA;

        } else {

            direccion =
                    Direccion.DERECHA;
        }


        estado =
                Estado.MINAR;
    }


    private void actualizarMinado(
            float delta,
            Mapa mapa) {

        tiempoMinado += delta;




        if (
            !golpeAplicado
            &&
            tiempoMinado >=
                JugadorAnimacion.getTiempoImpactoMinar()
        ) {

            golpeAplicado = true;

            jugador.minarRoca(
                rocaObjetivo,
                mapa
            );
        }



        if (
            tiempoMinado >=
                JugadorAnimacion.getDuracionMinar()
        ) {

            rocaObjetivo = null;

            tiempoMinado = 0f;

            estado =
                    Estado.IDLE;
        }
    }


    // ==========================================================
    // TALADO
    // ==========================================================

    private void iniciarTalado(arbol arbol) {

        arbolObjetivo = arbol;

        tiempoTalado = 0f;

        hachazoAplicado = false;




        float centroJugadorX =
                jugador.getPosicionX() + 32f;


        if (arbol.getPosicionX() < centroJugadorX) {

            direccion =
                    Direccion.IZQUIERDA;

        } else {

            direccion =
                    Direccion.DERECHA;
        }


        estado =
                Estado.TALAR;
    }


    private void actualizarTalado(
            float delta,
            Mapa mapa) {

        tiempoTalado += delta;




        if (
            !hachazoAplicado
            &&
            tiempoTalado >=
                JugadorAnimacion.getTiempoImpactoTalar()
        ) {

            hachazoAplicado = true;

            jugador.talarArbol(
                arbolObjetivo,
                mapa
            );
        }




        if (
            tiempoTalado >=
                JugadorAnimacion.getDuracionTalar()
        ) {

            arbolObjetivo = null;

            tiempoTalado = 0f;

            estado =
                    Estado.IDLE;
        }
    }


    // ==========================================================
    // RECOLECCION 
    // ==========================================================


    public boolean puedeRecolectar() {

        return estado == Estado.IDLE
                || estado == Estado.CAMINAR
                || estado == Estado.CORRER;
    }




    public void iniciarRecoleccion(
            float xObjetivo,
            Runnable alRecoger) {

        accionRecoleccion = alRecoger;

        tiempoRecoleccion = 0f;

        recoleccionAplicada = false;




        float centroJugadorX =
                jugador.getPosicionX() + 32f;


        if (xObjetivo < centroJugadorX) {

            direccion =
                    Direccion.IZQUIERDA;

        } else {

            direccion =
                    Direccion.DERECHA;
        }


        estado =
                Estado.RECOLECTAR;
    }


    private void actualizarRecoleccion(float delta) {

        tiempoRecoleccion += delta;




        if (
            !recoleccionAplicada
            &&
            tiempoRecoleccion >=
                JugadorAnimacion.getTiempoImpactoRecolectar()
        ) {

            recoleccionAplicada = true;

            if (accionRecoleccion != null) {

                accionRecoleccion.run();
            }
        }




        if (
            tiempoRecoleccion >=
                JugadorAnimacion.getDuracionRecolectar()
        ) {

            accionRecoleccion = null;

            tiempoRecoleccion = 0f;

            estado =
                    Estado.IDLE;
        }
    }


    // ==========================================================
    // COMER
    // ==========================================================

    private void iniciarComer(Runnable alMorder) {

        accionComer = alMorder;

        tiempoComer = 0f;

        comidaAplicada = false;

        estado =
                Estado.COMER;
    }


    private void actualizarComer(float delta) {

        tiempoComer += delta;




        if (
            !comidaAplicada
            &&
            tiempoComer >=
                JugadorAnimacion.getTiempoImpactoComer()
        ) {

            comidaAplicada = true;

            if (accionComer != null) {

                accionComer.run();
            }
        }




        if (
            tiempoComer >=
                JugadorAnimacion.getDuracionComer()
        ) {

            accionComer = null;

            tiempoComer = 0f;

            estado =
                    Estado.IDLE;
        }
    }


    // ==========================================================
    // HERIDO
    // ==========================================================

    private void iniciarHerido() {



        rocaObjetivo = null;

        arbolObjetivo = null;

        tiempoMinado = 0f;

        tiempoTalado = 0f;

        tiempoGolpe = 0f;

        accionRecoleccion = null;

        tiempoRecoleccion = 0f;

        accionComer = null;

        tiempoComer = 0f;


        tiempoHerido = 0f;

        estado =
                Estado.HERIDO;
    }


    // ==========================================================
    // GETTERS DE ANIMACION
    // ==========================================================

    public boolean estaHerido() {

        return estado == Estado.HERIDO;
    }


    public float getTiempoHerido() {

        return tiempoHerido;
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


    public boolean estaComiendo() {

        return estado == Estado.COMER;
    }


    public float getTiempoComer() {

        return tiempoComer;
    }


    public boolean estaRecolectando() {

        return estado == Estado.RECOLECTAR;
    }


    public float getTiempoRecoleccion() {

        return tiempoRecoleccion;
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


    // ==========================================================
    // ATAQUE
    // ==========================================================

    public void actualizarAtaque(
            combate sistemaCombate,
            Guardian guardian) {


        // Delta se obtiene aca porque Principal
        // utiliza la version de 2 parametros.

        float delta =
                Gdx.graphics.getDeltaTime();


        // ======================================================
        // SI YA ESTA GOLPEANDO
        // ======================================================

        if (estado == Estado.GOLPEAR) {

            actualizarGolpe(
                delta,
                sistemaCombate,
                guardian
            );

            return;
        }


        // ======================================================
        // COMPROBACIONES
        // ======================================================

        if (sistemaCombate == null) {

            return;
        }


        if (guardian == null) {

            return;
        }


        if (!guardian.estaVivo()) {

            return;
        }




        if (
            estado == Estado.MINAR
            ||
            estado == Estado.TALAR
            ||
            estado == Estado.RECOLECTAR
            ||
            estado == Estado.COMER
            ||
            estado == Estado.HERIDO
        ) {

            return;
        }


        // ======================================================
        // ATAQUE CON ESPACIO
        // ======================================================

        if (
            Gdx.input.isKeyJustPressed(
                Input.Keys.SPACE
            )
        ) {




            if (
                jugador.getEnergia()
                <
                sistemaCombate.getEnergiaAtaque()
            ) {

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


    // ==========================================================
    // INICIAR GOLPE
    // ==========================================================

    private void iniciarGolpe() {

        tiempoGolpe = 0f;

        punoAplicado = false;

        estado =
                Estado.GOLPEAR;
    }


    // ==========================================================
    // ACTUALIZAR GOLPE
    // ==========================================================

    private void actualizarGolpe(
            float delta,
            combate sistemaCombate,
            Guardian guardian) {

        tiempoGolpe += delta;


        // ======================================================
        // IMPACTO
        // ======================================================

        if (
            !punoAplicado
            &&
            tiempoGolpe >=
                JugadorAnimacion.getTiempoImpactoGolpear()
        ) {

            punoAplicado = true;


            if (
                sistemaCombate != null
                &&
                guardian != null
                &&
                guardian.estaVivo()
            ) {

                sistemaCombate.atacar(
                    jugador,
                    guardian,
                    direccion
                );
            }
        }


        // ======================================================
        // FIN DEL GOLPE
        // ======================================================

        if (
            tiempoGolpe >=
                JugadorAnimacion.getDuracionGolpear()
        ) {

            tiempoGolpe = 0f;

            estado =
                    Estado.IDLE;
        }
    }


    // ==========================================================
    // COMER BAYAS
    // ==========================================================



    private boolean comerBayas() {


        // ======================================================
        // COMPROBAR HAMBRE
        // ======================================================

        if (jugador.getHambre() >= 100) {

            System.out.println(
                "No tenes hambre."
            );

            return false;
        }


        // ======================================================
        // COMPROBAR INVENTARIO
        // ======================================================

        if (
            !jugador.getInventario()
                .tieneRecurso(
                    "Bayas",
                    1
                )
        ) {

            System.out.println(
                "No tenes bayas."
            );

            return false;
        }


        // ======================================================
        // CREAR COMIDA
        // ======================================================

        Comida bayas =
                new Comida(
                    "Bayas",
                    1,
                    10
                );


        // ======================================================
        // COMER
        // ======================================================

        iniciarComer(
            () -> jugador.comer(bayas)
        );

        return true;
    }
}
