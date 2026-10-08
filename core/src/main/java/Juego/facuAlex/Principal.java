package Juego.facuAlex;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import Juego.facuAlex.Herramientas.*;
import Juego.facuAlex.Mapa.*;
import Juego.facuAlex.Pantallas.*;
import Juego.facuAlex.enemigos.guardian.*;
import Juego.facuAlex.inventario.*;
import Juego.facuAlex.jugador.*;
import Juego.facuAlex.receta.RecetarioUI;
import Juego.facuAlex.recursos.*;
import Juego.facuAlex.recursos.Comida;
import Juego.facuAlex.sistemas.*;

public class Principal extends ApplicationAdapter {

    private SpriteBatch batch;

    private OrthographicCamera camara;

    private Jugador jugador;

    private Mapa mapa;

    private JugadorAnimacion jugadorAnimacion;

    private JugadorControl jugadorControl;

    private static final float TAMANO_JUGADOR = 64f;

    private static final float TAMANO_GUARDIAN = 96f;

    private InventarioUI inventarioUI;

    private RecetarioUI recetarioUI;

    private BarrasSupervivencia barrasSupervivencia;

    private BarraRapidaUI barraRapidaUI;

    private float graciaGuardian;

    // ==========================================================
    // TEMPLO
    // ==========================================================

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


    // ==========================================================
    // PANTALLAS
    // ==========================================================

    private MenuInicio menuInicio;

    private PantallaCarga pantallaCarga;

    private PantallaDerrota pantallaDerrota;

    private PantallaVictoria pantallaVictoria;


    // ==========================================================
    // ESTADO DEL JUEGO
    // ==========================================================

    private boolean enMenu;

    private boolean cargando;

    private boolean derrota;

    private boolean victoria;


    // ==========================================================
    // VICTORIA
    // ==========================================================

    private static final float TIEMPO_ANTES_VICTORIA = 4f;

    private float tiempoRescateActivo;


    // ==========================================================
    // CARGA
    // ==========================================================

    private static final int PASOS_CARGA = 5;

    private static final float TIEMPO_MINIMO_CARGA = 0.4f;

    private int pasoCarga;

    private float tiempoCarga;


    // ==========================================================
    // GUARDIAN
    // ==========================================================

    private Guardian guardian;

    private combate sistemaCombate;


    // ==========================================================
    // GEMA
    // ==========================================================

    private GemaMundo gemaAzul;


    // ==========================================================
    // ESTRUCTURA DE RESCATE
    // ==========================================================

    private PanelEstructuraRescate panelEstructuraRescate;


    // ==========================================================
    // CREATE
    // ==========================================================

    @Override
    public void create() {

        batch = new SpriteBatch();

        gemaAzul = null;

        camara = new OrthographicCamera();

        camara.setToOrtho(false, 900, 600);

        pantallaDerrota = new PantallaDerrota();

        pantallaVictoria = new PantallaVictoria();

        pantallaCarga = new PantallaCarga();

        menuInicio = new MenuInicio();

        transicion = new TransicionFade();

        font = new BitmapFont();

        ubicacion = Ubicacion.MUNDO;

        enMenu = true;

        cargando = false;

        derrota = false;

        victoria = false;
    }


    // ==========================================================
    // CAMBIO DE TAMAÑO DE VENTANA
    // ==========================================================

    @Override
    public void resize(int width, int height) {

        /*
         * Mantiene la proporcion original de 900x600
         * aunque la ventana se agrande o achique.
         */
        Proporcion.actualizar(width, height);
    }


    // ==========================================================
    // COMENZAR CARGA
    // ==========================================================

    private void comenzarCarga() {

        enMenu = false;

        derrota = false;

        victoria = false;

        ubicacion = Ubicacion.MUNDO;

        guardianColocado = false;

        tiempoRescateActivo = 0f;

        cargando = true;

        pasoCarga = 0;

        tiempoCarga = 0f;

        pantallaCarga.mostrar();

        pantallaCarga.setProgreso(
                0.05f,
                "Preparando..."
        );
    }


    // ==========================================================
    // RENDER CARGA
    // ==========================================================

