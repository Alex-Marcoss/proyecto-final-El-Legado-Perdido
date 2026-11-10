package Juego.facuAlex.sistemas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/*
 * Números flotantes (daño, curación...) que suben y se desvanecen.
 *
 *     TextosFlotantes.agregar(x, y, "-15", TextosFlotantes.ROJO);
 *
 * x e y son coordenadas del MUNDO (donde nace el número).
 * Principal tiene que llamar a actualizar(delta) y dibujar(...) en cada frame.
 */
public final class TextosFlotantes {

    public static final Color ROJO = new Color(1f, 0.22f, 0.22f, 1f);
    public static final Color AZUL = new Color(0.35f, 0.65f, 1f, 1f);
    public static final Color BLANCO = new Color(1f, 1f, 1f, 1f);
    public static final Color VERDE = new Color(0.45f, 1f, 0.45f, 1f);

    private static final float DURACION = 0.9f;   // segundos en pantalla
    private static final float SUBIDA = 38f;      // cuánto sube en total
    private static final float ESCALA = 1.4f;
    private static final int MAXIMO = 40;

    private static class Texto {

        final String texto;
        final float x;
        final float y;
        final Color color;
        float tiempo;

        Texto(String texto, float x, float y, Color color) {
            this.texto = texto;
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    private static final List<Texto> textos = new ArrayList<Texto>();
    private static final GlyphLayout layout = new GlyphLayout();
    private static final Random azar = new Random();

    private TextosFlotantes() {}

    public static void agregar(float x, float y, String texto, Color color) {

        if (textos.size() >= MAXIMO) {
            textos.remove(0);
        }

        // Pequeño corrimiento al azar para que golpes seguidos no se tapen
        float corrimiento = (azar.nextFloat() - 0.5f) * 20f;

        textos.add(new Texto(texto, x + corrimiento, y, new Color(color)));
    }

    public static void limpiar() {
        textos.clear();
    }

    public static void actualizar(float delta) {

        Iterator<Texto> it = textos.iterator();

        while (it.hasNext()) {

            Texto t = it.next();

            t.tiempo += delta;

            if (t.tiempo >= DURACION) {
                it.remove();
            }
        }
    }

    // Se dibuja con la cámara del mundo
    public static void dibujar(SpriteBatch batch, OrthographicCamera camara,
                               BitmapFont fuente) {

        if (textos.isEmpty()) {
            return;
        }

        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        fuente.getData().setScale(ESCALA);

        for (Texto t : textos) {

            float progreso = t.tiempo / DURACION;               // 0 a 1

            // Sube rápido al principio y frena al final
            float subida = SUBIDA * (1f - (1f - progreso) * (1f - progreso));

            // Se mantiene visible y se desvanece en el último tramo
            float alpha = progreso < 0.6f
                    ? 1f
                    : 1f - (progreso - 0.6f) / 0.4f;

            layout.setText(fuente, t.texto);

            float x = t.x - layout.width / 2f;
            float y = t.y + subida;

            // Sombra para que se lea sobre cualquier fondo
            fuente.setColor(0f, 0f, 0f, 0.8f * alpha);
            fuente.draw(batch, t.texto, x + 1.5f, y - 1.5f);

            fuente.setColor(t.color.r, t.color.g, t.color.b, alpha);
            fuente.draw(batch, t.texto, x, y);
        }

        fuente.setColor(Color.WHITE);
        fuente.getData().setScale(1f);

        batch.end();
    }
}