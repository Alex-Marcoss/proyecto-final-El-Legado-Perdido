package Juego.facuAlex.estructuras;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/*
 * Horno con dos estados: apagado y prendido.
 *
 * estructuras/horno.png es una hoja de 2 columnas x 1 fila (2 frames de
 * 128x128): frame 0 = apagado, frame 1 = prendido.
 *
 * El horno solo cambia de sprite: cuando se pone combustible y empieza a
 * cocinar algo, hay que llamar a setEncendido(true); al terminar, a
 * setEncendido(false).
 */
public class Horno extends Estructura {

    private static final int COLUMNAS = 2;

    private static final int FRAME_APAGADO = 0;
    private static final int FRAME_ENCENDIDO = 1;

    private final TextureRegion[] frames;

    private boolean encendido;

    public Horno(float x, float y) {
        super(
                "Horno",
                "estructuras/horno.png",
                x, y, 64f, 64f
        );

        // El sprite es de 128x128 y se dibuja a 64x64
        textura.setFilter(
                Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear
        );

        frames = TextureRegion.split(
                textura,
                textura.getWidth() / COLUMNAS,
                textura.getHeight()
        )[0];
    }

    // =====================================================
    // ESTADO
    // =====================================================

    public void setEncendido(boolean encendido) {

        this.encendido = encendido;
    }

    public void encender() {

        encendido = true;
    }

    public void apagar() {

        encendido = false;
    }

    public boolean estaEncendido() {

        return encendido;
    }

    // =====================================================
    // DIBUJAR
    // =====================================================

    @Override
    public void dibujar(SpriteBatch batch) {

        batch.draw(
                frames[encendido ? FRAME_ENCENDIDO : FRAME_APAGADO],
                x,
                y,
                ancho,
                alto
        );
    }
}