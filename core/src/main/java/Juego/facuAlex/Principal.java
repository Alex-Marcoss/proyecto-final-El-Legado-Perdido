package Juego.facuAlex;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

import Juego.facuAlex.Herramientas.hacha;
import Juego.facuAlex.Herramientas.pico;
import Juego.facuAlex.Mapa.Mapa;
import Juego.facuAlex.recursos.arbol;
import Juego.facuAlex.recursos.roca;

public class Principal extends ApplicationAdapter {

    private SpriteBatch batch;

    private OrthographicCamera camara;

    private Jugador jugador;

    private Mapa mapa;

    private JugadorAnimacion jugadorAnimacion;

    private JugadorControl jugadorControl;

    private static final float TAMANO_JUGADOR = 64f;

    private InventarioUI inventarioUI;

    private BarrasSupervivencia barrasSupervivencia;

    private BarraRapidaUI barraRapidaUI;

    // Pantallas
    private MenuInicio menuInicio;

    private PantallaCarga pantallaCarga;

    private PantallaDerrota pantallaDerrota;

    // Estado del juego
    private boolean enMenu;

    private boolean cargando;

    private boolean derrota;

    // Carga por pasos
    private static final int PASOS_CARGA = 5;

    // Segundos mínimos que se muestra la pantalla de carga
    private static final float TIEMPO_MINIMO_CARGA = 0.4f;

    private int pasoCarga;

    private float tiempoCarga;

    @Override
    public void create() {

        batch = new SpriteBatch();

        // ==============================
        // CÁMARA
        // ==============================

        camara = new OrthographicCamera();

        camara.setToOrtho(false, 900, 600);

        // ==============================
        // PANTALLAS (se crean una sola vez)
        // ==============================

        pantallaDerrota = new PantallaDerrota();

        pantallaCarga = new PantallaCarga();

        menuInicio = new MenuInicio();

        // La partida se crea al tocar "Iniciar partida"
        enMenu = true;

        cargando = false;

        derrota = false;
    }

    // ==========================================================
    // CARGA DE LA PARTIDA (por pasos, con barra de progreso)
    // ==========================================================

    private void comenzarCarga() {

        enMenu = false;

        derrota = false;

        cargando = true;

        pasoCarga = 0;

        tiempoCarga = 0f;

        pantallaCarga.mostrar();

        pantallaCarga.setProgreso(0.05f, "Preparando...");
    }

    private void renderCarga(float delta) {

        ScreenUtils.clear(0f, 0f, 0f, 1f);

        tiempoCarga += delta;

        pantallaCarga.actualizar(delta);

        pantallaCarga.dibujar();

        if (pasoCarga < PASOS_CARGA) {

            // El siguiente paso se ejecuta recién cuando la barra llegó
            // al objetivo anterior, así siempre se ve avanzar
            if (pantallaCarga.alcanzoObjetivo()) {

                ejecutarPasoCarga(pasoCarga);

                pasoCarga++;
            }

        } else if (pantallaCarga.estaCompleta()
                && tiempoCarga >= TIEMPO_MINIMO_CARGA) {

            cargando = false;
        }
    }

    // El mensaje que se define al final de cada paso
    // describe lo que va a hacer el paso siguiente.
    private void ejecutarPasoCarga(int paso) {

        switch (paso) {

            case 0:

                // Liberar lo de la partida anterior (si existe)
                if (mapa != null) {
                    mapa.dispose();
                }

                if (jugadorAnimacion != null) {
                    jugadorAnimacion.dispose();
                }

                if (inventarioUI != null) {
                    inventarioUI.dispose();
                }

                if (barrasSupervivencia != null) {
                    barrasSupervivencia.dispose();
                }

                if (barraRapidaUI != null) {
                    barraRapidaUI.dispose();
                }

                pantallaCarga.setProgreso(0.20f, "Generando el mundo...");

                break;

            case 1:

                mapa = new Mapa(900, 600);

                pantallaCarga.setProgreso(0.50f, "Creando al jugador...");

                break;

            case 2:

                jugador = new Jugador("Facu");

                barrasSupervivencia = new BarrasSupervivencia(jugador);

                barraRapidaUI = new BarraRapidaUI(jugador);

                hacha hachaInicial = new hacha(20, 10);
                jugador.getInventario().agregarItem(hachaInicial);

                pico picoInicial = new pico(30, 15);
                jugador.getInventario().agregarItem(picoInicial);

                // Posición inicial
                // (si justo hay un arbol, roca o agua, busca el lugar libre mas cercano)
                float[] spawn = mapa.buscarPosicionLibre(3072, 3072);

                jugador.setPosicion(spawn[0], spawn[1]);

                pantallaCarga.setProgreso(0.75f, "Preparando la interfaz...");

                break;

            case 3:

                jugadorAnimacion = new JugadorAnimacion();

                jugadorControl = new JugadorControl(jugador);

                inventarioUI = new InventarioUI(jugador);

                pantallaCarga.setProgreso(0.95f, "\u00daltimos detalles...");

                break;

            case 4:

                // Cámara inicial
                camara.position.set(
                    jugador.getPosicionX() + TAMANO_JUGADOR / 2f,
                    jugador.getPosicionY() + TAMANO_JUGADOR / 2f,
                    0
                );

                camara.update();
                pantallaCarga.setProgreso(1f, "Listo");
                break;
        }
    }

