package Juego.facuAlex;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class MenuInicio {

    // Lo que el jugador eligió en el menú
    public enum Accion {
        NINGUNA,
        INICIAR,
        SALIR
    }

    private enum Vista {
        PRINCIPAL,
        CONTROLES
    }

    // =====================================================
    // RESOLUCIÓN VIRTUAL (la misma que el resto del juego)
    // =====================================================

    private static final float ANCHO_VIRTUAL = 900f;
    private static final float ALTO_VIRTUAL = 600f;

    // =====================================================
    // TEXTOS
    // =====================================================

    private static final String TITULO = "EL LEGADO PERDIDO";

    // Lista de controles: { "acción", "tecla" }
    private static final String[][] CONTROLES = {
            { "Moverse", "W  A  S  D" },
            { "Correr", "SHIFT" },
            { "Interactuar (talar / minar)", "E" },
            { "Abrir / cerrar inventario", "I" },
            { "Elegir slot de la barra", "1 a 9 / RUEDA" },
            { "Mover objetos", "ARRASTRAR CON CLICK" },
            { "Equipar herramienta", "ELEGIRLA EN LA BARRA" }
    };

    // =====================================================
    // AJUSTES DE DISEÑO
    // =====================================================

    // Segundos durante los que se ignoran los clicks al mostrar el menú
    private static final float TIEMPO_BLOQUEO_CLICK = 0.3f;

    private static final float OSCURECER_FONDO = 0.25f;

    // Cartel del título
    private static final float ANCHO_CARTEL = 640f;
    private static final float ALTO_CARTEL = 100f;
    private static final float Y_CARTEL = 450f;

    // Botones del menú principal
    private static final float ANCHO_BOTON = 300f;
    private static final float ALTO_BOTON = 52f;
    private static final float ESPACIO_BOTONES = 18f;
    private static final float Y_PRIMER_BOTON = 330f; // el de más arriba

    // Panel de controles
    private static final float ANCHO_PANEL = 640f;
    private static final float ALTO_PANEL = 460f;
    private static final float Y_PANEL = 70f;

    // =====================================================
    // ATRIBUTOS
    // =====================================================

    private SpriteBatch batch;

    private Texture fondo;
    private Texture pixel; // 1x1 blanco para dibujar rectángulos

    private BitmapFont fuenteTitulo;
    private BitmapFont fuenteTexto;
    private BitmapFont fuenteChica;
    private GlyphLayout layout;

    private Rectangle botonIniciar;
    private Rectangle botonControles;
    private Rectangle botonSalir;
    private Rectangle botonVolver;

    private Vista vista;

    private float tiempo;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MenuInicio() {

        batch = new SpriteBatch();

        fondo = new Texture(Gdx.files.internal("pantallas/Inicio.jpg"));
        fondo.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1f, 1f, 1f, 1f);
        pm.fill();
        pixel = new Texture(pm);
        pm.dispose();

        // Fuentes nítidas (TTF): el título se achica solo si no entra en el cartel
        fuenteTitulo = Fuentes.tituloAjustado(TITULO, ANCHO_CARTEL - 60f, 52);
        fuenteTexto = Fuentes.texto(26);
        fuenteChica = Fuentes.texto(20);

        layout = new GlyphLayout();

        // Botones apilados y centrados
        float x = (ANCHO_VIRTUAL - ANCHO_BOTON) / 2f;
        float paso = ALTO_BOTON + ESPACIO_BOTONES;

        botonIniciar = new Rectangle(x, Y_PRIMER_BOTON, ANCHO_BOTON, ALTO_BOTON);
        botonControles = new Rectangle(x, Y_PRIMER_BOTON - paso, ANCHO_BOTON, ALTO_BOTON);
        botonSalir = new Rectangle(x, Y_PRIMER_BOTON - paso * 2f, ANCHO_BOTON, ALTO_BOTON);

        // Botón "Volver" del panel de controles
        botonVolver = new Rectangle(
                (ANCHO_VIRTUAL - ANCHO_BOTON) / 2f,
                Y_PANEL + 25f,
                ANCHO_BOTON,
                ALTO_BOTON
        );

        mostrar();
    }

    // =====================================================
    // MOSTRAR (vuelve al menú principal)
    // =====================================================

    public void mostrar() {
        vista = Vista.PRINCIPAL;
        tiempo = 0f;
    }

    // =====================================================
    // ACTUALIZAR: devuelve la opción elegida
    // =====================================================

    public Accion actualizar(float delta) {

        tiempo += delta;

        if (tiempo < TIEMPO_BLOQUEO_CLICK) {
            return Accion.NINGUNA;
        }

        boolean click = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);

        float mx = mouseX();
        float my = mouseY();

        // ---------- PANEL DE CONTROLES ----------
        if (vista == Vista.CONTROLES) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
                    || (click && botonVolver.contains(mx, my))) {

                vista = Vista.PRINCIPAL;
            }

            return Accion.NINGUNA;
        }

        // ---------- MENÚ PRINCIPAL ----------
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            return Accion.INICIAR;
        }

        if (click) {

            if (botonIniciar.contains(mx, my)) {
                return Accion.INICIAR;
            }

            if (botonControles.contains(mx, my)) {
                vista = Vista.CONTROLES;
            }

            if (botonSalir.contains(mx, my)) {
                return Accion.SALIR;
            }
        }

        return Accion.NINGUNA;
    }

    // =====================================================
    // MOUSE (a coordenadas virtuales)
    // =====================================================

    private float mouseX() {
        return Gdx.input.getX() * ANCHO_VIRTUAL / Gdx.graphics.getWidth();
    }

    private float mouseY() {
        return (Gdx.graphics.getHeight() - Gdx.input.getY())
                * ALTO_VIRTUAL / Gdx.graphics.getHeight();
    }

    // =====================================================
    // DIBUJAR
    // =====================================================

    public void dibujar() {

        batch.getProjectionMatrix().setToOrtho2D(0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);

        // Fondo: llena toda la pantalla manteniendo la proporción
        float escala = Math.max(
                ANCHO_VIRTUAL / fondo.getWidth(),
                ALTO_VIRTUAL / fondo.getHeight()
        );

        float anchoFondo = fondo.getWidth() * escala;
        float altoFondo = fondo.getHeight() * escala;

        batch.draw(
                fondo,
                (ANCHO_VIRTUAL - anchoFondo) / 2f,
                (ALTO_VIRTUAL - altoFondo) / 2f,
                anchoFondo,
                altoFondo
        );

        // Oscurecer un poco para que el texto se lea bien
        batch.setColor(0f, 0f, 0f, OSCURECER_FONDO);
        batch.draw(pixel, 0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);
        batch.setColor(1f, 1f, 1f, 1f);

        if (vista == Vista.PRINCIPAL) {
            dibujarMenuPrincipal();
        } else {
            dibujarControles();
        }

        batch.setColor(1f, 1f, 1f, 1f);

        batch.end();
    }

    // =====================================================
    // MENÚ PRINCIPAL
    // =====================================================

    private void dibujarMenuPrincipal() {

        // Cartel con el título
        float cartelX = (ANCHO_VIRTUAL - ANCHO_CARTEL) / 2f;

        dibujarPanel(cartelX, Y_CARTEL, ANCHO_CARTEL, ALTO_CARTEL, 0.92f);

        layout.setText(fuenteTitulo, TITULO);

        float tituloX = (ANCHO_VIRTUAL - layout.width) / 2f;
        float tituloY = Y_CARTEL + (ALTO_CARTEL + layout.height) / 2f;

        fuenteTitulo.setColor(0f, 0f, 0f, 1f);
        fuenteTitulo.draw(batch, TITULO, tituloX + 3f, tituloY - 3f);

        fuenteTitulo.setColor(0.95f, 0.78f, 0.30f, 1f);
        fuenteTitulo.draw(batch, TITULO, tituloX, tituloY);

        // Botones
        float mx = mouseX();
        float my = mouseY();

        dibujarBoton(botonIniciar, "Iniciar partida", botonIniciar.contains(mx, my));
        dibujarBoton(botonControles, "Ver controles", botonControles.contains(mx, my));
        dibujarBoton(botonSalir, "Salir al escritorio", botonSalir.contains(mx, my));
    }

    // =====================================================
    // PANEL DE CONTROLES
    // =====================================================

    private void dibujarControles() {

        float panelX = (ANCHO_VIRTUAL - ANCHO_PANEL) / 2f;

        dibujarPanel(panelX, Y_PANEL, ANCHO_PANEL, ALTO_PANEL, 0.93f);

        // Título del panel
        String titulo = "CONTROLES";

        layout.setText(fuenteTexto, titulo);

        fuenteTexto.setColor(0.95f, 0.78f, 0.30f, 1f);
        fuenteTexto.draw(
                batch,
                titulo,
                (ANCHO_VIRTUAL - layout.width) / 2f,
                Y_PANEL + ALTO_PANEL - 30f
        );

        // Lista: acción a la izquierda, tecla a la derecha
        float y = Y_PANEL + ALTO_PANEL - 90f;

        for (int i = 0; i < CONTROLES.length; i++) {

            String accion = CONTROLES[i][0];
            String tecla = CONTROLES[i][1];

            fuenteChica.setColor(0.90f, 0.90f, 0.90f, 1f);
            fuenteChica.draw(batch, accion, panelX + 40f, y);

            layout.setText(fuenteChica, tecla);

            fuenteChica.setColor(0.95f, 0.78f, 0.30f, 1f);
            fuenteChica.draw(
                    batch,
                    tecla,
                    panelX + ANCHO_PANEL - 40f - layout.width,
                    y
            );

            y -= 38f;
        }

        // Botón volver
        dibujarBoton(botonVolver, "Volver", botonVolver.contains(mouseX(), mouseY()));
    }

    // =====================================================
    // PANEL (cartel de madera oscura con borde)
    // =====================================================

    private void dibujarPanel(float x, float y, float ancho, float alto, float alpha) {

        float borde = 3f;

        batch.setColor(0.60f, 0.45f, 0.25f, 1f);
        batch.draw(pixel, x, y, ancho, alto);

        batch.setColor(0.10f, 0.08f, 0.06f, alpha);
        batch.draw(
                pixel,
                x + borde,
                y + borde,
                ancho - borde * 2f,
                alto - borde * 2f
        );

        batch.setColor(1f, 1f, 1f, 1f);
    }

    // =====================================================
    // BOTÓN
    // =====================================================

    private void dibujarBoton(Rectangle r, String texto, boolean hover) {

        float borde = 2f;

        batch.setColor(0.60f, 0.45f, 0.25f, 1f);
        batch.draw(pixel, r.x, r.y, r.width, r.height);

        if (hover) {
            batch.setColor(0.38f, 0.27f, 0.13f, 0.95f);
        } else {
            batch.setColor(0.10f, 0.08f, 0.06f, 0.90f);
        }

        batch.draw(
                pixel,
                r.x + borde,
                r.y + borde,
                r.width - borde * 2f,
                r.height - borde * 2f
        );

        layout.setText(fuenteTexto, texto);

        if (hover) {
            fuenteTexto.setColor(1f, 0.90f, 0.60f, 1f);
        } else {
            fuenteTexto.setColor(0.90f, 0.90f, 0.90f, 1f);
        }

        fuenteTexto.draw(
                batch,
                texto,
                r.x + (r.width - layout.width) / 2f,
                r.y + (r.height + layout.height) / 2f
        );

        batch.setColor(1f, 1f, 1f, 1f);
    }

    // =====================================================
    // DISPOSE
    // =====================================================

    public void dispose() {

        batch.dispose();
        fondo.dispose();
        pixel.dispose();
        fuenteTitulo.dispose();
        fuenteTexto.dispose();
        fuenteChica.dispose();
    }
}