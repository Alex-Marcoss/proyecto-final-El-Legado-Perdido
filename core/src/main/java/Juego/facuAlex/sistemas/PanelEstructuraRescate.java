package Juego.facuAlex.sistemas;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import Juego.facuAlex.Fuentes;
import Juego.facuAlex.jugador.*;
import Juego.facuAlex.inventario.*;
import Juego.facuAlex.receta.Ingrediente;
import Juego.facuAlex.recursos.Recursos;

public class PanelEstructuraRescate {

    // =====================================================
    // AJUSTES DE DISEÑO
    // =====================================================

    // El panel se achica o agranda solo según su contenido.
    // Estos valores controlan el espaciado.
    private static final float PADDING = 14f;
    private static final float ALTURA_LINEA = 24f;
    private static final float ANCHO_MINIMO = 230f;

    // Separación entre el panel y el borde de la ventana
    // (el panel va arriba a la derecha para no tapar la estructura)
    private static final float MARGEN_PANTALLA = 20f;

    // Tamaño de las letras
    private static final int TAMANO_TITULO = 18;
    private static final int TAMANO_TEXTO = 16;

    private static final Color DORADO = new Color(0.95f, 0.78f, 0.30f, 1f);
    private static final Color GRIS = new Color(0.75f, 0.75f, 0.75f, 1f);
    private static final Color VERDE = new Color(0.45f, 0.85f, 0.45f, 1f);
    private static final Color ROJO = new Color(0.95f, 0.45f, 0.40f, 1f);

    // =====================================================
    // ATRIBUTOS
    // =====================================================

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private BitmapFont fuenteTitulo;
    private BitmapFont fuente;
    private GlyphLayout layout;

    private OrthographicCamera camaraUI;

    private final ArrayList<Linea> lineas = new ArrayList<>();

    // Una línea de texto del panel
    private static class Linea {

        String izquierda;
        String derecha;      // opcional (columna de la derecha)
        Color color;
        Color colorDerecha;
        boolean centrada;
        boolean titulo;
    }

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public PanelEstructuraRescate() {

        batch = new SpriteBatch();

        shapeRenderer = new ShapeRenderer();

        fuenteTitulo = Fuentes.texto(TAMANO_TITULO);
        fuente = Fuentes.texto(TAMANO_TEXTO);

        layout = new GlyphLayout();

        camaraUI = new OrthographicCamera(
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()
        );
    }

    // =====================================================
    // DIBUJAR
    // =====================================================

