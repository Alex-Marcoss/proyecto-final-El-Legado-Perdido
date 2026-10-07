package Juego.facuAlex;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

import Juego.facuAlex.Herramientas.*;

import Juego.facuAlex.Mapa.*;

import Juego.facuAlex.Pantallas.*;

import Juego.facuAlex.enemigos.guardian.*;

import Juego.facuAlex.inventario.*;

import Juego.facuAlex.jugador.*;

import Juego.facuAlex.recursos.*;

import Juego.facuAlex.sistemas.*;


public class Principal extends ApplicationAdapter {

private SpriteBatch batch;

private OrthographicCamera camara;

private Jugador jugador;

private Mapa mapa;

private JugadorAnimacion jugadorAnimacion;

private GuardianAnimacion guardianAnimacion;

private JugadorControl jugadorControl;

private static final float TAMANO_JUGADOR = 64f;

private static final float TAMANO_GUARDIAN = 96f;

private InventarioUI inventarioUI;

private BarrasSupervivencia barrasSupervivencia;

private BarraRapidaUI barraRapidaUI;

// Pantallas

private MenuInicio menuInicio;

private PantallaCarga pantallaCarga;

private PantallaDerrota pantallaDerrota;

private PantallaVictoria pantallaVictoria;

// Estado del juego

private boolean enMenu;

private boolean cargando;

private boolean derrota;

private boolean victoria;

// Tiempo antes de mostrar victoria

private static final float TIEMPO_ANTES_VICTORIA = 4f;

private float tiempoRescateActivo;

// Carga por pasos

private static final int PASOS_CARGA = 5;

private static final float TIEMPO_MINIMO_CARGA = 0.4f;

private int pasoCarga;

private float tiempoCarga;

private Guardian guardian;

private combate sistemaCombate;

private GemaMundo gemaAzul;

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

    enMenu = true;

    cargando = false;

    derrota = false;

    victoria = false;
}

// ==========================================================
// COMENZAR CARGA
// ==========================================================

