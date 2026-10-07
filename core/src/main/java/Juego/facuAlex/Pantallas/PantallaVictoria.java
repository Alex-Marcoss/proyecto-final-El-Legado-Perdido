package Juego.facuAlex.Pantallas;


import Juego.facuAlex.Proporcion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import Juego.facuAlex.Fuentes;

public class PantallaVictoria {

    // Lo que el jugador eligió en esta pantalla
    public enum Accion {
        NINGUNA,
        JUGAR_DE_NUEVO,
        VOLVER_AL_MENU
    }

    // =====================================================
    // RESOLUCIÓN VIRTUAL (la misma que el resto del juego)
    // =====================================================

    private static final float ANCHO_VIRTUAL = 900f;
    private static final float ALTO_VIRTUAL = 600f;

    // =====================================================
    // TEXTOS
    // =====================================================

    // Podés cambiarlo por "GANASTE"
    private static final String TITULO = "VICTORIA";

    private static final String SUBTITULO_1 = "\u00a1Tu se\u00f1al fue recibida:";
    private static final String SUBTITULO_2 = "has sido rescatado!";

    // =====================================================
    // AJUSTES
    // =====================================================

    private static final float TIEMPO_FADE = 1.5f;
    private static final float TIEMPO_BLOQUEO_CLICK = 0.8f;

    // Oscurecido general y oscurecido extra del lado izquierdo
    // (ahí va el texto; el rayo y el helicóptero quedan sin tapar)
    private static final float OSCURECER_GENERAL = 0.12f;
    private static final float OSCURECER_IZQUIERDA = 0.65f;

    // Columna de la izquierda
    private static final float X_COLUMNA = 50f;
    private static final float Y_TITULO = 545f;

    private static final float ANCHO_BOTON = 250f;
    private static final float ALTO_BOTON = 50f;
    private static final float Y_BOTON_JUGAR = 150f;
    private static final float Y_BOTON_MENU = 85f;

    // =====================================================
    // ATRIBUTOS
    // =====================================================

    private SpriteBatch batch;

    private Texture fondo;
    private Texture pixel;
    private Texture degradado; // oscurecido de izquierda a derecha

    private BitmapFont fuenteTitulo;
    private BitmapFont fuenteSubtitulo;
    private BitmapFont fuenteTexto;
    private GlyphLayout layout;

    private Rectangle botonJugar;
    private Rectangle botonMenu;

    private float tiempo;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public PantallaVictoria() {

        batch = new SpriteBatch();

        fondo = new Texture(Gdx.files.internal("pantallas/victoria.jpg"));
        fondo.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Pixel blanco para rectángulos
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1f, 1f, 1f, 1f);
        pm.fill();
        pixel = new Texture(pm);
        pm.dispose();

        // Degradado: negro opaco a la izquierda, transparente hacia la derecha
        int ancho = 256;

        Pixmap grad = new Pixmap(ancho, 1, Pixmap.Format.RGBA8888);

        for (int i = 0; i < ancho; i++) {

            // Llega a transparente al 70% del ancho
            float a = 1f - (i / (ancho * 0.70f));

            if (a < 0f) {
                a = 0f;
            }

            grad.setColor(0f, 0f, 0f, a);
            grad.drawPixel(i, 0);
        }

        degradado = new Texture(grad);
        degradado.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        grad.dispose();

        fuenteTitulo = Fuentes.titulo(56);
        fuenteSubtitulo = Fuentes.texto(24);
        fuenteTexto = Fuentes.texto(24);

        layout = new GlyphLayout();

        botonJugar = new Rectangle(X_COLUMNA, Y_BOTON_JUGAR, ANCHO_BOTON, ALTO_BOTON);
        botonMenu = new Rectangle(X_COLUMNA, Y_BOTON_MENU, ANCHO_BOTON, ALTO_BOTON);

