package Juego.facuAlex;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import Juego.facuAlex.Mapa.Mapa;
import Juego.facuAlex.recursos.arbol;

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

        direccion = Direccion.ABAJO;
        estado = Estado.IDLE;
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================

    public void actualizar(float delta, Mapa mapa) {

        boolean arriba =
            Gdx.input.isKeyPressed(Input.Keys.W);

        boolean abajo =
            Gdx.input.isKeyPressed(Input.Keys.S);

        boolean izquierda =
            Gdx.input.isKeyPressed(Input.Keys.A);

        boolean derecha =
            Gdx.input.isKeyPressed(Input.Keys.D);

        boolean correr =
            Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        float velocidad;
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {

            arbol arbolCercano =
                mapa.obtenerArbolCercano(
                    jugador.getPosicionX(),
                    jugador.getPosicionY(),
                    100f
                );

            if (arbolCercano != null) {

                jugador.talarArbol(
                    arbolCercano,
                    mapa
                );

            } else {

                System.out.println(
                    "No hay ningun arbol cerca."
                );
            }
        }

        // ==========================================
        // NINGUNA TECLA
        // ==========================================

        if (!arriba && !abajo && !izquierda && !derecha) {

            estado = Estado.IDLE;

            return;
        }

        // ==========================================
        // VELOCIDAD
        // ==========================================

        if (correr) {

            velocidad = 180f;
            estado = Estado.CORRER;

        } else {

            velocidad = 100f;
            estado = Estado.CAMINAR;
        }

        // ==========================================
        // MOVIMIENTO
        // ==========================================

    
        estado = Estado.IDLE;
        float movimientoX = 0f;
        float movimientoY = 0f;

        if (!arriba && !abajo && !izquierda && !derecha) {
            return;
        }

        // Determinar estado y velocidad
        if (correr) {
            velocidad = 180f;
            estado = Estado.CORRER;
        } else {
            velocidad = 100f;
            estado = Estado.CAMINAR;
        }

        // Calcular componentes
        if (arriba) movimientoY += velocidad * delta;
        if (abajo) movimientoY -= velocidad * delta;
        if (izquierda) movimientoX -= velocidad * delta;
        if (derecha) movimientoX += velocidad * delta;

        // Actualizar dirección visual según el eje dominante
        if (Math.abs(movimientoX) > Math.abs(movimientoY)) {
            if (movimientoX > 0) {
                direccion = Direccion.DERECHA;
            } else {
                direccion = Direccion.IZQUIERDA;
            }
        } else if (Math.abs(movimientoY) > 0) {
            if (movimientoY > 0) {
                direccion = Direccion.ARRIBA;
            } else {
                direccion = Direccion.ABAJO;
            }
        }

        // ==========================================
        // MOVER
        // ==========================================

        if (estado == Estado.CORRER) {

            jugador.correr(
                movimientoX,
                movimientoY,
                mapa
            );

        } else {

            jugador.mover(
                movimientoX,
                movimientoY,
                mapa
            );
        }
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public Direccion getDireccion() {

        return direccion;
    }

    public Estado getEstado() {

        return estado;
    }
}