private void comenzarCarga() {

    enMenu = false;

    derrota = false;

    victoria = false;

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

            if (guardianAnimacion != null) {
                guardianAnimacion.dispose();
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

            if (panelEstructuraRescate != null) {
                panelEstructuraRescate.dispose();
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

            hacha hachaInicial =
                    new hacha(50, 10);

            jugador.getInventario().agregarItem(
                hachaInicial
            );

            pico picoInicial =
                    new pico(50, 15);

            jugador.getInventario().agregarItem(
                picoInicial
            );

            // Posición inicial

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

            pantallaCarga.setProgreso(
                0.75f,
                "Preparando la interfaz..."
            );

            break;

        case 3:

            jugadorAnimacion =
                    new JugadorAnimacion();

            guardianAnimacion =
                    new GuardianAnimacion();

            jugadorControl =
                    new JugadorControl(jugador);

            inventarioUI =
                    new InventarioUI(jugador);

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
// VOLVER AL MENÚ
// ==========================================================

private void volverAlMenu() {

    derrota = false;

    victoria = false;

    cargando = false;

    enMenu = true;

    menuInicio.mostrar();
}

// ==========================================================
// MENÚ
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

        gemaAzul = new GemaMundo(
            guardian.getPosicionX(),
            guardian.getPosicionY()
        );

        System.out.println(
            "El Guardian dejo caer la Gema Azul."
        );
    }
}

// ==========================================================
// RENDER
// ==========================================================

@Override
public void render() {

    float delta =
            Gdx.graphics.getDeltaTime();

    // =====================================================
    // MENÚ
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

    inventarioUI.actualizar();

    if (!inventarioUI.estaAbierto()) {

        // (no se interactua con la estructura en medio de un golpe de pico)
        boolean interactuoConEstructura =
                !jugadorControl.estaMinando()
                && !jugadorControl.estaTalando()
                && !jugadorControl.estaGolpeando()
                && !jugadorControl.estaHerido()
                && comprobarInteraccionEstructura();

        if (!interactuoConEstructura) {

            jugadorControl.actualizar(
                delta,
                mapa
            );
        }

        // =================================================
        // ATAQUE DEL JUGADOR
        // =================================================

        jugadorControl.actualizarAtaque(
            delta,
            sistemaCombate,
            guardian
        );

        // =================================================
        // GUARDIAN
        // =================================================

        if (guardian != null
                && guardian.estaVivo()) {

            guardian.actualizar(
                jugador,
                delta
            );
        }

        comprobarMuerteGuardian();
    }

    // =====================================================
    // ACTUALIZAR ANIMACIONES
    // =====================================================

    jugadorAnimacion.actualizar(delta);

    if (guardianAnimacion != null) {

        guardianAnimacion.actualizar(delta);
    }

    // =====================================================
    // CÁMARA
    // =====================================================

    camara.position.set(
        jugador.getPosicionX()
            + TAMANO_JUGADOR / 2f,

        jugador.getPosicionY()
            + TAMANO_JUGADOR / 2f,

        0
    );

    camara.update();

    ScreenUtils.clear(
        0.15f,
        0.15f,
        0.2f,
        1f
    );

    // =====================================================
    // MAPA
    // =====================================================

    mapa.dibujar(camara);

    batch.setProjectionMatrix(
        camara.combined
    );

    batch.begin();

    // =====================================================
    // ORDEN DE DIBUJO
    // =====================================================

    float yPies =
            jugador.getHitbox().y
            + jugador.getHitbox().height / 2f;

    // =====================================================
    // ÁRBOLES Y ROCAS DETRÁS
    // =====================================================

    for (arbol arbol : mapa.getArboles()) {

        if (!arbol.estaTalado()
                && arbol.getYOrden() > yPies) {

            arbol.dibujar(batch);
        }
    }

    for (roca roca : mapa.getRocas()) {

        if (roca.getYOrden() > yPies) {

            roca.dibujar(batch);
        }
    }

    // =====================================================
    // ESTRUCTURA
    // =====================================================

    mapa.getEstructuraRescate()
            .dibujar(batch);

    // =====================================================
    // GUARDIAN
    // =====================================================

    if (guardian != null
            && guardian.estaVivo()) {

        TextureRegion frameGuardian =
                obtenerFrameGuardian();

        batch.draw(
            frameGuardian,

            guardian.getPosicionX()
                - (TAMANO_GUARDIAN
                    - TAMANO_JUGADOR) / 2f,

            guardian.getPosicionY()
                - (TAMANO_GUARDIAN
                    - TAMANO_JUGADOR) / 2f,

            TAMANO_GUARDIAN,
            TAMANO_GUARDIAN
        );
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

    // =====================================================
    // GEMA
    // =====================================================

    if (gemaAzul != null) {

        gemaAzul.dibujar(batch);
    }

    // =====================================================
    // ÁRBOLES Y ROCAS DELANTE
    // =====================================================

    for (arbol arbol : mapa.getArboles()) {

        if (!arbol.estaTalado()
                && arbol.getYOrden() <= yPies) {

            arbol.dibujar(batch);
        }
    }

    for (roca roca : mapa.getRocas()) {

        if (roca.getYOrden() <= yPies) {

            roca.dibujar(batch);
        }
    }

    batch.end();

    // =====================================================
    // HUD
    // =====================================================

    barrasSupervivencia.dibujar();

    barraRapidaUI.actualizar();

    if (!inventarioUI.estaAbierto()) {

        barraRapidaUI.dibujar();

        comprobarRecogerGema();

        panelEstructuraRescate.dibujar(
            jugador,
            mapa.getEstructuraRescate()
        );
    }

    inventarioUI.dibujar();
}

// ==========================================================
// FRAME DEL GUARDIAN
// ==========================================================

private TextureRegion obtenerFrameGuardian() {

    Guardian.EstadoAtaque estado =
            guardian.getEstadoAtaque();

    Guardian.Direccion direccion =
            guardian.getDireccion();

    // =====================================================
    // PREPARANDO
    // =====================================================

    if (estado ==
            Guardian.EstadoAtaque.PREPARANDO) {

        switch (direccion) {

            case ARRIBA:

                return guardianAnimacion.getFrame(
                    guardianAnimacion.getPrepararUp()
                );

            case ABAJO:

                return guardianAnimacion.getFrame(
                    guardianAnimacion.getPrepararDown()
                );

            case IZQUIERDA:

                return guardianAnimacion.getFrame(
                    guardianAnimacion.getPrepararLeft()
                );

            case DERECHA:

                return guardianAnimacion.getFrame(
                    guardianAnimacion.getPrepararRight()
                );
        }
    }

    // =====================================================
    // ATACANDO
    // =====================================================

    if (estado ==
            Guardian.EstadoAtaque.ATACANDO) {

        switch (direccion) {

            case ARRIBA:

                return guardianAnimacion.getFrame(
                    guardianAnimacion.getAtacarUp()
                );

            case ABAJO:

                return guardianAnimacion.getFrame(
                    guardianAnimacion.getAtacarDown()
                );

            case IZQUIERDA:

                return guardianAnimacion.getFrame(
                    guardianAnimacion.getAtacarLeft()
                );

            case DERECHA:

                return guardianAnimacion.getFrame(
                    guardianAnimacion.getAtacarRight()
                );
        }
    }

    // =====================================================
    // NORMAL
    // =====================================================

    /*
     * Si está persiguiendo al jugador,
     * usamos caminar.
     *
     * Si no está atacando y está cerca,
     * usamos idle.
     */

    float diferenciaX =
            jugador.getPosicionX()
            - guardian.getPosicionX();

    float diferenciaY =
            jugador.getPosicionY()
            - guardian.getPosicionY();

    float distancia =
            (float) Math.sqrt(
                diferenciaX * diferenciaX +
                diferenciaY * diferenciaY
            );

    boolean estaCaminando =
            distancia > guardian.getDistanciaAtaque()
            && distancia <= guardian.getDistanciaDeteccion();

    if (estaCaminando) {

        switch (direccion) {

            case ARRIBA:

                return guardianAnimacion.getFrame(
                    guardianAnimacion.getWalkUp()
                );

            case ABAJO:

                return guardianAnimacion.getFrame(
                    guardianAnimacion.getWalkDown()
                );

            case IZQUIERDA:

                return guardianAnimacion.getFrame(
                    guardianAnimacion.getWalkLeft()
                );

            case DERECHA:

                return guardianAnimacion.getFrame(
                    guardianAnimacion.getWalkRight()
                );
        }
    }

    // =====================================================
    // IDLE
    // =====================================================

    switch (direccion) {

        case ARRIBA:

            return guardianAnimacion.getFrame(
                guardianAnimacion.getIdleUp()
            );

        case ABAJO:

            return guardianAnimacion.getFrame(
                guardianAnimacion.getIdleDown()
            );

        case IZQUIERDA:

            return guardianAnimacion.getFrame(
                guardianAnimacion.getIdleLeft()
            );

        case DERECHA:

            return guardianAnimacion.getFrame(
                guardianAnimacion.getIdleRight()
            );
    }

    return guardianAnimacion.getFrame(
        guardianAnimacion.getIdleDown()
    );
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
// INTERACCIÓN ESTRUCTURA
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
// JUGADOR MINANDO
// ==========================================================

// Los frames de caminar miden 102 x 144 px y se dibujan en
// TAMANO_JUGADOR x TAMANO_JUGADOR. Los frames de minar son mas
// grandes (entra el pico), asi que se dibujan con LA MISMA escala
// para que el personaje se vea del mismo tamano que al caminar.
private void dibujarJugadorMinando() {

    boolean haciaLaIzquierda =
            jugadorControl.getDireccion()
                == JugadorControl.Direccion.IZQUIERDA;

    TextureRegion frame =
            jugadorAnimacion.getFrameMinar(
                haciaLaIzquierda,
                jugadorControl.getTiempoMinado()
            );

    float escalaX = TAMANO_JUGADOR / 102f;
    float escalaY = TAMANO_JUGADOR / 144f;

    // El factor corrige el tamano si la imagen tiene otra resolucion
    float factor = jugadorAnimacion.getMinarFactor();

    float ancho = frame.getRegionWidth() * factor * escalaX;
    float alto = frame.getRegionHeight() * factor * escalaY;

    // Donde estan los pies del jugador (en unidades del juego)
    float piesX = (haciaLaIzquierda
            ? JugadorAnimacion.MINAR_PIES_X_IZQUIERDA
            : JugadorAnimacion.MINAR_PIES_X_DERECHA) * escalaX;

    // Los pies estan en el centro horizontal de la celda de minar
    float x = jugador.getPosicionX() + piesX - ancho / 2f;
    float y = jugador.getPosicionY();

    batch.draw(frame, x, y, ancho, alto);
}

// ==========================================================
// JUGADOR TALANDO
// ==========================================================

// Igual que minar: se dibuja con la misma escala que el sprite
// de caminar, con los pies en el mismo punto, para que el
// personaje no "salte" ni cambie de tamano al talar.
private void dibujarJugadorTalando() {

    boolean haciaLaIzquierda =
            jugadorControl.getDireccion()
                == JugadorControl.Direccion.IZQUIERDA;

    TextureRegion frame =
            jugadorAnimacion.getFrameTalar(
                haciaLaIzquierda,
                jugadorControl.getTiempoTalado()
            );

    float escalaX = TAMANO_JUGADOR / 102f;
    float escalaY = TAMANO_JUGADOR / 144f;

    float factor = jugadorAnimacion.getTalarFactor();

    float ancho = frame.getRegionWidth() * factor * escalaX;
    float alto = frame.getRegionHeight() * factor * escalaY;

    float piesX = (haciaLaIzquierda
            ? JugadorAnimacion.MINAR_PIES_X_IZQUIERDA
            : JugadorAnimacion.MINAR_PIES_X_DERECHA) * escalaX;

    // Los pies estan en el centro horizontal de la celda
    float x = jugador.getPosicionX() + piesX - ancho / 2f;
    float y = jugador.getPosicionY();

    batch.draw(frame, x, y, ancho, alto);
}

// ==========================================================
// JUGADOR GOLPEANDO (puno)
// ==========================================================

// Misma escala y mismos pies que el sprite de caminar, asi el
// personaje no cambia de tamano ni salta de lugar al pegar.
private void dibujarJugadorGolpeando() {

    JugadorControl.Direccion direccion =
            jugadorControl.getDireccion();

    TextureRegion frame =
            jugadorAnimacion.getFrameGolpear(
                direccion,
                jugadorControl.getTiempoGolpe()
            );

    float escalaX = TAMANO_JUGADOR / 102f;
    float escalaY = TAMANO_JUGADOR / 144f;

    float factor = JugadorAnimacion.getGolpearFactor();

    float ancho = frame.getRegionWidth() * factor * escalaX;
    float alto = frame.getRegionHeight() * factor * escalaY;

    float piesX =
            JugadorAnimacion.getPiesXGolpear(direccion) * escalaX;

    // Los pies estan en el centro horizontal de la celda
    float x = jugador.getPosicionX() + piesX - ancho / 2f;
    float y = jugador.getPosicionY();

    batch.draw(frame, x, y, ancho, alto);
}

// ==========================================================
// JUGADOR HERIDO (recibe dano)
// ==========================================================

// Misma escala y mismos pies que el sprite de caminar.
private void dibujarJugadorHerido() {

    JugadorControl.Direccion direccion =
            jugadorControl.getDireccion();

    TextureRegion frame =
            jugadorAnimacion.getFrameDano(
                direccion,
                jugadorControl.getTiempoHerido()
            );

    float escalaX = TAMANO_JUGADOR / 102f;
    float escalaY = TAMANO_JUGADOR / 144f;

    float factor = JugadorAnimacion.getGolpearFactor();

    float ancho = frame.getRegionWidth() * factor * escalaX;
    float alto = frame.getRegionHeight() * factor * escalaY;

    float piesX =
            JugadorAnimacion.getPiesXGolpear(direccion) * escalaX;

    float x = jugador.getPosicionX() + piesX - ancho / 2f;
    float y = jugador.getPosicionY();

    batch.draw(frame, x, y, ancho, alto);
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

    batch.dispose();

    pantallaDerrota.dispose();

    pantallaVictoria.dispose();

    pantallaCarga.dispose();

    menuInicio.dispose();

    if (jugadorAnimacion != null) {

        jugadorAnimacion.dispose();
    }

    if (guardianAnimacion != null) {

        guardianAnimacion.dispose();
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

    if (gemaAzul != null) {

        gemaAzul.dispose();
    }

    if (panelEstructuraRescate != null) {

        panelEstructuraRescate.dispose();
    }
}


}
