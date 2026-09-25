package Juego.facuAlex;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

import Juego.facuAlex.Herramientas.hacha;
import Juego.facuAlex.Mapa.Mapa;
import Juego.facuAlex.recursos.arbol;

public class Principal extends ApplicationAdapter {

    private SpriteBatch batch;

    private OrthographicCamera camara;

    private Jugador jugador;

    private Mapa mapa;

    private JugadorAnimacion jugadorAnimacion;

    private JugadorControl jugadorControl;

    private static final float TAMANO_JUGADOR = 64f;
    
    private InventarioUI inventarioUI;
    
    
    @Override
    public void create() {

        batch = new SpriteBatch();

        // ==============================
        // CÁMARA
        // ==============================

        camara = new OrthographicCamera();

        camara.setToOrtho(false, 900, 600);

        // ==============================
        // CREAR MAPA Y JUGADOR
        // ==============================
 
        mapa = new Mapa(900, 600);

        jugador = new Jugador("Facu");
        
        hacha hachaInicial = new hacha(20, 10);
        jugador.getInventario().agregarItem(hachaInicial);

        // ==============================
        // POSICIÓN INICIAL
        // ==============================

        jugador.mover(3072, 3072, mapa);

        // ==============================
        // ANIMACIÓN Y CONTROLES
        // ==============================

        jugadorAnimacion = new JugadorAnimacion();

        jugadorControl = new JugadorControl(jugador);
        
        inventarioUI = new InventarioUI(jugador);

        // ==============================
        // CÁMARA INICIAL
        // ==============================

        camara.position.set(
            jugador.getPosicionX() + TAMANO_JUGADOR / 2f,
            jugador.getPosicionY() + TAMANO_JUGADOR / 2f,
            0
        );

        camara.update();
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        // Inventario
        inventarioUI.actualizar();

        // Mientras el inventario está abierto no movemos al jugador
        if (!inventarioUI.estaAbierto()) {
            jugadorControl.actualizar(delta, mapa);
        }

        jugadorAnimacion.actualizar(delta);

        // Cámara sigue al jugador
        camara.position.set(
            jugador.getPosicionX() + TAMANO_JUGADOR / 2f,
            jugador.getPosicionY() + TAMANO_JUGADOR / 2f,
            0
        );

        camara.update();

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

     // =========================
     // DIBUJAR EL MUNDO
     // =========================

     mapa.dibujar(camara);

     batch.setProjectionMatrix(camara.combined);

     batch.begin();

     for (arbol arbol : mapa.getArboles()) {

         if (!arbol.estaTalado()) {
             arbol.dibujar(batch);
         }
     }

     TextureRegion frame = obtenerFrameActual();

     batch.draw(
         frame,
         jugador.getPosicionX(),
         jugador.getPosicionY(),
         TAMANO_JUGADOR,
         TAMANO_JUGADOR
     );

     batch.end();


     // =========================
     // DIBUJAR INVENTARIO
     // =========================

     inventarioUI.dibujar();
    }

    // ==========================================================
    // OBTENER FRAME ACTUAL
    // ==========================================================

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

        // ==============================
        // DEFAULT
        // ==============================

        return jugadorAnimacion.getFrame(
            jugadorAnimacion.getIdleDown()
        );
    }

    // ==========================================================
    // DISPOSE
    // ==========================================================

    @Override
    public void dispose() {

        batch.dispose();
        jugadorAnimacion.dispose();
        mapa.dispose();
        inventarioUI.dispose();
    }
    
   
	
}