    private void renderCarga(float delta) {

        ScreenUtils.clear(
                0f,
                0f,
                0f,
                1f
        );

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


    // ==========================================================
    // PASOS DE CARGA
    // ==========================================================

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

                pantallaCarga.setProgreso(
                        0.20f,
                        "Generando el mundo..."
                );

                break;


            case 1:

                mapa = new Mapa(
                        900,
                        600
                );

                pantallaCarga.setProgreso(
                        0.50f,
                        "Creando al jugador..."
                );

                break;


            case 2:

                jugador = new Jugador("Facu");

                barrasSupervivencia =
                        new BarrasSupervivencia(jugador);

                barraRapidaUI =
                        new BarraRapidaUI(jugador);


                Recursos madera =
                        new Recursos("Madera", 10);

                madera.cargarIcono(
                        "objetos/madera.png"
                );

                jugador.getInventario().agregarRecurso(
                        madera,
                        10
                );


                Recursos piedra =
                        new Recursos("Piedra", 10);

                piedra.cargarIcono(
                        "objetos/piedra.png"
                );

                jugador.getInventario().agregarRecurso(
                        piedra,
                        10
                );


                float[] spawn =
                        mapa.buscarPosicionLibre(
                                3072,
                                3072
                        );

                jugador.setPosicion(
                        spawn[0],
                        spawn[1]
                );


                sistemaCombate =
                        new combate();


                guardian =
                        new Guardian();

                guardian.setPosicion(
                        jugador.getPosicionX() + 100,
                        jugador.getPosicionY()
                );


                templo =
                        new Templo(
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

                jugadorAnimacion =
                        new JugadorAnimacion();

                jugadorControl =
                        new JugadorControl(jugador);

                inventarioUI =
                        new InventarioUI(jugador);

                recetarioUI =
                        new RecetarioUI(jugador);

                panelEstructuraRescate =
                        new PanelEstructuraRescate();


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

                pantallaCarga.setProgreso(
                        1f,
                        "Listo"
                );

                break;
        }
    }


    // ==========================================================
    // VOLVER AL MENU
    // ==========================================================

    private void volverAlMenu() {

        derrota = false;

        victoria = false;

        cargando = false;

        enMenu = true;

        menuInicio.mostrar();
    }


    // ==========================================================
    // MENU
    // ==========================================================

