package Juego.facuAlex;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Mantiene la proporcion del juego (900 x 600) sin importar el tamano
 * de la ventana.
 */
public final class Proporcion {

    public static final float ANCHO = 900f;
    public static final float ALTO = 600f;

    private static final FitViewport viewport =
            new FitViewport(ANCHO, ALTO);

    private static final Vector2 temporal = new Vector2();

    private Proporcion() {
    }

    // Llamar en create() y en resize()
    public static void actualizar(int ancho, int alto) {

        viewport.update(ancho, alto, true);
    }

    // Llamar al principio de cada render(): pinta las barras
    // negras y limita el dibujo a la zona con la proporcion correcta.
    public static void aplicar() {

        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);

        Gdx.gl.glViewport(
            0,
            0,
            Gdx.graphics.getBackBufferWidth(),
            Gdx.graphics.getBackBufferHeight()
        );

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();

        // Los clear() posteriores del juego no tocan las barras negras
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);

        Gdx.gl.glScissor(
            viewport.getScreenX(),
            viewport.getScreenY(),
            viewport.getScreenWidth(),
            viewport.getScreenHeight()
        );
    }

    // Mouse en coordenadas virtuales (0..900 y 0..600, Y hacia arriba)
    public static float mouseX() {

        temporal.set(Gdx.input.getX(), Gdx.input.getY());

        return viewport.unproject(temporal).x;
    }

    public static float mouseY() {

        temporal.set(Gdx.input.getX(), Gdx.input.getY());

        return viewport.unproject(temporal).y;
    }
}
