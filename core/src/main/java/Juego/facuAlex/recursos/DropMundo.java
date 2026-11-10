package Juego.facuAlex.recursos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Un recurso que quedo en el suelo (por ejemplo la carne de un animal).
 * Se recoge con E estando cerca.
 *
 * (x, y) = punto donde cayo: x al centro, y a la altura de los pies.
 */
public class DropMundo {

    private static final float TAMANO = 36f;
    private static final float DISTANCIA_RECOGER = 70f;
    private static final float ALTURA_FLOTE = 4f;
    private static final float MITAD_JUGADOR = 32f;

    private final Recursos item;
    private final float x;
    private final float y;

    private float tiempo;
    private boolean recogido;

    private final GlyphLayout layout = new GlyphLayout();

    public DropMundo(Recursos item, float x, float y) {

        this.item = item;
        this.x = x;
        this.y = y;
        this.recogido = false;
    }

    public void dibujar(SpriteBatch batch) {

        if (recogido) {
            return;
        }

        Texture icono = item.getIcono();

        if (icono == null) {
            return;
        }

        tiempo += Gdx.graphics.getDeltaTime();

        // Flota suavemente para que se note que se puede recoger
        float flote = (float) Math.sin(tiempo * 3f) * ALTURA_FLOTE;

        batch.draw(
                icono,
                x - TAMANO / 2f,
                y + flote,
                TAMANO,
                TAMANO
        );
    }

    // Llamar con el batch iniciado
    public void dibujarCartel(SpriteBatch batch, BitmapFont font) {

        String texto = "E para recoger: " + item.getNombre()
                + " x" + item.getCantidad();

        layout.setText(font, texto);

        font.draw(
                batch,
                texto,
                x - layout.width / 2f,
                y + TAMANO + 24f
        );
    }

    // jugadorX / jugadorY = posicion del jugador (esquina del sprite)
    public float distanciaAl(float jugadorX, float jugadorY) {

        float dx = jugadorX + MITAD_JUGADOR - x;
        float dy = jugadorY - y;

        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public boolean estaCerca(float jugadorX, float jugadorY) {
        return distanciaAl(jugadorX, jugadorY) <= DISTANCIA_RECOGER;
    }

    public Recursos getItem() {
        return item;
    }

    public void marcarRecogido() {
        recogido = true;
    }

    public boolean estaRecogido() {
        return recogido;
    }

    // Si nadie lo recogio, libera el icono
    public void dispose() {

        if (!recogido) {
            item.liberarIcono();
        }
    }
}