    public void dibujar(
            Jugador jugador,
            EstructuraRescateMundo estructura
    ) {

        if (jugador == null || estructura == null) {
            return;
        }

        if (!estructura.estaCerca(
                jugador.getPosicionX(),
                jugador.getPosicionY())) {

            return;
        }

        EstructuraRescate datos = estructura.getEstructura();

        // ---------- 1) Armar el contenido ----------
        lineas.clear();

        armarLineas(jugador, datos);

        // ---------- 2) Calcular el tamaño del panel ----------
        float anchoMaximo = 0f;

        for (Linea linea : lineas) {
            anchoMaximo = Math.max(anchoMaximo, medirAncho(linea));
        }

        float panelAncho = Math.max(ANCHO_MINIMO, anchoMaximo + PADDING * 2f);
        float panelAlto = PADDING * 2f + lineas.size() * ALTURA_LINEA;

        // Arriba a la derecha
        float x = Gdx.graphics.getWidth() - panelAncho - MARGEN_PANTALLA;
        float y = Gdx.graphics.getHeight() - panelAlto - MARGEN_PANTALLA;

        // ---------- 3) Cámara de UI (en píxeles de la ventana) ----------
        camaraUI.setToOrtho(
                false,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()
        );

        camaraUI.update();

        // ---------- 4) Fondo ----------
        Gdx.gl.glEnable(GL20.GL_BLEND);

        shapeRenderer.setProjectionMatrix(camaraUI.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.03f, 0.03f, 0.05f, 0.88f);
        shapeRenderer.rect(x, y, panelAncho, panelAlto);

        // Franja de color arriba
        shapeRenderer.setColor(0.3f, 0.55f, 0.9f, 1f);
        shapeRenderer.rect(x, y + panelAlto - 3f, panelAncho, 3f);

        shapeRenderer.end();

        // ---------- 5) Texto ----------
        batch.setProjectionMatrix(camaraUI.combined);

        batch.begin();

        float cursorY = y + panelAlto - PADDING;

        for (Linea linea : lineas) {

            BitmapFont f = linea.titulo ? fuenteTitulo : fuente;

            f.setColor(linea.color);

            if (linea.centrada || linea.titulo) {

                layout.setText(f, linea.izquierda);

                f.draw(
                        batch,
                        linea.izquierda,
                        x + (panelAncho - layout.width) / 2f,
                        cursorY
                );

            } else {

                f.draw(batch, linea.izquierda, x + PADDING, cursorY);

                if (linea.derecha != null) {

                    f.setColor(linea.colorDerecha);

                    layout.setText(f, linea.derecha);

                    f.draw(
                            batch,
                            linea.derecha,
                            x + panelAncho - PADDING - layout.width,
                            cursorY
                    );
                }
            }

            cursorY -= ALTURA_LINEA;
        }

        batch.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    // =====================================================
    // CONTENIDO SEGÚN EL ESTADO DE LA ESTRUCTURA
    // =====================================================

    private void armarLineas(Jugador jugador, EstructuraRescate datos) {

        inventario inventarioJugador = jugador.getInventario();

        agregarTitulo("ESTRUCTURA DE RESCATE");

        // ---------- Fases de construcción ----------
        if (!datos.estaCompletamenteConstruida()) {

            agregarTexto("Fase " + datos.getFaseActual(), GRIS);

            Ingrediente[] materiales = datos.getMaterialesFaseActual();

            for (Ingrediente material : materiales) {

                int actual = obtenerCantidadRecurso(
                        inventarioJugador,
                        material.getNombreRecurso()
                );

                Color color = actual >= material.getCantidad() ? VERDE : ROJO;

                agregarFila(
                        material.getNombreRecurso(),
                        actual + " / " + material.getCantidad(),
                        color
                );
            }

            if (datos.puedeConstruir(inventarioJugador)) {
                agregarCentrada("[ E ]  Construir", DORADO);
            } else {
                agregarCentrada("Faltan materiales", ROJO);
            }

            return;
        }

        // ---------- Estructura terminada ----------
        if (!datos.tieneGema()) {

            boolean tieneGema = inventarioJugador.tieneItem("Gema Azul");

            agregarTexto("Estructura completa", VERDE);

            agregarFila(
                    "Gema Azul",
                    tieneGema ? "lista" : "falta",
                    tieneGema ? VERDE : ROJO
            );

            if (tieneGema) {
                agregarCentrada("[ E ]  Colocar gema", DORADO);
            } else {
                agregarCentrada("Necesit\u00e1s la Gema Azul", ROJO);
            }

        } else if (!datos.estaActiva()) {

            agregarTexto("Gema Azul colocada", VERDE);

            agregarCentrada("[ E ]  Activar rescate", DORADO);

        } else {

            agregarCentrada("RESCATE ACTIVADO", VERDE);

            agregarCentrada("\u00a1Se\u00f1al enviada!", DORADO);
        }
    }

    // =====================================================
    // AYUDAS PARA ARMAR LÍNEAS
    // =====================================================

    private void agregarTitulo(String texto) {

        Linea linea = new Linea();

        linea.izquierda = texto;
        linea.color = DORADO;
        linea.titulo = true;

        lineas.add(linea);
    }

    private void agregarTexto(String texto, Color color) {

        Linea linea = new Linea();

        linea.izquierda = texto;
        linea.color = color;

        lineas.add(linea);
    }

    private void agregarCentrada(String texto, Color color) {

        Linea linea = new Linea();

        linea.izquierda = texto;
        linea.color = color;
        linea.centrada = true;

        lineas.add(linea);
    }

    private void agregarFila(String izquierda, String derecha, Color colorDerecha) {

        Linea linea = new Linea();

        linea.izquierda = izquierda;
        linea.derecha = derecha;
        linea.color = Color.WHITE;
        linea.colorDerecha = colorDerecha;

        lineas.add(linea);
    }

    // Ancho que necesita una línea
    private float medirAncho(Linea linea) {

        BitmapFont f = linea.titulo ? fuenteTitulo : fuente;

        layout.setText(f, linea.izquierda);

        float ancho = layout.width;

        if (linea.derecha != null) {

            layout.setText(f, linea.derecha);

            // 24 = espacio mínimo entre las dos columnas
            ancho += 24f + layout.width;
        }

        return ancho;
    }

    // =====================================================
    // CANTIDAD DE UN RECURSO EN EL INVENTARIO
    // =====================================================

    private int obtenerCantidadRecurso(
            inventario inventario,
            String nombre
    ) {

        int cantidadTotal = 0;

        for (int i = 0; i < inventario.getCantidadSlots(); i++) {

            if (inventario.getItem(i) == null) {
                continue;
            }

            if (inventario.getItem(i) instanceof Recursos) {

                Recursos recurso = (Recursos) inventario.getItem(i);

                if (recurso.getNombre().equals(nombre)) {

                    cantidadTotal += recurso.getCantidad();
                }
            }
        }

        return cantidadTotal;
    }

    // =====================================================
    // DISPOSE
    // =====================================================

    public void dispose() {

        batch.dispose();
        shapeRenderer.dispose();
        fuenteTitulo.dispose();
        fuente.dispose();
    }
}