    // ==========================================================
    // VOLVER AL MENÚ
    // ==========================================================

    private void volverAlMenu() {

        derrota = false;

        cargando = false;

        enMenu = true;

        menuInicio.mostrar();
    }

    // ==========================================================
    // RENDER DEL MENÚ DE INICIO
    // ==========================================================

    private void renderMenu(float delta) {

        ScreenUtils.clear(0f, 0f, 0f, 1f);

        MenuInicio.Accion accion = menuInicio.actualizar(delta);

        menuInicio.dibujar();

        switch (accion) {

            case INICIAR:

                comenzarCarga();

                break;

            case SALIR:

                Gdx.app.exit();

                break;

            default:

                break;
        }
    }

    // ==========================================================
    // RENDER
    // ==========================================================

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        // ==============================
        // MENÚ DE INICIO
        // ==============================

        if (enMenu) {

            renderMenu(delta);

            return;
        }

        // ==============================
        // PANTALLA DE CARGA
        // ==============================

        if (cargando) {

            renderCarga(delta);

            return;
        }

        // ==============================
        // ¿PERDIÓ?
        // ==============================

        if (!derrota && jugador.gameOver()) {

            derrota = true;

            pantallaDerrota.mostrar();
        }

        if (derrota) {

            renderDerrota(delta);

            return;
        }

        // ==============================
        // JUEGO NORMAL
        // ==============================

        jugador.actualizarEnergia(delta);

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

        // Orden de dibujo: lo que esta mas "arriba" en el mapa queda DETRAS
        // del jugador, y lo que esta mas "abajo" queda DELANTE. Asi, si el
        // jugador pasa por detras de un arbol, el arbol lo tapa.
        float yPies = jugador.getHitbox().y
                + jugador.getHitbox().height / 2f;

        // 1) Arboles y rocas que quedan DETRAS del jugador
        for (arbol arbol : mapa.getArboles()) {

            if (!arbol.estaTalado() && arbol.getYOrden() > yPies) {
                arbol.dibujar(batch);
            }
        }

        for (roca roca : mapa.getRocas()) {

            if (roca.getYOrden() > yPies) {
                roca.dibujar(batch);
            }
        }

        // 2) Jugador
        TextureRegion frame = obtenerFrameActual();

        batch.draw(
            frame,
            jugador.getPosicionX(),
            jugador.getPosicionY(),
            TAMANO_JUGADOR,
            TAMANO_JUGADOR
        );

        // 3) Arboles y rocas que quedan DELANTE del jugador
        for (arbol arbol : mapa.getArboles()) {

            if (!arbol.estaTalado() && arbol.getYOrden() <= yPies) {
                arbol.dibujar(batch);
            }
        }

        for (roca roca : mapa.getRocas()) {

            if (roca.getYOrden() <= yPies) {
                roca.dibujar(batch);
            }
        }

        batch.end();

        barrasSupervivencia.dibujar();

        barraRapidaUI.actualizar();

        if (!inventarioUI.estaAbierto()) {
            barraRapidaUI.dibujar();
        }

        inventarioUI.dibujar();
    }

    // ==========================================================
    // RENDER DE LA PANTALLA DE DERROTA
    // ==========================================================

    private void renderDerrota(float delta) {

        ScreenUtils.clear(0f, 0f, 0f, 1f);

        PantallaDerrota.Accion accion = pantallaDerrota.actualizar(delta);

        pantallaDerrota.dibujar();

        switch (accion) {

            case JUGAR_DE_NUEVO:

                comenzarCarga();

                break;

            case VOLVER_AL_MENU:

                volverAlMenu();

                break;

            default:

                break;
        }
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
        pantallaDerrota.dispose();
        pantallaCarga.dispose();
        menuInicio.dispose();

        // Si se sale desde el menú, la partida nunca se creó
        if (jugadorAnimacion != null) {
            jugadorAnimacion.dispose();
        }

        if (mapa != null) {
            mapa.dispose();
        }

        if (inventarioUI != null) {
            inventarioUI.dispose();
        }

        if (barrasSupervivencia != null) {
            barrasSupervivencia.dispose();
        }

        if (barraRapidaUI != null) {
            barraRapidaUI.dispose();
        }
    }
}
