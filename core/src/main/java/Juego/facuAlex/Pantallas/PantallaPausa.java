package Juego.facuAlex.Pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import Juego.facuAlex.Fuentes;
import Juego.facuAlex.Proporcion;

/*
 * Menú de pausa. Se dibuja por encima del juego congelado.
 *
 * ESC o el botón "Continuar" → seguir jugando
 * Botón "Volver al menú"     → salir al menú principal
 */
public class PantallaPausa {

    public enum Accion {
        NINGUNA,
        CONTINUAR,
        VOLVER_AL_MENU
    }

    private static final float ANCHO_VIRTUAL = 900f;
    private static final float ALTO_VIRTUAL = 600f;

    private static final float OSCURECER_FONDO = 0.45f;
    private static final float TIEMPO_BLOQUEO_CLICK = 0.15f;

    // Paleta del menú de inicio
    private static final float[] COLOR_DORADO = {0.95f, 0.78f, 0.30f};
    private static final float[] COLOR_BORDE = {0.60f, 0.45f, 0.25f};

    private static final float ANCHO_PANEL = 440f;
    private static final float ALTO_PANEL = 330f;
    private static final float Y_PANEL = 135f;

    private static final float Y_TITULO = 420f;

    private static final float ANCHO_BOTON = 300f;
    private static final float ALTO_BOTON = 52f;
    private static final float Y_BOTON_CONTINUAR = 290f;
    private static final float Y_BOTON_MENU = 220f;

    private final SpriteBatch batch;
    private final Texture pixel;

    private final BitmapFont fuenteTitulo;
    private final BitmapFont fuenteTexto;
    private final BitmapFont fuenteAyuda;

    private final GlyphLayout layout = new GlyphLayout();

    private final Rectangle botonContinuar;
    private final Rectangle botonMenu;

    private float tiempo;

    public PantallaPausa() {

        batch = new SpriteBatch();

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1f, 1f, 1f, 1f);
        pm.fill();
        pixel = new Texture(pm);
        pm.dispose();

        fuenteTitulo = Fuentes.titulo(56);
        fuenteTexto = Fuentes.texto(26);
        fuenteAyuda = Fuentes.texto(20);

        float x = (ANCHO_VIRTUAL - ANCHO_BOTON) / 2f;

        botonContinuar = new Rectangle(
                x, Y_BOTON_CONTINUAR, ANCHO_BOTON, ALTO_BOTON);

        botonMenu = new Rectangle(
                x, Y_BOTON_MENU, ANCHO_BOTON, ALTO_BOTON);
    }

    public void mostrar() {
        tiempo = 0f;
    }

    // Usa el delta real: el del juego vale 0 mientras está en pausa
    public Accion actualizar(float delta) {

        tiempo += delta;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            return Accion.CONTINUAR;
        }

        if (tiempo >= TIEMPO_BLOQUEO_CLICK
                && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

            float mx = Proporcion.mouseX();
            float my = Proporcion.mouseY();

            if (botonContinuar.contains(mx, my)) {
                return Accion.CONTINUAR;
            }

            if (botonMenu.contains(mx, my)) {
                return Accion.VOLVER_AL_MENU;
            }
        }

        return Accion.NINGUNA;
    }

    public void dibujar() {

        batch.getProjectionMatrix()
                .setToOrtho2D(0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        batch.begin();

        // Oscurece el juego congelado
        batch.setColor(0f, 0f, 0f, OSCURECER_FONDO);
        batch.draw(pixel, 0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);
        batch.setColor(1f, 1f, 1f, 1f);

        // Cartel de madera oscura detrás del menú (como el de inicio)
        dibujarPanel(
                (ANCHO_VIRTUAL - ANCHO_PANEL) / 2f,
                Y_PANEL,
                ANCHO_PANEL,
                ALTO_PANEL
        );

        // Título en dorado, con sombra
        String titulo = "PAUSA";
        layout.setText(fuenteTitulo, titulo);
        float tituloX = (ANCHO_VIRTUAL - layout.width) / 2f;

        fuenteTitulo.setColor(0f, 0f, 0f, 1f);
        fuenteTitulo.draw(batch, titulo, tituloX + 3f, Y_TITULO - 3f);

        fuenteTitulo.setColor(
                COLOR_DORADO[0], COLOR_DORADO[1], COLOR_DORADO[2], 1f);
        fuenteTitulo.draw(batch, titulo, tituloX, Y_TITULO);

        // Botones
        float mx = Proporcion.mouseX();
        float my = Proporcion.mouseY();

        dibujarBoton(botonContinuar, "Continuar",
                botonContinuar.contains(mx, my));
        dibujarBoton(botonMenu, "Volver al menú",
                botonMenu.contains(mx, my));

        // Ayuda
        String ayuda = "ESC para continuar";
        layout.setText(fuenteAyuda, ayuda);

        fuenteAyuda.setColor(0.90f, 0.90f, 0.90f, 1f);
        fuenteAyuda.draw(
                batch,
                ayuda,
                (ANCHO_VIRTUAL - layout.width) / 2f,
                Y_PANEL + 30f
        );

        fuenteAyuda.setColor(1f, 1f, 1f, 1f);

        batch.end();
    }

    // Cartel de madera oscura con borde marrón (igual que MenuInicio)
    private void dibujarPanel(float x, float y, float ancho, float alto) {

        float borde = 3f;

        batch.setColor(
                COLOR_BORDE[0], COLOR_BORDE[1], COLOR_BORDE[2], 1f);
        batch.draw(pixel, x, y, ancho, alto);

        batch.setColor(0.10f, 0.08f, 0.06f, 0.95f);
        batch.draw(
                pixel,
                x + borde,
                y + borde,
                ancho - borde * 2f,
                alto - borde * 2f
        );

        batch.setColor(1f, 1f, 1f, 1f);
    }

    // Mismo botón que el menú de inicio
    private void dibujarBoton(Rectangle r, String texto, boolean hover) {

        float borde = 2f;

        batch.setColor(
                COLOR_BORDE[0], COLOR_BORDE[1], COLOR_BORDE[2], 1f);
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

    public void dispose() {

        batch.dispose();
        pixel.dispose();
        fuenteTitulo.dispose();
        fuenteTexto.dispose();
        fuenteAyuda.dispose();
    }
}