        tiempo = 0f;
    }

    // =====================================================
    // MOSTRAR (reinicia el fundido)
    // =====================================================

    public void mostrar() {
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            return Accion.JUGAR_DE_NUEVO;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            return Accion.VOLVER_AL_MENU;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

            float mx = mouseX();
            float my = mouseY();

            if (botonJugar.contains(mx, my)) {
                return Accion.JUGAR_DE_NUEVO;
            }

            if (botonMenu.contains(mx, my)) {
                return Accion.VOLVER_AL_MENU;
            }
        }

        return Accion.NINGUNA;
    }

    // =====================================================
    // MOUSE (a coordenadas virtuales)
    // =====================================================

    private float mouseX() {
        return Proporcion.mouseX();
    }

    private float mouseY() {
        return Proporcion.mouseY();
    }

    // =====================================================
    // DIBUJAR
    // =====================================================

    public void dibujar() {

        batch.getProjectionMatrix().setToOrtho2D(0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        float alpha = Math.min(1f, tiempo / TIEMPO_FADE);

        batch.begin();

        // Fondo (llena la pantalla manteniendo la proporción)
        float escala = Math.max(
                ANCHO_VIRTUAL / fondo.getWidth(),
                ALTO_VIRTUAL / fondo.getHeight()
        );

        float anchoFondo = fondo.getWidth() * escala;
        float altoFondo = fondo.getHeight() * escala;

        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(
                fondo,
                (ANCHO_VIRTUAL - anchoFondo) / 2f,
                (ALTO_VIRTUAL - altoFondo) / 2f,
                anchoFondo,
                altoFondo
        );

        // Oscurecido general suave
        batch.setColor(0f, 0f, 0f, OSCURECER_GENERAL * alpha);
        batch.draw(pixel, 0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        // Oscurecido extra de izquierda a derecha (para leer el texto)
        batch.setColor(1f, 1f, 1f, OSCURECER_IZQUIERDA * alpha);
        batch.draw(degradado, 0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        // Título con sombra
        layout.setText(fuenteTitulo, TITULO);

        float altoTitulo = layout.height;

        fuenteTitulo.setColor(0f, 0f, 0f, alpha);
        fuenteTitulo.draw(batch, TITULO, X_COLUMNA + 3f, Y_TITULO - 3f);

        fuenteTitulo.setColor(0.95f, 0.78f, 0.30f, alpha);
        fuenteTitulo.draw(batch, TITULO, X_COLUMNA, Y_TITULO);

        // Subtítulo (2 líneas)
        float ySub = Y_TITULO - altoTitulo - 30f;

        fuenteSubtitulo.setColor(0f, 0f, 0f, alpha);
        fuenteSubtitulo.draw(batch, SUBTITULO_1, X_COLUMNA + 2f, ySub - 2f);
        fuenteSubtitulo.draw(batch, SUBTITULO_2, X_COLUMNA + 2f, ySub - 34f);

        fuenteSubtitulo.setColor(0.92f, 0.92f, 0.92f, alpha);
        fuenteSubtitulo.draw(batch, SUBTITULO_1, X_COLUMNA, ySub);
        fuenteSubtitulo.draw(batch, SUBTITULO_2, X_COLUMNA, ySub - 32f);

        // Botones
        float mx = mouseX();
        float my = mouseY();

        dibujarBoton(botonJugar, "Jugar de nuevo", botonJugar.contains(mx, my), alpha);
        dibujarBoton(botonMenu, "Volver al men\u00fa", botonMenu.contains(mx, my), alpha);

        batch.setColor(1f, 1f, 1f, 1f);

        batch.end();
    }

    // =====================================================
    // BOTÓN
    // =====================================================

    private void dibujarBoton(Rectangle r, String texto, boolean hover, float alpha) {

        float borde = 2f;

        batch.setColor(0.60f, 0.45f, 0.25f, alpha);
        batch.draw(pixel, r.x, r.y, r.width, r.height);

        if (hover) {
            batch.setColor(0.38f, 0.27f, 0.13f, 0.95f * alpha);
        } else {
            batch.setColor(0.10f, 0.08f, 0.06f, 0.90f * alpha);
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
            fuenteTexto.setColor(1f, 0.90f, 0.60f, alpha);
        } else {
            fuenteTexto.setColor(0.90f, 0.90f, 0.90f, alpha);
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
        degradado.dispose();
        fuenteTitulo.dispose();
        fuenteSubtitulo.dispose();
        fuenteTexto.dispose();
    }
}
