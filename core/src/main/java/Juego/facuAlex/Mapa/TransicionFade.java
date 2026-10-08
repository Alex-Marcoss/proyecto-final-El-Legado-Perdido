package Juego.facuAlex.Mapa;   // <-- ajustá el package a donde tengas tus clases del mundo

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

/**
 * Fundido a negro para pasar de un mapa a otro.
 *
 *   transicion.iniciar(() -> { ...cambiar de mapa... });
 *
 * La pantalla se oscurece, cuando esta TOTALMENTE negra se ejecuta el codigo
 * que le pasaste (cambiar de mapa, mover al jugador) y despues se aclara.
 *
 * Mientras isActiva() sea true, conviene bloquear el movimiento del jugador.
 */
public class TransicionFade {

    // Segundos que tarda en oscurecerse (y lo mismo en aclararse)
    private static final float DURACION = 0.45f;

    private enum Fase {
        NINGUNA,
        OSCURECIENDO,
        ACLARANDO
    }

    private Fase fase = Fase.NINGUNA;

    private float alfa = 0f;

    private Runnable alMedio;

    private final ShapeRenderer shape = new ShapeRenderer();

    private final Matrix4 proyeccion = new Matrix4();

    public void iniciar(Runnable alMedio) {

        // Si ya hay una transicion en curso, se ignora
        if (fase != Fase.NINGUNA) {
            return;
        }

        this.alMedio = alMedio;
        this.fase = Fase.OSCURECIENDO;
        this.alfa = 0f;
    }

    public void actualizar(float delta) {

        if (fase == Fase.OSCURECIENDO) {

            alfa += delta / DURACION;

            if (alfa >= 1f) {

                alfa = 1f;

                // Pantalla toda negra: es el momento de cambiar de mapa
                if (alMedio != null) {
                    alMedio.run();
                    alMedio = null;
                }

                fase = Fase.ACLARANDO;
            }

        } else if (fase == Fase.ACLARANDO) {

            alfa -= delta / DURACION;

            if (alfa <= 0f) {

                alfa = 0f;
                fase = Fase.NINGUNA;
            }
        }
    }

    public boolean isActiva() {
        return fase != Fase.NINGUNA;
    }

    // Llamar al FINAL de render(), cuando el batch ya hizo end()
    public void dibujar() {

        if (alfa <= 0f) {
            return;
        }

        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();

        proyeccion.setToOrtho2D(0, 0, ancho, alto);
        shape.setProjectionMatrix(proyeccion);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0f, 0f, 0f, alfa);
        shape.rect(0, 0, ancho, alto);
        shape.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        shape.dispose();
    }
}