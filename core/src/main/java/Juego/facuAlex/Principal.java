package Juego.facuAlex;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

import Juego.facuAlex.Mapa.Mapa;

public class Principal extends ApplicationAdapter {

    private SpriteBatch batch;
    private OrthographicCamera camara;

    private Jugador jugador;
    private Mapa mapa;

    private JugadorAnimacion jugadorAnimacion;
    private JugadorControl jugadorControl;

    private static final float TAMANO_JUGADOR = 64f;

    @Override
    public void create() {

        batch = new SpriteBatch();

        // Configurar cámara (vista de 900x600)
        camara = new OrthographicCamera();
        camara.setToOrtho(false, 900, 600);

        // ==============================
        // CREAR JUGADOR Y MAPA
        // ==============================

        jugador = new Jugador("Facu");
        mapa = new Mapa(900, 600);

        // Posicionar jugador
        jugador.mover(400, 250, mapa);

        // Centrar la cámara en la posición del jugador al iniciar
        camara.position.set(jugador.getPosicionX(), jugador.getPosicionY(), 0);
        camara.update();

        // ==============================
        // ANIMACIÓN Y CONTROLES
        // ==============================

        jugadorAnimacion = new JugadorAnimacion();
        jugadorControl = new JugadorControl(jugador);
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        // Actualizar jugador
        jugadorControl.actualizar(delta, mapa);
        jugadorAnimacion.actualizar(delta);

        // Centrar siempre la cámara donde está el jugador
        camara.position.set(
            jugador.getPosicionX() + TAMANO_JUGADOR / 2f,
            jugador.getPosicionY() + TAMANO_JUGADOR / 2f,
            0
        );
        camara.update();

        // Limpiar pantalla
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // 1. DIBUJAR MAPA .TMX
        mapa.dibujar(camara);

        // 2. DIBUJAR JUGADOR
        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        TextureRegion frame = obtenerFrameActual();

        batch.draw(
            frame,
            jugador.getPosicionX(),
            jugador.getPosicionY(),
            TAMANO_JUGADOR,
            TAMANO_JUGADOR
        );

        batch.end();
    }

    private TextureRegion obtenerFrameActual() {

        JugadorControl.Estado estado = jugadorControl.getEstado();
        JugadorControl.Direccion direccion = jugadorControl.getDireccion();

        if (estado == JugadorControl.Estado.IDLE) {
            switch (direccion) {
                case ARRIBA: return jugadorAnimacion.getFrame(jugadorAnimacion.getIdleUp());
                case ABAJO: return jugadorAnimacion.getFrame(jugadorAnimacion.getIdleDown());
                case IZQUIERDA: return jugadorAnimacion.getFrame(jugadorAnimacion.getIdleLeft());
                case DERECHA: return jugadorAnimacion.getFrame(jugadorAnimacion.getIdleRight());
            }
        }

        if (estado == JugadorControl.Estado.CAMINAR) {
            switch (direccion) {
                case ARRIBA: return jugadorAnimacion.getFrame(jugadorAnimacion.getWalkUp());
                case ABAJO: return jugadorAnimacion.getFrame(jugadorAnimacion.getWalkDown());
                case IZQUIERDA: return jugadorAnimacion.getFrame(jugadorAnimacion.getWalkLeft());
                case DERECHA: return jugadorAnimacion.getFrame(jugadorAnimacion.getWalkRight());
            }
        }

        if (estado == JugadorControl.Estado.CORRER) {
            switch (direccion) {
                case ARRIBA: return jugadorAnimacion.getFrame(jugadorAnimacion.getRunUp());
                case ABAJO: return jugadorAnimacion.getFrame(jugadorAnimacion.getRunDown());
                case IZQUIERDA: return jugadorAnimacion.getFrame(jugadorAnimacion.getRunLeft());
                case DERECHA: return jugadorAnimacion.getFrame(jugadorAnimacion.getRunRight());
            }
        }

        return jugadorAnimacion.getFrame(jugadorAnimacion.getIdleDown());
    }

    @Override
    public void dispose() {
        batch.dispose();
        jugadorAnimacion.dispose();
        mapa.dispose();
    }
}
