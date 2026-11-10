package Juego.facuAlex.sistemas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/*
 * Mensajes en pantalla.
 *
 * Se usan desde cualquier clase, sin pasar referencias:
 *
 *     Avisos.mostrar("Fogata encendida.");        // blanco
 *     Avisos.error("Inventario lleno.");          // rojo
 *     Avisos.recurso("+2 Piedra");                // verde
 *
 * Los avisos se apilan (máximo 4), el más nuevo arriba, y se desvanecen solos.
 * Si el mismo texto ya está en pantalla, solo se renueva su tiempo.
 *
 * Principal tiene que llamar a actualizar(delta) y dibujar() en cada frame.
 */
public final class Avisos {

    private static final float ANCHO_VIRTUAL = 900f;
    private static final float ALTO_VIRTUAL = 600f;

    private static final float DURACION = 2.8f;
    private static final float DESVANECER = 0.5f;
    private static final int MAXIMO = 4;

    // Altura (desde abajo) del aviso más nuevo, y separación entre líneas
    private static final float Y_BASE = 450f;
    private static final float ALTO_LINEA = 28f;

    private static final float RELLENO_X = 10f;
    private static final float RELLENO_Y = 5f;

    public static final Color BLANCO = new Color(1f, 1f, 1f, 1f);
    public static final Color ROJO = new Color(1f, 0.45f, 0.40f, 1f);
    public static final Color VERDE = new Color(0.55f, 1f, 0.55f, 1f);

    private static class Aviso {

        final String texto;
        final Color color;
        float tiempo;

        Aviso(String texto, Color color) {
            this.texto = texto;
            this.color = color;
            this.tiempo = DURACION;
        }
    }

    private static final List<Aviso> avisos = new ArrayList<Aviso>();
    private static final GlyphLayout layout = new GlyphLayout();

    private static SpriteBatch batch;
    private static BitmapFont fuente;
    private static Texture pixel;

    private Avisos() {}

    // =====================================================
    // CREAR AVISOS
    // =====================================================

    public static void mostrar(String texto) {
        agregar(texto, BLANCO);
    }

    public static void error(String texto) {
        agregar(texto, ROJO);
    }

    public static void recurso(String texto) {
        agregar(texto, VERDE);
    }

    private static void agregar(String texto, Color color) {

        if (texto == null || texto.isEmpty()) {
            return;
        }

        // Si ya está en pantalla, solo se renueva
        for (Aviso a : avisos) {

            if (a.texto.equals(texto)) {
                a.tiempo = DURACION;
                return;
            }
        }

        avisos.add(new Aviso(texto, color));

        if (avisos.size() > MAXIMO) {
            avisos.remove(0);
        }
    }

    public static void limpiar() {
        avisos.clear();
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================

    public static void actualizar(float delta) {

        Iterator<Aviso> it = avisos.iterator();

        while (it.hasNext()) {

            Aviso a = it.next();

            a.tiempo -= delta;

            if (a.tiempo <= 0f) {
                it.remove();
            }
        }
    }

    // =====================================================
    // DIBUJAR (coordenadas virtuales 900 x 600, como las interfaces)
    // =====================================================

    public static void dibujar() {

        if (avisos.isEmpty()) {
            return;
        }

        iniciarSiHaceFalta();

        batch.getProjectionMatrix()
                .setToOrtho2D(0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        batch.begin();

        fuente.getData().setScale(1.1f);

        // El más nuevo arriba, los anteriores debajo
        int fila = 0;

        for (int i = avisos.size() - 1; i >= 0; i--, fila++) {

            Aviso a = avisos.get(i);

            float alpha = Math.min(1f, a.tiempo / DESVANECER);

            layout.setText(fuente, a.texto);

            float x = (ANCHO_VIRTUAL - layout.width) / 2f;
            float y = Y_BASE - fila * ALTO_LINEA;

            // Fondo oscuro para que se lea sobre cualquier cosa
            batch.setColor(0f, 0f, 0f, 0.6f * alpha);
            batch.draw(
                    pixel,
                    x - RELLENO_X,
                    y - layout.height - RELLENO_Y,
                    layout.width + RELLENO_X * 2f,
                    layout.height + RELLENO_Y * 2f
            );

            fuente.setColor(a.color.r, a.color.g, a.color.b, alpha);
            fuente.draw(batch, a.texto, x, y);
        }

        batch.setColor(Color.WHITE);
        fuente.setColor(Color.WHITE);

        batch.end();
    }

    private static void iniciarSiHaceFalta() {

        if (batch != null) {
            return;
        }

        batch = new SpriteBatch();
        fuente = new BitmapFont();

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        pixel = new Texture(pm);
        pm.dispose();
    }

    public static void dispose() {

        if (batch != null) {
            batch.dispose();
            fuente.dispose();
            pixel.dispose();

            batch = null;
            fuente = null;
            pixel = null;
        }

        avisos.clear();
    }
}