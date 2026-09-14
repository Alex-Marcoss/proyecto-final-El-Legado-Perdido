`package Juego.facuAlex;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

import Juego.facuAlex.Mapa.Mapa;

public class Principal extends ApplicationAdapter {

    private SpriteBatch batch;

    private Jugador jugador;
    private Mapa mapa;

    private JugadorAnimacion jugadorAnimacion;
    private JugadorControl jugadorControl;

    @Override
    public void create() {

        batch = new SpriteBatch();

        // ==============================
        // CREAR JUGADOR
        // ==============================

        jugador = new Jugador("Facu");

        // ==============================
        // CREAR MAPA
        // ==============================

        mapa = new Mapa(900, 600);

        // Posición inicial
        jugador.mover(400, 250, mapa);

        // ==============================
        // CREAR ANIMACIONES
        // ==============================

        jugadorAnimacion = new JugadorAnimacion();

        // ==============================
        // CREAR CONTROL
        // ==============================

        jugadorControl = new JugadorControl(jugador);
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        // ==============================
        // ACTUALIZAR
        // ==============================

        jugadorControl.actualizar(delta, mapa);

        jugadorAnimacion.actualizar(delta);

        // ==============================
        // LIMPIAR PANTALLA
        // ==============================

        ScreenUtils.clear(
            0.15f,
            0.15f,
            0.2f,
            1f
        );

        // ==============================
        // DIBUJAR
        // ==============================

        batch.begin();

        TextureRegion frame =
            obtenerFrameActual();

        batch.draw(
            frame,
            jugador.getPosicionX(),
            jugador.getPosicionY(),
            64,
            64
        );

        batch.end();
    }

    // =========================================================
    // OBTENER FRAME SEGUN ESTADO Y DIRECCION
    // =========================================================

    private TextureRegion obtenerFrameActual() {

        JugadorControl.Estado estado =
            jugadorControl.getEstado();

        JugadorControl.Direccion direccion =
            jugadorControl.getDireccion();

        // ==============================
        // IDLE
        // ==============================

        if (estado == JugadorControl.Estado.IDLE) {

            switch (direccion) {

                case ARRIBA:
                    return jugadorAnimacion.getFrame(
                        jugadorAnimacion.getIdleUp()
                    );

                case ABAJO:
                    return jugadorAnimacion.getFrame(
                        jugadorAnimacion.getIdleDown()
                    );

                case IZQUIERDA:
                    return jugadorAnimacion.getFrame(
                        jugadorAnimacion.getIdleLeft()
                    );

                case DERECHA:
                    return jugadorAnimacion.getFrame(
                        jugadorAnimacion.getIdleRight()
                    );
            }
        }

        // ==============================
        // CAMINAR
        // ==============================

        if (estado == JugadorControl.Estado.CAMINAR) {

            switch (direccion) {

                case ARRIBA:
                    return jugadorAnimacion.getFrame(
                        jugadorAnimacion.getWalkUp()
                    );

                case ABAJO:
                    return jugadorAnimacion.getFrame(
                        jugadorAnimacion.getWalkDown()
                    );

                case IZQUIERDA:
                    return jugadorAnimacion.getFrame(
                        jugadorAnimacion.getWalkLeft()
                    );

                case DERECHA:
                    return jugadorAnimacion.getFrame(
                        jugadorAnimacion.getWalkRight()
                    );
            }
        }

        // ==============================
        // CORRER
        // ==============================

        if (estado == JugadorControl.Estado.CORRER) {

            switch (direccion) {

                case ARRIBA:
                    return jugadorAnimacion.getFrame(
                        jugadorAnimacion.getRunUp()
                    );

                case ABAJO:
                    return jugadorAnimacion.getFrame(
                        jugadorAnimacion.getRunDown()
                    );

                case IZQUIERDA:
                    return jugadorAnimacion.getFrame(
                        jugadorAnimacion.getRunLeft()
                    );

                case DERECHA:
                    return jugadorAnimacion.getFrame(
                        jugadorAnimacion.getRunRight()
                    );
            }
        }

        // Por seguridad
        return jugadorAnimacion.getFrame(
            jugadorAnimacion.getIdleDown()
        );
    }

    @Override
    public void dispose() {

        batch.dispose();

        jugadorAnimacion.dispose();
    }
}
```