    private void renderMenu(float delta) {

        ScreenUtils.clear(
                0f,
                0f,
                0f,
                1f
        );

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


    // ==========================================================
    // MUERTE DEL GUARDIAN
    // ==========================================================

    private void comprobarMuerteGuardian() {

        if (guardian == null) {

            return;
        }

        if (!guardian.estaVivo()
                && gemaAzul == null) {

            gemaAzul =
                    new GemaMundo(
                            guardian.getPosicionX(),
                            guardian.getPosicionY()
                    );

            System.out.println(
                    "El Guardian dejo caer la Gema Azul."
            );
        }
    }


    // ==========================================================
    // ACTUALIZAR RECURSOS
    // ==========================================================

    private void actualizarRecursos(float delta) {

        for (arbustoBayas arbusto :
                mapa.getArbustosBayas()) {

            arbusto.actualizar(delta);
        }
    }


    // ==========================================================
    // INTERACCION CON PLANTAS
    // ==========================================================

    private void comprobarRecogerFibra() {

        if (!Gdx.input.isKeyJustPressed(
                Input.Keys.E)) {

            return;
        }


        planta plantaCercana = null;

        float distanciaMenor = 90f;


        for (planta plantaActual :
                mapa.getPlantas()) {

            float dx =
                    jugador.getPosicionX()
                            - plantaActual.getPosicionX();

            float dy =
                    jugador.getPosicionY()
                            - plantaActual.getPosicionY();

            float distancia =
                    (float) Math.sqrt(
                            dx * dx
                                    + dy * dy
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


        Recursos fibra =
                plantaCercana.recolectarRecurso();


        if (fibra == null) {

            return;
        }


        jugador.gastarEnergia(
                plantaCercana.getEnergiaNecesaria()
        );


        jugador.getInventario().agregarRecurso(
                fibra,
                fibra.getCantidad()
        );


        System.out.println(
                "Recolectaste fibra."
        );
    }


    // ==========================================================
    // INTERACCION CON BAYAS
    // ==========================================================

    private void comprobarRecogerBayas() {

        if (!Gdx.input.isKeyJustPressed(
                Input.Keys.E)) {

            return;
        }


        arbustoBayas arbustoCercano = null;

        float distanciaMenor = 90f;


        for (arbustoBayas arbusto :
                mapa.getArbustosBayas()) {

            if (!arbusto.tieneBayas()) {

                continue;
            }


            float dx =
                    jugador.getPosicionX()
                            - arbusto.getPosicionX();

            float dy =
                    jugador.getPosicionY()
                            - arbusto.getPosicionY();


            float distancia =
                    (float) Math.sqrt(
                            dx * dx
                                    + dy * dy
                    );


            if (distancia <= distanciaMenor) {

                distanciaMenor = distancia;

                arbustoCercano = arbusto;
            }
        }


        if (arbustoCercano == null) {

            return;
        }


        if (arbustoCercano.recogerBayas()) {

            Comida bayas =
                    new Comida(
                            "Bayas",
                            1,
                            10
                    );

            bayas.cargarIcono(
                    "objetos/baya.png"
            );

            jugador.getInventario().agregarRecurso(
                    bayas,
                    bayas.getCantidad()
            );

            System.out.println(
                    "Recolectaste bayas."
            );
        }
    }


    // ==========================================================
    // RENDER
    // ==========================================================

    @Override
    public void render() {

        /*
         * Mantiene la proporcion 900x600 y agrega
         * barras negras si la ventana tiene otra proporcion.
         */
        Proporcion.aplicar();

        float delta =
                Gdx.graphics.getDeltaTime();


        // =====================================================
        // MENU
        // =====================================================

        if (enMenu) {

            renderMenu(delta);

            return;
        }


        // =====================================================
        // CARGA
        // =====================================================

        if (cargando) {

            renderCarga(delta);

            return;
        }


        // =====================================================
        // DERROTA
        // =====================================================

        if (!derrota
                && !victoria
                && jugador.gameOver()) {

            derrota = true;

            pantallaDerrota.mostrar();
        }


        if (derrota) {

            renderDerrota(delta);

            return;
        }


        // =====================================================
        // VICTORIA
        // =====================================================

        if (!victoria
                && mapa
                .getEstructuraRescate()
                .getEstructura()
                .estaActiva()) {

            tiempoRescateActivo += delta;


            if (tiempoRescateActivo
                    >= TIEMPO_ANTES_VICTORIA) {

                victoria = true;

                pantallaVictoria.mostrar();
            }
        }


        if (victoria) {

            renderVictoria(delta);

            return;
        }


        // =====================================================
        // JUEGO NORMAL
        // =====================================================

        jugador.actualizarEnergia(delta);

        jugador.actualizarSupervivencia(delta);


        // =====================================================
        // INVENTARIO Y CRAFTING
        // =====================================================

        if (inventarioUI.estaAbierto()) {

            inventarioUI.actualizar();

        } else if (recetarioUI.estaAbierto()) {

            recetarioUI.actualizar();

        } else {

            inventarioUI.actualizar();

            recetarioUI.actualizar();
        }


        // =====================================================
        // TRANSICION
        // =====================================================

        transicion.actualizar(delta);


        boolean enTemplo =
                (ubicacion == Ubicacion.TEMPLO);


        // =====================================================
        // RECURSOS
        // =====================================================

        actualizarRecursos(delta);


        // =====================================================
        // JUGADOR
        // =====================================================

        boolean interfazAbierta =
                inventarioUI.estaAbierto()
                        || recetarioUI.estaAbierto();


        if (!interfazAbierta
                && !transicion.isActiva()) {

            // =================================================
            // PUERTA DEL TEMPLO
            // =================================================

            boolean interactuo =
                    comprobarInteraccionTemplo();


            // =================================================
            // ESTRUCTURA DE RESCATE
            // =================================================

            if (!interactuo && !enTemplo) {

                interactuo =
                        comprobarInteraccionEstructura();
            }


            // =================================================
            // MOVIMIENTO
            // =================================================

            if (!interactuo) {

                jugadorControl.actualizar(
                        delta,
                        mapa
                );
            }


            // =================================================
            // RECOLECCION
            // =================================================

            if (!enTemplo) {

                comprobarRecogerFibra();

                comprobarRecogerBayas();
            }


            // =================================================
            // COMBATE
            // =================================================

            if (enTemplo) {

                jugadorControl.actualizarAtaque(
                        sistemaCombate,
                        guardian
                );


                if (graciaGuardian > 0f) {

                    graciaGuardian -= delta;

                } else if (guardian != null) {

                    guardian.actualizar(
                            jugador,
                            delta
                    );
                }


                comprobarMuerteGuardian();
            }
        }


        // =====================================================
        // ANIMACION DEL JUGADOR
        // =====================================================

        jugadorAnimacion.actualizar(delta);


        // =====================================================
        // CAMARA
        // =====================================================

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


        // =====================================================
        // LIMPIAR PANTALLA
        // =====================================================

        ScreenUtils.clear(
                0.15f,
                0.15f,
                0.2f,
                1f
        );


        // =====================================================
        // MAPA
        // =====================================================

        if (enTemplo) {

            interiorTemplo.dibujar(camara);

        } else {

            mapa.dibujar(camara);
        }


        batch.setProjectionMatrix(
                camara.combined
        );

        batch.begin();


        // =====================================================
        // ORDEN DE DIBUJO
        // =====================================================

        Rectangle pies =
                jugador.getHitbox();

        float yPies =
                pies.y
                        + pies.height / 2f;


        if (enTemplo) {

            // =================================================
            // GUARDIAN
            // =================================================

            if (guardian != null) {

                float escalaGuardian =
                        TAMANO_GUARDIAN
                                / GuardianAnimacion.CELDA;

                guardian.dibujar(
                        batch,
                        escalaGuardian
                );

                guardian.dibujarRayos(batch);
            }

        } else {

            // =================================================
            // OBJETOS DETRAS
            // =================================================

            dibujarObjetosDetras(yPies);


            // =================================================
            // ESTRUCTURA DE RESCATE
            // =================================================

            mapa.getEstructuraRescate()
                    .dibujar(batch);


            // =================================================
            // TEMPLO
            // =================================================

            templo.dibujar(batch);
        }


        // =====================================================
        // JUGADOR
        // =====================================================

        if (jugadorControl.estaHerido()) {

            dibujarJugadorHerido();

        } else if (jugadorControl.estaMinando()) {

            dibujarJugadorMinando();

        } else if (jugadorControl.estaTalando()) {

            dibujarJugadorTalando();

        } else if (jugadorControl.estaGolpeando()) {

            dibujarJugadorGolpeando();

        } else {

            TextureRegion frame =
                    obtenerFrameActual();

            batch.draw(
                    frame,
                    jugador.getPosicionX(),
                    jugador.getPosicionY(),
                    TAMANO_JUGADOR,
                    TAMANO_JUGADOR
            );
        }


        if (enTemplo) {

            // =================================================
            // GEMA
            // =================================================

            if (gemaAzul != null) {

                gemaAzul.dibujar(batch);
            }


            // =================================================
            // CARTEL SALIDA
            // =================================================

            if (interiorTemplo.jugadorEnSalida(pies)) {

                interiorTemplo.dibujarCartelSalida(
                        batch,
                        font
                );
            }

        } else {

            // =================================================
            // OBJETOS DELANTE
            // =================================================

            dibujarObjetosDelante(yPies);


            // =================================================
            // CARTEL TEMPLO
            // =================================================

            if (templo.jugadorEnPuerta(pies)) {

                templo.dibujarCartel(
                        batch,
                        font
                );
            }
        }


        batch.end();


        // =====================================================
        // HUD
        // =====================================================

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


        // =====================================================
        // INVENTARIO / CRAFTING
        // =====================================================

        if (inventarioUI.estaAbierto()) {

            inventarioUI.dibujar();

        } else if (recetarioUI.estaAbierto()) {

            recetarioUI.dibujar();
        }


        // =====================================================
        // TRANSICION
        // =====================================================

        transicion.dibujar();
    }


    // ==========================================================
    // JUGADOR MINANDO
    // ==========================================================

    private void dibujarJugadorMinando() {

        boolean haciaLaIzquierda =
                jugadorControl.getDireccion()
                        == JugadorControl.Direccion.IZQUIERDA;

        TextureRegion frame =
                jugadorAnimacion.getFrameMinar(
                        haciaLaIzquierda,
                        jugadorControl.getTiempoMinado()
                );

        float escalaX =
                TAMANO_JUGADOR / 102f;

        float escalaY =
                TAMANO_JUGADOR / 144f;

        float factor =
                jugadorAnimacion.getMinarFactor();

        float ancho =
                frame.getRegionWidth()
                        * factor
                        * escalaX;

        float alto =
                frame.getRegionHeight()
                        * factor
                        * escalaY;

        float piesX =
                (haciaLaIzquierda
                        ? JugadorAnimacion.MINAR_PIES_X_IZQUIERDA
                        : JugadorAnimacion.MINAR_PIES_X_DERECHA)
                        * escalaX;

        float x =
                jugador.getPosicionX()
                        + piesX
                        - ancho / 2f;

        float y =
                jugador.getPosicionY();

        batch.draw(
                frame,
                x,
                y,
                ancho,
                alto
        );
    }


    // ==========================================================
    // JUGADOR TALANDO
    // ==========================================================

    private void dibujarJugadorTalando() {

        boolean haciaLaIzquierda =
                jugadorControl.getDireccion()
                        == JugadorControl.Direccion.IZQUIERDA;

        TextureRegion frame =
                jugadorAnimacion.getFrameTalar(
                        haciaLaIzquierda,
                        jugadorControl.getTiempoTalado()
                );

        float escalaX =
                TAMANO_JUGADOR / 102f;

        float escalaY =
                TAMANO_JUGADOR / 144f;

        float factor =
                jugadorAnimacion.getTalarFactor();

        float ancho =
                frame.getRegionWidth()
                        * factor
                        * escalaX;

        float alto =
                frame.getRegionHeight()
                        * factor
                        * escalaY;

        float piesX =
                (haciaLaIzquierda
                        ? JugadorAnimacion.MINAR_PIES_X_IZQUIERDA
                        : JugadorAnimacion.MINAR_PIES_X_DERECHA)
                        * escalaX;

        float x =
                jugador.getPosicionX()
                        + piesX
                        - ancho / 2f;

        float y =
                jugador.getPosicionY();

        batch.draw(
                frame,
                x,
                y,
                ancho,
                alto
        );
    }


    // ==========================================================
    // JUGADOR GOLPEANDO
    // ==========================================================

    private void dibujarJugadorGolpeando() {

        JugadorControl.Direccion direccion =
                jugadorControl.getDireccion();

        TextureRegion frame =
                jugadorAnimacion.getFrameGolpear(
                        direccion,
                        jugadorControl.getTiempoGolpe()
                );

        float escalaX =
                TAMANO_JUGADOR / 102f;

        float escalaY =
                TAMANO_JUGADOR / 144f;

        float factor =
                JugadorAnimacion.getGolpearFactor();

        float ancho =
                frame.getRegionWidth()
                        * factor
                        * escalaX;

        float alto =
                frame.getRegionHeight()
                        * factor
                        * escalaY;

        float piesX =
                JugadorAnimacion.getPiesXGolpear(
                        direccion
                ) * escalaX;

        float x =
                jugador.getPosicionX()
                        + piesX
                        - ancho / 2f;

        float y =
                jugador.getPosicionY();

        batch.draw(
                frame,
                x,
                y,
                ancho,
                alto
        );
    }


    // ==========================================================
    // JUGADOR HERIDO
    // ==========================================================

    private void dibujarJugadorHerido() {

        JugadorControl.Direccion direccion =
                jugadorControl.getDireccion();

        TextureRegion frame =
                jugadorAnimacion.getFrameDano(
                        direccion,
                        jugadorControl.getTiempoHerido()
                );

        float escalaX =
                TAMANO_JUGADOR / 102f;

        float escalaY =
                TAMANO_JUGADOR / 144f;

        float factor =
                JugadorAnimacion.getGolpearFactor();

        float ancho =
                frame.getRegionWidth()
                        * factor
                        * escalaX;

        float alto =
                frame.getRegionHeight()
                        * factor
                        * escalaY;

        float piesX =
                JugadorAnimacion.getPiesXGolpear(
                        direccion
                ) * escalaX;

        float x =
                jugador.getPosicionX()
                        + piesX
                        - ancho / 2f;

        float y =
                jugador.getPosicionY();

        batch.draw(
                frame,
                x,
                y,
                ancho,
                alto
        );
    }


    // ==========================================================
    // OBJETOS DEL MUNDO DETRAS DEL JUGADOR
    // ==========================================================

    private void dibujarObjetosDetras(float yPies) {

        for (arbol a :
                mapa.getArboles()) {

            if (!a.estaTalado()
                    && a.getYOrden() > yPies) {

                a.dibujar(batch);
            }
        }


        for (roca r :
                mapa.getRocas()) {

            if (r.getYOrden() > yPies) {

                r.dibujar(batch);
            }
        }


        for (planta p :
                mapa.getPlantas()) {

            if (!p.estaRecolectada()
                    && p.getYOrden() > yPies) {

                p.dibujar(batch);
            }
        }


        for (arbustoBayas b :
                mapa.getArbustosBayas()) {

            if (b.getYOrden() > yPies) {

                b.dibujar(batch);
            }
        }
    }


    // ==========================================================
    // OBJETOS DEL MUNDO DELANTE DEL JUGADOR
    // ==========================================================

    private void dibujarObjetosDelante(float yPies) {

        for (arbol a :
                mapa.getArboles()) {

            if (!a.estaTalado()
                    && a.getYOrden() <= yPies) {

                a.dibujar(batch);
            }
        }


        for (roca r :
                mapa.getRocas()) {

            if (r.getYOrden() <= yPies) {

                r.dibujar(batch);
            }
        }


        for (planta p :
                mapa.getPlantas()) {

            if (!p.estaRecolectada()
                    && p.getYOrden() <= yPies) {

                p.dibujar(batch);
            }
        }


        for (arbustoBayas b :
                mapa.getArbustosBayas()) {

            if (b.getYOrden() <= yPies) {

                b.dibujar(batch);
            }
        }
    }


    // ==========================================================
    // TEMPLO: ENTRAR Y SALIR
    // ==========================================================

    private boolean comprobarInteraccionTemplo() {

        if (!Gdx.input.isKeyJustPressed(
                Input.Keys.E)) {

            return false;
        }


        Rectangle pies =
                jugador.getHitbox();


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

        transicion.iniciar(() -> {

            ubicacion = Ubicacion.TEMPLO;

            jugador.setInterior(interiorTemplo);


            jugador.setPosicion(
                    interiorTemplo.getSpawnJugadorX()
                            - TAMANO_JUGADOR / 2f,

                    interiorTemplo.getSpawnJugadorY()
            );


            if (!guardianColocado
                    && guardian != null) {

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

        if (gemaAzul == null) {

            return;
        }


        if (gemaAzul.estaRecogida()) {

            return;
        }


        if (Gdx.input.isKeyJustPressed(
                Input.Keys.E)) {

            if (gemaAzul.estaCerca(
                    jugador.getPosicionX(),
                    jugador.getPosicionY())) {

                jugador.recogerItem(
                        new GemaAzul()
                );

                gemaAzul.recoger();

                System.out.println(
                        "Recogiste la Gema Azul."
                );
            }
        }
    }


    // ==========================================================
    // INTERACCION ESTRUCTURA
    // ==========================================================

    private boolean comprobarInteraccionEstructura() {

        if (!Gdx.input.isKeyJustPressed(
                Input.Keys.E)) {

            return false;
        }


        EstructuraRescateMundo estructura =
                mapa.getEstructuraRescate();


        if (estructura == null) {

            return false;
        }


        if (estructura.estaCerca(
                jugador.getPosicionX(),
                jugador.getPosicionY())) {

            estructura.interactuar(jugador);

            return true;
        }


        return false;
    }


    // ==========================================================
    // DERROTA
    // ==========================================================

    private void renderDerrota(float delta) {

        ScreenUtils.clear(
                0f,
                0f,
                0f,
                1f
        );


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

        ScreenUtils.clear(
                0f,
                0f,
                0f,
                1f
        );


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
    // FRAME DEL JUGADOR
    // ==========================================================

    private TextureRegion obtenerFrameActual() {

        JugadorControl.Estado estado =
                jugadorControl.getEstado();

        JugadorControl.Direccion direccion =
                jugadorControl.getDireccion();


        // =====================================================
        // IDLE
        // =====================================================

        if (estado ==
                JugadorControl.Estado.IDLE) {

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


        // =====================================================
        // CAMINAR
        // =====================================================

        if (estado ==
                JugadorControl.Estado.CAMINAR) {

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


        // =====================================================
        // CORRER
        // =====================================================

        if (estado ==
                JugadorControl.Estado.CORRER) {

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
    }
}
