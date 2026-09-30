package Juego.facuAlex;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class PantallaDerrota {

    // =====================================================
    // ACCIONES
    // =====================================================

    public enum Accion {
        NINGUNA,
        JUGAR_DE_NUEVO,
        VOLVER_AL_MENU
    }

    // =====================================================
    // RESOLUCIÓN VIRTUAL
    // =====================================================

    private static final float ANCHO_VIRTUAL = 900f;
    private static final float ALTO_VIRTUAL = 600f;

    // =====================================================
    // AJUSTES
    // =====================================================

    private static final float TIEMPO_FADE = 1.2f;
    private static final float TIEMPO_BLOQUEO_CLICK = 0.6f;
    private static final float OSCURECER_FONDO = 0.35f;

    private static final float Y_TITULO = 480f;

    private static final float ANCHO_BOTON = 220f;
    private static final float ALTO_BOTON = 50f;
    private static final float ESPACIO_BOTONES = 50f;
    private static final float Y_BOTONES = 270f;

    // =====================================================
    // ATRIBUTOS
    // =====================================================

    private SpriteBatch batch;

    private Texture fondo;
    private Texture pixel;

    private BitmapFont fuenteTitulo;
    private BitmapFont fuenteTexto;

    private GlyphLayout layout;

    private Rectangle botonJugar;
    private Rectangle botonMenu;

    private float tiempo;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public PantallaDerrota() {

        batch = new SpriteBatch();

        // Fondo
        fondo = new Texture(
                Gdx.files.internal("pantallas/derrota.jpg")
        );

        fondo.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
        );

        // Pixel 1x1 para rectángulos
        Pixmap pm = new Pixmap(
                1,
                1,
                Pixmap.Format.RGBA8888
        );

        pm.setColor(1f, 1f, 1f, 1f);
        pm.fill();

        pixel = new Texture(pm);

        pm.dispose();

        // =================================================
        // FUENTES
        // =================================================

        fuenteTitulo = Fuentes.tituloAjustado(
                "GAME OVER",
                600f,
                64
        );

        fuenteTexto = Fuentes.texto(22);

        layout = new GlyphLayout();

        // =================================================
        // BOTONES
        // =================================================

        float anchoTotal =
                ANCHO_BOTON * 2 + ESPACIO_BOTONES;

        float x0 =
                (ANCHO_VIRTUAL - anchoTotal) / 2f;

        botonJugar = new Rectangle(
                x0,
                Y_BOTONES,
                ANCHO_BOTON,
                ALTO_BOTON
        );

        botonMenu = new Rectangle(
                x0 + ANCHO_BOTON + ESPACIO_BOTONES,
                Y_BOTONES,
                ANCHO_BOTON,
                ALTO_BOTON
        );

        tiempo = 0f;
    }

    // =====================================================
    // MOSTRAR
    // =====================================================

    public void mostrar() {

        tiempo = 0f;
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================

    public Accion actualizar(float delta) {

        tiempo += delta;

        if (tiempo < TIEMPO_BLOQUEO_CLICK) {

            return Accion.NINGUNA;
        }

        // ENTER → jugar nuevamente
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

            return Accion.JUGAR_DE_NUEVO;
        }

        // ESC → volver al menú
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {

            return Accion.VOLVER_AL_MENU;
        }

        // Click
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
    // MOUSE
    // =====================================================

    private float mouseX() {

        return Gdx.input.getX()
                * ANCHO_VIRTUAL
                / Gdx.graphics.getWidth();
    }

    private float mouseY() {

        return (Gdx.graphics.getHeight() - Gdx.input.getY())
                * ALTO_VIRTUAL
                / Gdx.graphics.getHeight();
    }

    // =====================================================
    // DIBUJAR
    // =====================================================

    public void dibujar() {

        batch.getProjectionMatrix().setToOrtho2D(
                0,
                0,
                ANCHO_VIRTUAL,
                ALTO_VIRTUAL
        );

        float alpha =
                Math.min(1f, tiempo / TIEMPO_FADE);

        batch.begin();

        // =================================================
        // FONDO
        // =================================================

        float escala = Math.max(
                ANCHO_VIRTUAL / fondo.getWidth(),
                ALTO_VIRTUAL / fondo.getHeight()
        );

        float anchoFondo =
                fondo.getWidth() * escala;

        float altoFondo =
                fondo.getHeight() * escala;

        batch.setColor(
                1f,
                1f,
                1f,
                alpha
        );

        batch.draw(
                fondo,
                (ANCHO_VIRTUAL - anchoFondo) / 2f,
                (ALTO_VIRTUAL - altoFondo) / 2f,
                anchoFondo,
                altoFondo
        );

        // =================================================
        // OSCURECER FONDO
        // =================================================

        batch.setColor(
                0f,
                0f,
                0f,
                OSCURECER_FONDO * alpha
        );

        batch.draw(
                pixel,
                0,
                0,
                ANCHO_VIRTUAL,
                ALTO_VIRTUAL
        );

        // =================================================
        // TÍTULO
        // =================================================

        String titulo = "GAME OVER";

        layout.setText(
                fuenteTitulo,
                titulo
        );

        float tituloX =
                (ANCHO_VIRTUAL - layout.width) / 2f;

        // Sombra
        fuenteTitulo.setColor(
                0f,
                0f,
                0f,
                alpha
        );

        fuenteTitulo.draw(
                batch,
                titulo,
                tituloX + 4f,
                Y_TITULO - 4f
        );

        // Título
        fuenteTitulo.setColor(
                0.85f,
                0.10f,
                0.10f,
                alpha
        );

        fuenteTitulo.draw(
                batch,
                titulo,
                tituloX,
                Y_TITULO
        );

        float altoTitulo = layout.height;

        // =================================================
        // SUBTÍTULO
        // =================================================

        String subtitulo =
                "Has sido derrotado";

        layout.setText(
                fuenteTexto,
                subtitulo
        );

        fuenteTexto.setColor(
                0.85f,
                0.85f,
                0.85f,
                alpha
        );

        fuenteTexto.draw(
                batch,
                subtitulo,
                (ANCHO_VIRTUAL - layout.width) / 2f,
                Y_TITULO - altoTitulo - 25f
        );

        // =================================================
        // BOTONES
        // =================================================

        float mx = mouseX();
        float my = mouseY();

        dibujarBoton(
                botonJugar,
                "Jugar de nuevo",
                botonJugar.contains(mx, my),
                alpha
        );

        dibujarBoton(
                botonMenu,
                "Volver al menu",
                botonMenu.contains(mx, my),
                alpha
        );

        batch.setColor(
                1f,
                1f,
                1f,
                1f
        );

        batch.end();
    }

    // =====================================================
    // DIBUJAR BOTÓN
    // =====================================================

    private void dibujarBoton(
            Rectangle r,
            String texto,
            boolean hover,
            float alpha) {

        float borde = 2f;

        // Borde
        batch.setColor(
                0.60f,
                0.45f,
                0.25f,
                alpha
        );

        batch.draw(
                pixel,
                r.x,
                r.y,
                r.width,
                r.height
        );

        // Fondo
        if (hover) {

            batch.setColor(
                    0.38f,
                    0.27f,
                    0.13f,
                    0.95f * alpha
            );

        } else {

            batch.setColor(
                    0.10f,
                    0.08f,
                    0.06f,
                    0.90f * alpha
            );
        }

        batch.draw(
                pixel,
                r.x + borde,
                r.y + borde,
                r.width - borde * 2f,
                r.height - borde * 2f
        );

        // Texto centrado
        layout.setText(
                fuenteTexto,
                texto
        );

        if (hover) {

            fuenteTexto.setColor(
                    1f,
                    0.90f,
                    0.60f,
                    alpha
            );

        } else {

            fuenteTexto.setColor(
                    0.90f,
                    0.90f,
                    0.90f,
                    alpha
            );
        }

        fuenteTexto.draw(
                batch,
                texto,
                r.x + (r.width - layout.width) / 2f,
                r.y + (r.height + layout.height) / 2f
        );

        batch.setColor(
                1f,
                1f,
                1f,
                1f
        );
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
    }
}