package Juego.facuAlex;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import Juego.facuAlex.Mapa.*;
import Juego.facuAlex.Pantallas.*;
import Juego.facuAlex.enemigos.guardian.*;
import Juego.facuAlex.inventario.*;
import Juego.facuAlex.jugador.*;
import Juego.facuAlex.receta.RecetarioUI;
import Juego.facuAlex.recursos.*;
import Juego.facuAlex.recursos.Comida;
import Juego.facuAlex.sistemas.*;
import Juego.facuAlex.enemigos.*;
import Juego.facuAlex.estructuras.*;

public class Principal extends ApplicationAdapter {

    private SpriteBatch batch;
    private OrthographicCamera camara;
    private Jugador jugador;
    private Mapa mapa;
    private JugadorAnimacion jugadorAnimacion;
    private JugadorControl jugadorControl;

    // CICLO DIA / NOCHE

    private CicloDia cicloDia;
    private OverlayNoche overlayNoche;

    private static final float TAMANO_JUGADOR = 64f;
    private static final float TAMANO_GUARDIAN = 96f;

    private InventarioUI inventarioUI;
    private RecetarioUI recetarioUI;
    private BarrasSupervivencia barrasSupervivencia;
    private BarraRapidaUI barraRapidaUI;

    private float graciaGuardian;
    private GestorCriaturas GestorCriaturas;
    private BitmapFont fuenteInteraccion;
    private ShapeRenderer shapeRenderer;
    // TEMPLO

    private enum Ubicacion {
        MUNDO,
        TEMPLO
    }

    private Ubicacion ubicacion = Ubicacion.MUNDO;

    private Templo templo;
    private InteriorTemplo interiorTemplo;
    private TransicionFade transicion;
    private BitmapFont font;
    private boolean guardianColocado;

    private static final float ESCALA_TEMPLO = 1.5f;
    private static final float ESCALA_INTERIOR = 3f;

    // PANTALLAS

    private MenuInicio menuInicio;
    private PantallaCarga pantallaCarga;
    private PantallaDerrota pantallaDerrota;
    private PantallaVictoria pantallaVictoria;

    // ESTADO

    private boolean enMenu;
    private boolean cargando;
    private boolean derrota;
    private boolean victoria;

    // VICTORIA

    private static final float TIEMPO_ANTES_VICTORIA = 4f;
    private float tiempoRescateActivo;

    // CARGA

    private static final int PASOS_CARGA = 5;
    private static final float TIEMPO_MINIMO_CARGA = 0.4f;

    private int pasoCarga;
    private float tiempoCarga;

    // GUARDIAN

    private Guardian guardian;
    private combate sistemaCombate;

    // GEMA

    private GemaMundo gemaAzul;

    // RESCATE

    private PanelEstructuraRescate panelEstructuraRescate;

    // CONSTRUCCIÓN

    private boolean modoConstruccion;
    private int estructuraSeleccionada = 1;

    private static final float TAMANO_ESTRUCTURA = 64f;

    // Texturas para la interfaz de construcción.

    private Texture texturaPixel;
    private Texture texturaFogata;
    private Texture texturaMesa;
    private Texture texturaHorno;
    private Texture texturaCofre;
    private Texture texturaCama;

    // Lo que se ve en la previsualizacion: la fogata y el horno son hojas
    // de varios frames, asi que se muestra solo el primero.
    private TextureRegion regionFogata;
    private TextureRegion regionMesa;
    private TextureRegion regionHorno;
    private TextureRegion regionCofre;
    private TextureRegion regionCama;

    private boolean texturasConstruccionCargadas;

    @Override
    public void create() {

        batch = new SpriteBatch();

        camara = new OrthographicCamera();
        camara.setToOrtho(false, 900, 600);

        pantallaDerrota = new PantallaDerrota();
        pantallaVictoria = new PantallaVictoria();
        pantallaCarga = new PantallaCarga();
        menuInicio = new MenuInicio();
        transicion = new TransicionFade();
        font = new BitmapFont();

        // Se crea una sola vez; no depende de la partida.
        overlayNoche = new OverlayNoche();

        ubicacion = Ubicacion.MUNDO;
        enMenu = true;
        cargando = false;
        derrota = false;
        victoria = false;

        modoConstruccion = false;
        fuenteInteraccion = new BitmapFont();
        fuenteInteraccion.getData().setScale(0.8f);
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void resize(int width, int height) {
        Proporcion.actualizar(width, height);
    }

    private void cargarTexturasConstruccion() {

        if (texturasConstruccionCargadas) {
            return;
        }

        Pixmap pixmap = new Pixmap(
                1,
                1,
                Pixmap.Format.RGBA8888
        );

        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.fill();

        texturaPixel = new Texture(pixmap);
        pixmap.dispose();

        texturaFogata = new Texture(
                Gdx.files.internal("estructuras/fogata.png")
        );

        texturaMesa = new Texture(
                Gdx.files.internal("estructuras/mesaFabricacion.png")
        );

        texturaHorno = new Texture(
                Gdx.files.internal("estructuras/horno.png")
        );

        texturaCofre = new Texture(
                Gdx.files.internal("estructuras/cofre.png")
        );

        texturaCama = new Texture(
                Gdx.files.internal("estructuras/cama.png")
        );

        // Fogata y horno: sprites de 128x128 que se dibujan a 64x64
        // (igual que las clases Fogata y Horno), por eso Linear.
        texturaFogata.setFilter(
                Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear
        );

        texturaMesa.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
        );

        texturaHorno.setFilter(
                Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear
        );

        texturaCofre.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
        );

        texturaCama.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
        );

        // fogata.png: hoja de 4x4 frames, horno.png: hoja de 2x1 frames.
        // La vista previa usa el primer frame de cada una.
        regionFogata = new TextureRegion(
                texturaFogata,
                0, 0,
                texturaFogata.getWidth() / 4,
                texturaFogata.getHeight() / 4
        );

        regionHorno = new TextureRegion(
                texturaHorno,
                0, 0,
                texturaHorno.getWidth() / 2,
                texturaHorno.getHeight()
        );

        regionMesa = new TextureRegion(texturaMesa);
        regionCofre = new TextureRegion(texturaCofre);
        regionCama = new TextureRegion(texturaCama);

        texturasConstruccionCargadas = true;
    }

    private void comenzarCarga() {

        enMenu = false;
        derrota = false;
        victoria = false;

        ubicacion = Ubicacion.MUNDO;
        guardianColocado = false;
        tiempoRescateActivo = 0f;
        modoConstruccion = false;

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

            if (pantallaCarga.alcanzoObjetivo()) {
                ejecutarPasoCarga(pasoCarga);
                pasoCarga++;
            }

        } else if (
                pantallaCarga.estaCompleta()
                && tiempoCarga >= TIEMPO_MINIMO_CARGA
        ) {
            cargando = false;
        }
    }

    private void ejecutarPasoCarga(int paso) {

        switch (paso) {

            case 0:

                if (gemaAzul != null) {
                    gemaAzul.dispose();
                }

                gemaAzul = null;

                if (mapa != null) {
                    mapa.dispose();
                }

                if (jugadorAnimacion != null) {
                    jugadorAnimacion.dispose();
                }

                if (guardian != null) {
                    guardian.dispose();
                    guardian = null;
                }

                if (inventarioUI != null) {
                    inventarioUI.dispose();
                }

                if (recetarioUI != null) {
                    recetarioUI.dispose();
                }

                if (barrasSupervivencia != null) {
                    barrasSupervivencia.dispose();
                }

                if (barraRapidaUI != null) {
                    barraRapidaUI.dispose();
                }

                if (panelEstructuraRescate != null) {
                    panelEstructuraRescate.dispose();
                }

                if (templo != null) {
                    templo.dispose();
                    templo = null;
                }

                if (interiorTemplo != null) {
                    interiorTemplo.dispose();
                    interiorTemplo = null;
                }

                if (GestorCriaturas != null) {
                    GestorCriaturas.dispose();
                    GestorCriaturas = null;
                }

                pantallaCarga.setProgreso(
                        0.20f,
                        "Generando el mundo..."
                );

                break;

            case 1:

                mapa = new Mapa(900, 600);

                pantallaCarga.setProgreso(
                        0.50f,
                        "Creando al jugador..."
                );

                break;

            case 2:

                jugador = new Jugador("Facu");

                // Cada partida nueva empieza de día (día 1, 8:00).
                cicloDia = new CicloDia();

                barrasSupervivencia =
                        new BarrasSupervivencia(jugador);

                barraRapidaUI =
                        new BarraRapidaUI(jugador);

                Recursos madera =
                        new Recursos("Madera", 10);

                madera.cargarIcono("objetos/madera.png");

                jugador.getInventario().agregarRecurso(
                        madera, 10
                );

                Recursos piedra =
                        new Recursos("Piedra", 10);

                piedra.cargarIcono("objetos/piedra.png");

                jugador.getInventario().agregarRecurso(
                        piedra, 10
                );

                float[] spawn =
                        mapa.buscarPosicionLibre(3072, 3072);

                jugador.setPosicion(spawn[0], spawn[1]);

                sistemaCombate = new combate();

                guardian = new Guardian();

                guardian.setPosicion(
                        jugador.getPosicionX() + 100,
                        jugador.getPosicionY()
                );

                GestorCriaturas =
                        new GestorCriaturas(mapa, jugador);

                templo = new Templo(
                        "estructuras/templo.png",
                        spawn[0] + 300f,
                        spawn[1] + 100f,
                        ESCALA_TEMPLO
                );

                interiorTemplo =
                        new InteriorTemplo(
                                "templo.tmx",
                                ESCALA_INTERIOR
                        );

                jugador.setTemplo(templo);
                jugador.setInterior(null);

                pantallaCarga.setProgreso(
                        0.75f,
                        "Preparando la interfaz..."
                );

                break;

            case 3:

                jugadorAnimacion = new JugadorAnimacion();
                jugadorControl = new JugadorControl(jugador);
                inventarioUI = new InventarioUI(jugador);
                recetarioUI = new RecetarioUI(jugador);

                panelEstructuraRescate =
                        new PanelEstructuraRescate();

                cargarTexturasConstruccion();

                pantallaCarga.setProgreso(
                        0.95f,
                        "Últimos detalles..."
                );

                break;

            case 4:

                camara.position.set(
                        jugador.getPosicionX()
                                + TAMANO_JUGADOR / 2f,
                        jugador.getPosicionY()
                                + TAMANO_JUGADOR / 2f,
                        0
                );

                camara.update();

                pantallaCarga.setProgreso(1f, "Listo");

                break;

            default:
                break;
        }
    }

    private void volverAlMenu() {

        derrota = false;
        victoria = false;
        cargando = false;
        enMenu = true;
        modoConstruccion = false;

        menuInicio.mostrar();
    }

    private void renderMenu(float delta) {

        ScreenUtils.clear(0f, 0f, 0f, 1f);

        MenuInicio.Accion accion =
                menuInicio.actualizar(delta);

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

    private void comprobarMuerteGuardian() {

        if (guardian == null) {
            return;
        }

        if (!guardian.estaVivo() && gemaAzul == null) {

            gemaAzul = new GemaMundo(
                    guardian.getPosicionX(),
                    guardian.getPosicionY()
            );

            System.out.println(
                    "El Guardian dejo caer la Gema Azul."
            );
        }
    }

    private void actualizarRecursos(float delta) {

        for (arbustoBayas arbusto : mapa.getArbustosBayas()) {
            arbusto.actualizar(delta);
        }
    }

    // ==========================================================
    // CONSTRUCCIÓN: CONTROLES
    // ==========================================================

    private void actualizarConstruccion() {

        if (ubicacion != Ubicacion.MUNDO) {
            modoConstruccion = false;
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {

            modoConstruccion = !modoConstruccion;

            System.out.println(
                    modoConstruccion
                    ? "Modo construccion activado."
                    : "Modo construccion desactivado."
            );
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {

            modoConstruccion = false;

            System.out.println(
                    "Modo construccion cancelado."
            );
        }

        if (!modoConstruccion) {
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            estructuraSeleccionada = 1;
            System.out.println("Seleccionaste: Fogata");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            estructuraSeleccionada = 2;
            System.out.println("Seleccionaste: Mesa de fabricacion");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            estructuraSeleccionada = 3;
            System.out.println("Seleccionaste: Horno");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            estructuraSeleccionada = 4;
            System.out.println("Seleccionaste: Cofre");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            estructuraSeleccionada = 5;
            System.out.println("Seleccionaste: Cama");
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            colocarEstructuraSeleccionada();
        }
    }

    // ==========================================================
    // CONSTRUCCIÓN: PREVISUALIZACIÓN
    // ==========================================================

    private TextureRegion obtenerRegionSeleccionada() {

        switch (estructuraSeleccionada) {

            case 1:
                return regionFogata;

            case 2:
                return regionMesa;

            case 3:
                return regionHorno;

            case 4:
                return regionCofre;

            case 5:
                return regionCama;

            default:
                return null;
        }
    }

    private String obtenerNombreEstructuraSeleccionada() {

        switch (estructuraSeleccionada) {

            case 1:
                return "Fogata";

            case 2:
                return "Mesa de fabricacion";

            case 3:
                return "Horno";

            case 4:
                return "Cofre";

            case 5:
                return "Cama";

            default:
                return "Ninguna";
        }
    }

    private String obtenerCostoEstructuraSeleccionada() {

        switch (estructuraSeleccionada) {

            case 1:
                return "Costo: 3 madera + 2 piedra";

            case 2:
                return "Costo: 5 madera + 2 piedra";

            case 3:
                return "Costo: 3 madera + 6 piedra";

            case 4:
                return "Costo: 5 madera";

            case 5:
                return "Costo: 4 madera + 2 fibra";

            default:
                return "";
        }
    }

    private void dibujarPrevisualizacionConstruccion() {

        if (!modoConstruccion
                || ubicacion != Ubicacion.MUNDO) {
            return;
        }

        TextureRegion textura = obtenerRegionSeleccionada();

        if (textura == null) {
            return;
        }

        Vector3 posicionMouse = new Vector3(
                Gdx.input.getX(),
                Gdx.input.getY(),
                0f
        );

        camara.unproject(posicionMouse);

        float x = posicionMouse.x - TAMANO_ESTRUCTURA / 2f;
        float y = posicionMouse.y - TAMANO_ESTRUCTURA / 2f;

        // La estructura aparece semitransparente debajo del cursor.
        batch.setColor(1f, 1f, 1f, 0.55f);

        batch.draw(
                textura,
                x,
                y,
                TAMANO_ESTRUCTURA,
                TAMANO_ESTRUCTURA
        );

        batch.setColor(1f, 1f, 1f, 1f);
    }

    // ==========================================================
    // CONSTRUCCIÓN: PANEL VISUAL
    // ==========================================================

    private void dibujarModoConstruccion() {

        if (!modoConstruccion
                || ubicacion != Ubicacion.MUNDO) {
            return;
        }

        float centroX = camara.position.x;
        float arriba = camara.position.y + 280f;

        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        // Fondo del panel.
        batch.setColor(0.04f, 0.05f, 0.08f, 0.92f);

        batch.draw(
                texturaPixel,
                centroX - 260f,
                arriba - 112f,
                520f,
                105f
        );

        batch.setColor(0.25f, 0.65f, 1f, 1f);

        batch.draw(
                texturaPixel,
                centroX - 260f,
                arriba - 10f,
                520f,
                3f
        );

        batch.setColor(1f, 1f, 1f, 1f);

        font.getData().setScale(1.15f);

        font.draw(
                batch,
                "MODO CONSTRUCCION [P]",
                centroX - 242f,
                arriba - 28f
        );

        font.getData().setScale(1f);

        font.draw(
                batch,
                "Seleccion: " + obtenerNombreEstructuraSeleccionada(),
                centroX - 242f,
                arriba - 51f
        );

        font.draw(
                batch,
                obtenerCostoEstructuraSeleccionada(),
                centroX - 242f,
                arriba - 73f
        );

        font.draw(
                batch,
                "1-5: elegir    Click izquierdo: construir    ESC: cancelar",
                centroX - 242f,
                arriba - 96f
        );

        batch.end();
    }

    // ==========================================================
    // CONSTRUCCIÓN: COLOCAR ESTRUCTURA
    // ==========================================================

    private void colocarEstructuraSeleccionada() {

        Vector3 posicionMouse = new Vector3(
                Gdx.input.getX(),
                Gdx.input.getY(),
                0
        );

        camara.unproject(posicionMouse);

        float x = posicionMouse.x - TAMANO_ESTRUCTURA / 2f;
        float y = posicionMouse.y - TAMANO_ESTRUCTURA / 2f;

        Estructura nueva = null;

        int maderaNecesaria = 0;
        int piedraNecesaria = 0;
        int fibraNecesaria = 0;

        switch (estructuraSeleccionada) {

            case 1:
                nueva = new Fogata(x, y);
                maderaNecesaria = 3;
                piedraNecesaria = 2;
                break;

            case 2:
                nueva = new MesaFabricacion(x, y);
                maderaNecesaria = 5;
                piedraNecesaria = 2;
                break;

            case 3:
                nueva = new Horno(x, y);
                maderaNecesaria = 3;
                piedraNecesaria = 6;
                break;

            case 4:
                nueva = new Cofre(x, y);
                maderaNecesaria = 5;
                break;

            case 5:
                nueva = new Cama(x, y);
                maderaNecesaria = 4;
                fibraNecesaria = 2;
                break;

            default:
                return;
        }

        inventario inventarioJugador =
                jugador.getInventario();

        if (maderaNecesaria > 0
                && !inventarioJugador.tieneRecurso(
                        "Madera", maderaNecesaria)) {

            System.out.println("No tenes suficiente madera.");
            nueva.dispose();
            return;
        }

        if (piedraNecesaria > 0
                && !inventarioJugador.tieneRecurso(
                        "Piedra", piedraNecesaria)) {

            System.out.println("No tenes suficiente piedra.");
            nueva.dispose();
            return;
        }

        if (fibraNecesaria > 0
                && !inventarioJugador.tieneRecurso(
                        "Fibra", fibraNecesaria)) {

            System.out.println("No tenes suficiente fibra.");
            nueva.dispose();
            return;
        }

        if (!mapa.agregarEstructura(nueva)) {

            System.out.println(
                    "No podes construir en ese lugar."
            );

            nueva.dispose();
            return;
        }

        if (maderaNecesaria > 0) {
            inventarioJugador.gastarRecurso(
                    "Madera", maderaNecesaria
            );
        }

        if (piedraNecesaria > 0) {
            inventarioJugador.gastarRecurso(
                    "Piedra", piedraNecesaria
            );
        }

        if (fibraNecesaria > 0) {
            inventarioJugador.gastarRecurso(
                    "Fibra", fibraNecesaria
            );
        }

        System.out.println(
                "Construiste: " + nueva.getNombre()
        );

        modoConstruccion = false;
    }

    // ==========================================================
    // RECOLECCIÓN DE FIBRA
    // ==========================================================

    private void comprobarRecogerFibra() {

        if (!Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            return;
        }

        if (!jugadorControl.puedeRecolectar()) {
            return;
        }

        planta plantaCercana = null;
        float distanciaMenor = 90f;

        for (planta plantaActual : mapa.getPlantas()) {

            float dx = jugador.getPosicionX()
                    - plantaActual.getPosicionX();

            float dy = jugador.getPosicionY()
                    - plantaActual.getPosicionY();

            float distancia = (float) Math.sqrt(
                    dx * dx + dy * dy
            );

            if (distancia <= distanciaMenor
                    && !plantaActual.estaRecolectada()) {

                distanciaMenor = distancia;
                plantaCercana = plantaActual;
            }
        }

        if (plantaCercana == null) {
            return;
        }

        if (jugador.getEnergia()
                < plantaCercana.getEnergiaNecesaria()) {

            System.out.println(
                    "No tenes suficiente energia."
            );

            return;
        }

        final planta objetivo = plantaCercana;

        jugadorControl.iniciarRecoleccion(
                objetivo.getPosicionX(),
                () -> {

                    Recursos fibra = objetivo.recolectarRecurso();

                    if (fibra == null) {
                        return;
                    }

                    jugador.gastarEnergia(
                            objetivo.getEnergiaNecesaria()
                    );

                    jugador.getInventario().agregarRecurso(
                            fibra,
                            fibra.getCantidad()
                    );

                    System.out.println("Recolectaste fibra.");
                }
        );
    }

    // ==========================================================
    // RECOLECCIÓN DE BAYAS
    // ==========================================================

    private void comprobarRecogerBayas() {

        if (!Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            return;
        }

        if (!jugadorControl.puedeRecolectar()) {
            return;
        }

        arbustoBayas arbustoCercano = null;
        float distanciaMenor = 90f;

        for (arbustoBayas arbusto : mapa.getArbustosBayas()) {

            if (!arbusto.tieneBayas()) {
                continue;
            }

            float dx = jugador.getPosicionX()
                    - arbusto.getPosicionX();

            float dy = jugador.getPosicionY()
                    - arbusto.getPosicionY();

            float distancia = (float) Math.sqrt(
                    dx * dx + dy * dy
            );

            if (distancia <= distanciaMenor) {
                distanciaMenor = distancia;
                arbustoCercano = arbusto;
            }
        }

        if (arbustoCercano == null) {
            return;
        }

        final arbustoBayas objetivo = arbustoCercano;

        jugadorControl.iniciarRecoleccion(
                objetivo.getPosicionX(),
                () -> {

                    if (objetivo.recogerBayas()) {

                        Comida bayas =
                                new Comida("Bayas", 1, 10);

                        bayas.cargarIcono("objetos/baya.png");

                        jugador.getInventario().agregarRecurso(
                                bayas,
                                bayas.getCantidad()
                        );

                        System.out.println("Recolectaste bayas.");
                    }
                }
        );
    }

    // ==========================================================
    // RENDER PRINCIPAL
    // ==========================================================

    @Override
    public void render() {

        Proporcion.aplicar();

        float delta = Gdx.graphics.getDeltaTime();

        if (enMenu) {
            renderMenu(delta);
            return;
        }

        if (cargando) {
            renderCarga(delta);
            return;
        }

        if (!derrota && !victoria && jugador.gameOver()) {
            derrota = true;
            pantallaDerrota.mostrar();
        }

        if (derrota) {
            renderDerrota(delta);
            return;
        }

        if (!victoria
                && mapa.getEstructuraRescate()
                        .getEstructura().estaActiva()) {

            tiempoRescateActivo += delta;

            if (tiempoRescateActivo >= TIEMPO_ANTES_VICTORIA) {
                victoria = true;
                pantallaVictoria.mostrar();
            }
        }

        if (victoria) {
            renderVictoria(delta);
            return;
        }

        jugador.actualizarEnergia(delta);
        jugador.actualizarSupervivencia(delta);

        cicloDia.actualizar(delta);

        if (inventarioUI.estaAbierto()) {
            inventarioUI.actualizar();
        } else if (recetarioUI.estaAbierto()) {
            recetarioUI.actualizar();
        } else {
            inventarioUI.actualizar();
            recetarioUI.actualizar();
        }

        transicion.actualizar(delta);

        boolean enTemplo = ubicacion == Ubicacion.TEMPLO;

        actualizarRecursos(delta);

        boolean interfazAbierta =
                inventarioUI.estaAbierto()
                || recetarioUI.estaAbierto();

        if (!interfazAbierta && !transicion.isActiva()) {

            if (!enTemplo) {
                actualizarConstruccion();
            }

            boolean interactuo = comprobarInteraccionTemplo();

            if (!enTemplo) {

                GestorCriaturas.actualizar(jugador, mapa, delta);

                // Vacas y enemigos según qué tan oscuro está.
                GestorCriaturas.actualizarCiclo(
                        cicloDia.getOscuridad(),
                        jugador,
                        mapa,
                        delta
                );
            }

            if (!interactuo && !enTemplo) {
                interactuo = comprobarInteraccionEstructura();
            }

            // En modo construcción no se inicia la recolección
            // accidentalmente con la misma pulsación.
            if (!interactuo && !modoConstruccion) {
                jugadorControl.actualizar(delta, mapa);
            }

            if (!enTemplo && !interactuo && !modoConstruccion) {
                comprobarRecogerFibra();
                comprobarRecogerBayas();
            }

            jugadorControl.actualizarAtaque(
                    sistemaCombate,
                    enTemplo ? guardian : null,
                    enTemplo ? null : GestorCriaturas
            );

            if (enTemplo) {

                if (graciaGuardian > 0f) {
                    graciaGuardian -= delta;
                } else if (guardian != null) {
                    guardian.actualizar(jugador, delta);
                }

                comprobarMuerteGuardian();
            }
        }

        jugadorAnimacion.actualizar(delta);

        if (enTemplo) {

            interiorTemplo.ajustarCamara(
                    camara,
                    jugador.getPosicionX()
                            + TAMANO_JUGADOR / 2f,
                    jugador.getPosicionY()
                            + TAMANO_JUGADOR / 2f
            );

        } else {

            camara.position.set(
                    jugador.getPosicionX()
                            + TAMANO_JUGADOR / 2f,
                    jugador.getPosicionY()
                            + TAMANO_JUGADOR / 2f,
                    0
            );

            camara.update();
        }

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        if (enTemplo) {
            interiorTemplo.dibujar(camara);
        } else {
            mapa.dibujar(camara);
        }

        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        Rectangle pies = jugador.getHitbox();
        float yPies = pies.y + pies.height / 2f;

        if (enTemplo) {

            if (guardian != null) {

                float escalaGuardian =
                        TAMANO_GUARDIAN / GuardianAnimacion.CELDA;

                guardian.dibujar(batch, escalaGuardian);
                guardian.dibujarRayos(batch);
            }

        } else {

            dibujarObjetosDetras(yPies);

            mapa.dibujarEstructuras(batch);
            dibujarCartelFogata();

            mapa.getEstructuraRescate().dibujar(batch);
            templo.dibujar(batch);

            // Vista previa de la estructura que se va a colocar.
            dibujarPrevisualizacionConstruccion();
        }

        if (jugadorControl.estaHerido()) {
            dibujarJugadorHerido();
        } else if (jugadorControl.estaMinando()) {
            dibujarJugadorMinando();
        } else if (jugadorControl.estaTalando()) {
            dibujarJugadorTalando();
        } else if (jugadorControl.estaComiendo()) {
            dibujarJugadorComiendo();
        } else if (jugadorControl.estaRecolectando()) {
            dibujarJugadorRecolectando();
        } else if (jugadorControl.estaGolpeando()) {
            dibujarJugadorGolpeando();
        } else {

            TextureRegion frame = obtenerFrameActual();

            batch.draw(
                    frame,
                    jugador.getPosicionX(),
                    jugador.getPosicionY(),
                    TAMANO_JUGADOR,
                    TAMANO_JUGADOR
            );
        }

        if (enTemplo) {

            if (gemaAzul != null) {
                gemaAzul.dibujar(batch);
            }

            if (interiorTemplo.jugadorEnSalida(pies)) {
                interiorTemplo.dibujarCartelSalida(batch, font);
            }

        } else {

            dibujarObjetosDelante(yPies);

            if (templo.jugadorEnPuerta(pies)) {
                templo.dibujarCartel(batch, font);
            }
        }

        batch.end();

        // La oscuridad solo afecta al exterior. Va por encima del mundo
        // y por debajo de la interfaz, que no se oscurece.
        if (!enTemplo) {

            overlayNoche.dibujar(
                    batch,
                    camara,
                    jugador.getPosicionX() + TAMANO_JUGADOR / 2f,
                    jugador.getPosicionY() + TAMANO_JUGADOR / 2f,
                    cicloDia.getOscuridad()
            );
        }

        // Panel del modo construcción por encima del mundo.
        dibujarModoConstruccion();

        barrasSupervivencia.dibujar();
        barraRapidaUI.actualizar();

        if (!interfazAbierta) {

            barraRapidaUI.dibujar();

            if (enTemplo) {
                comprobarRecogerGema();
            } else {
                panelEstructuraRescate.dibujar(
                        jugador,
                        mapa.getEstructuraRescate()
                );
            }
        }

        if (inventarioUI.estaAbierto()) {
            inventarioUI.dibujar();
        } else if (recetarioUI.estaAbierto()) {
            recetarioUI.dibujar();
        }

        transicion.dibujar();
    }

    // ==========================================================
    // DIBUJO DE ANIMACIONES DEL JUGADOR
    // ==========================================================

    private void dibujarJugadorMinando() {

        boolean izquierda =
                jugadorControl.getDireccion()
                == JugadorControl.Direccion.IZQUIERDA;

        TextureRegion frame = jugadorAnimacion.getFrameMinar(
                izquierda,
                jugadorControl.getTiempoMinado()
        );

        dibujarFrameAccion(
                frame,
                jugadorAnimacion.getMinarFactor(),
                izquierda
                        ? JugadorAnimacion.MINAR_PIES_X_IZQUIERDA
                        : JugadorAnimacion.MINAR_PIES_X_DERECHA
        );
    }

    private void dibujarJugadorTalando() {

        boolean izquierda =
                jugadorControl.getDireccion()
                == JugadorControl.Direccion.IZQUIERDA;

        TextureRegion frame = jugadorAnimacion.getFrameTalar(
                izquierda,
                jugadorControl.getTiempoTalado()
        );

        dibujarFrameAccion(
                frame,
                jugadorAnimacion.getTalarFactor(),
                izquierda
                        ? JugadorAnimacion.MINAR_PIES_X_IZQUIERDA
                        : JugadorAnimacion.MINAR_PIES_X_DERECHA
        );
    }

    private void dibujarJugadorComiendo() {

        JugadorControl.Direccion direccion =
                jugadorControl.getDireccion();

        TextureRegion frame = jugadorAnimacion.getFrameComer(
                direccion,
                jugadorControl.getTiempoComer()
        );

        dibujarFrameAccion(
                frame,
                JugadorAnimacion.getComerFactor(),
                JugadorAnimacion.getPiesXGolpear(direccion)
        );
    }

    private void dibujarJugadorRecolectando() {

        boolean izquierda =
                jugadorControl.getDireccion()
                == JugadorControl.Direccion.IZQUIERDA;

        TextureRegion frame =
                jugadorAnimacion.getFrameRecolectar(
                        izquierda,
                        jugadorControl.getTiempoRecoleccion()
                );

        dibujarFrameAccion(
                frame,
                JugadorAnimacion.getRecolectarFactor(),
                izquierda
                        ? JugadorAnimacion.MINAR_PIES_X_IZQUIERDA
                        : JugadorAnimacion.MINAR_PIES_X_DERECHA
        );
    }

    private void dibujarJugadorGolpeando() {

        JugadorControl.Direccion direccion =
                jugadorControl.getDireccion();

        TextureRegion frame = jugadorAnimacion.getFrameGolpear(
                direccion,
                jugadorControl.getTiempoGolpe()
        );

        dibujarFrameAccion(
                frame,
                JugadorAnimacion.getGolpearFactor(),
                JugadorAnimacion.getPiesXGolpear(direccion)
        );
    }

    private void dibujarJugadorHerido() {

        JugadorControl.Direccion direccion =
                jugadorControl.getDireccion();

        TextureRegion frame = jugadorAnimacion.getFrameDano(
                direccion,
                jugadorControl.getTiempoHerido()
        );

        dibujarFrameAccion(
                frame,
                JugadorAnimacion.getGolpearFactor(),
                JugadorAnimacion.getPiesXGolpear(direccion)
        );
    }

    private void dibujarFrameAccion(
            TextureRegion frame,
            float factor,
            float piesXOriginal
    ) {

        float escalaX = TAMANO_JUGADOR / 102f;
        float escalaY = TAMANO_JUGADOR / 144f;

        float ancho = frame.getRegionWidth() * factor * escalaX;
        float alto = frame.getRegionHeight() * factor * escalaY;

        float piesX = piesXOriginal * escalaX;

        float x = jugador.getPosicionX()
                + piesX
                - ancho / 2f;

        float y = jugador.getPosicionY();

        batch.draw(frame, x, y, ancho, alto);
    }

    // ==========================================================
    // OBJETOS DEL MUNDO DETRÁS DEL JUGADOR
    // ==========================================================

    private void dibujarObjetosDetras(float yPies) {

        for (arbol a : mapa.getArboles()) {
            if (!a.estaTalado() && a.getYOrden() > yPies) {
                a.dibujar(batch);
            }
        }

        for (roca r : mapa.getRocas()) {
            if (r.getYOrden() > yPies) {
                r.dibujar(batch);
            }
        }

        for (planta p : mapa.getPlantas()) {
            if (!p.estaRecolectada() && p.getYOrden() > yPies) {
                p.dibujar(batch);
            }
        }

        for (arbustoBayas b : mapa.getArbustosBayas()) {
            if (b.getYOrden() > yPies) {
                b.dibujar(batch);
            }
        }

        GestorCriaturas.dibujarDetras(batch, yPies);
    }

    // ==========================================================
    // OBJETOS DEL MUNDO DELANTE DEL JUGADOR
    // ==========================================================

    private void dibujarObjetosDelante(float yPies) {

        for (arbol a : mapa.getArboles()) {
            if (!a.estaTalado() && a.getYOrden() <= yPies) {
                a.dibujar(batch);
            }
        }

        for (roca r : mapa.getRocas()) {
            if (r.getYOrden() <= yPies) {
                r.dibujar(batch);
            }
        }

        for (planta p : mapa.getPlantas()) {
            if (!p.estaRecolectada() && p.getYOrden() <= yPies) {
                p.dibujar(batch);
            }
        }

        for (arbustoBayas b : mapa.getArbustosBayas()) {
            if (b.getYOrden() <= yPies) {
                b.dibujar(batch);
            }
        }

        GestorCriaturas.dibujarDelante(batch, yPies);
    }

    private void dibujarCartelFogata() {

        if (mapa == null || jugador == null || fuenteInteraccion == null) {
            return;
        }

        Estructura estructura = mapa.obtenerEstructuraCercana(
                jugador.getPosicionX(),
                jugador.getPosicionY()
        );

        if (!(estructura instanceof Fogata)) {
            return;
        }

        Fogata fogata = (Fogata) estructura;

        if (fogata.estaEncendida()) {
            return;
        }

        String texto = "(E) Encender fogata (3 maderas)";

        float paddingX = 10f;
        float paddingY = 6f;

        // Medimos el texto correctamente con BitmapFont.
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout =
                new com.badlogic.gdx.graphics.g2d.GlyphLayout(
                        fuenteInteraccion, texto
                );

        float anchoCartel = layout.width + paddingX * 2f;
        float altoCartel = layout.height + paddingY * 2f;

        float cartelX = fogata.getX()
                + fogata.getAncho() / 2f
                - anchoCartel / 2f;

        float cartelY = fogata.getY()
                + fogata.getAlto()
                + 12f;

        // Dibujamos el fondo negro semitransparente.
        batch.end();

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.8f);
        shapeRenderer.rect(
                cartelX,
                cartelY,
                anchoCartel,
                altoCartel
        );
        shapeRenderer.end();

        // Dibujamos el texto por encima del fondo.
        batch.begin();

        fuenteInteraccion.setColor(Color.WHITE);
        fuenteInteraccion.draw(
                batch,
                texto,
                cartelX + paddingX,
                cartelY + altoCartel - paddingY
        );
    }

    // ==========================================================
    // TEMPLO
    // ==========================================================

    private boolean comprobarInteraccionTemplo() {

        if (!Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            return false;
        }

        Rectangle pies = jugador.getHitbox();

        if (ubicacion == Ubicacion.MUNDO
                && templo.jugadorEnPuerta(pies)) {

            entrarTemplo();
            return true;
        }

        if (ubicacion == Ubicacion.TEMPLO
                && interiorTemplo.jugadorEnSalida(pies)) {

            salirTemplo();
            return true;
        }

        return false;
    }

    private void entrarTemplo() {

        modoConstruccion = false;

        transicion.iniciar(() -> {

            ubicacion = Ubicacion.TEMPLO;
            jugador.setInterior(interiorTemplo);

            jugador.setPosicion(
                    interiorTemplo.getSpawnJugadorX()
                            - TAMANO_JUGADOR / 2f,
                    interiorTemplo.getSpawnJugadorY()
            );

            if (!guardianColocado && guardian != null) {

                guardian.setPosicion(
                        interiorTemplo.getSpawnGuardianX(),
                        interiorTemplo.getSpawnGuardianY()
                );

                guardianColocado = true;
            }
        });
    }

    private void salirTemplo() {

        transicion.iniciar(() -> {

            ubicacion = Ubicacion.MUNDO;
            jugador.setInterior(null);

            jugador.setPosicion(
                    templo.getSalidaX()
                            - TAMANO_JUGADOR / 2f,
                    templo.getSalidaY()
            );
        });
    }

    // ==========================================================
    // GEMA
    // ==========================================================

    private void comprobarRecogerGema() {

        if (gemaAzul == null || gemaAzul.estaRecogida()) {
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)
                && gemaAzul.estaCerca(
                        jugador.getPosicionX(),
                        jugador.getPosicionY())) {

            jugador.recogerItem(new GemaAzul());
            gemaAzul.recoger();

            System.out.println("Recogiste la Gema Azul.");
        }
    }

    // ==========================================================
    // ESTRUCTURA DE RESCATE
    // ==========================================================

    private boolean comprobarInteraccionEstructura() {

        if (!Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            return false;
        }

        float jugadorX = jugador.getPosicionX();
        float jugadorY = jugador.getPosicionY();

        // 1. Estructura de rescate: tiene prioridad.
        EstructuraRescateMundo rescate = mapa.getEstructuraRescate();

        if (rescate != null && rescate.estaCerca(jugadorX, jugadorY)) {
            rescate.interactuar(jugador);
            return true;
        }

        // 2. Buscamos la estructura construida más cercana.
        Estructura estructura = mapa.obtenerEstructuraCercana(
                jugadorX, jugadorY
        );

        if (estructura == null) {
            return false;
        }

        // 3. Fogata.
        if (estructura instanceof Fogata) {

            Fogata fogata = (Fogata) estructura;

            if (!fogata.estaEncendida()) {

                if (!jugador.getInventario().tieneRecurso("Madera", 3)) {
                    System.out.println(
                            "Necesitas 3 maderas para encender la fogata."
                    );
                    return true;
                }

                if (fogata.encender()) {
                    jugador.getInventario().gastarRecurso("Madera", 3);

                    System.out.println(
                            "Fogata encendida por 60 segundos."
                    );
                }
            }

            return true;
        }

        // 4. Mesa de fabricación.
        if (estructura instanceof MesaFabricacion) {
            recetarioUI.abrir();
            return true;
        }

        // 5. Cofre.
        if (estructura instanceof Cofre) {
            System.out.println("Cofre: interacción pendiente.");
            return true;
        }

        // 6. Horno.
        if (estructura instanceof Horno) {
            System.out.println("Horno: interacción pendiente.");
            return true;
        }

        // 7. Cama.
        if (estructura instanceof Cama) {
            System.out.println("Cama: interacción pendiente.");
            return true;
        }

        return false;
    }

    // ==========================================================
    // DERROTA
    // ==========================================================

    private void renderDerrota(float delta) {

        ScreenUtils.clear(0f, 0f, 0f, 1f);

        PantallaDerrota.Accion accion =
                pantallaDerrota.actualizar(delta);

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
    // VICTORIA
    // ==========================================================

    private void renderVictoria(float delta) {

        ScreenUtils.clear(0f, 0f, 0f, 1f);

        PantallaVictoria.Accion accion =
                pantallaVictoria.actualizar(delta);

        pantallaVictoria.dibujar();

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
    // FRAME ACTUAL DEL JUGADOR
    // ==========================================================

    private TextureRegion obtenerFrameActual() {

        JugadorControl.Estado estado =
                jugadorControl.getEstado();

        JugadorControl.Direccion direccion =
                jugadorControl.getDireccion();

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

                default:
                    break;
            }
        }

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

                default:
                    break;
            }
        }

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

                default:
                    break;
            }
        }

        return jugadorAnimacion.getFrame(
                jugadorAnimacion.getIdleDown()
        );
    }

    // ==========================================================
    // DISPOSE
    // ==========================================================

    @Override
    public void dispose() {

        if (batch != null) {
            batch.dispose();
        }

        if (overlayNoche != null) {
            overlayNoche.dispose();
        }

        if (pantallaDerrota != null) {
            pantallaDerrota.dispose();
        }

        if (pantallaVictoria != null) {
            pantallaVictoria.dispose();
        }

        if (pantallaCarga != null) {
            pantallaCarga.dispose();
        }

        if (menuInicio != null) {
            menuInicio.dispose();
        }

        if (jugadorAnimacion != null) {
            jugadorAnimacion.dispose();
        }

        if (guardian != null) {
            guardian.dispose();
        }

        RayoEfecto.disposeRecursos();

        if (mapa != null) {
            mapa.dispose();
        }

        if (inventarioUI != null) {
            inventarioUI.dispose();
        }

        if (recetarioUI != null) {
            recetarioUI.dispose();
        }

        if (barrasSupervivencia != null) {
            barrasSupervivencia.dispose();
        }

        if (barraRapidaUI != null) {
            barraRapidaUI.dispose();
        }

        if (gemaAzul != null) {
            gemaAzul.dispose();
        }

        if (panelEstructuraRescate != null) {
            panelEstructuraRescate.dispose();
        }

        if (templo != null) {
            templo.dispose();
        }

        if (interiorTemplo != null) {
            interiorTemplo.dispose();
        }

        if (transicion != null) {
            transicion.dispose();
        }

        if (font != null) {
            font.dispose();
        }

        if (GestorCriaturas != null) {
            GestorCriaturas.dispose();
        }

        // Texturas propias del modo construcción.

        if (texturaPixel != null) {
            texturaPixel.dispose();
        }

        if (texturaFogata != null) {
            texturaFogata.dispose();
        }

        if (texturaMesa != null) {
            texturaMesa.dispose();
        }

        if (texturaHorno != null) {
            texturaHorno.dispose();
        }

        if (texturaCofre != null) {
            texturaCofre.dispose();
        }

        if (texturaCama != null) {
            texturaCama.dispose();
        }

        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}