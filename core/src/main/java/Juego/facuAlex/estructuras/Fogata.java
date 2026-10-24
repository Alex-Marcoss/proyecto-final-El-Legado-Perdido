package Juego.facuAlex.estructuras;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/*
 * Fogata animada.
 *
 * estructuras/fogata.png es una hoja de 4 columnas x 4 filas (16 frames de
 * 128x128). Los frames se numeran de izquierda a derecha y de arriba hacia
 * abajo (fila * 4 + columna):
 *
 *   0 1 2 3      llamas altas / bajando
 *   4 5 6 7
 *   8 9 10 11    llamas bajas / brasas / humo
 *   12 13 14 15  brasas / apagada
 *
 * La fogata se consume con el tiempo: cada madera suma segundos de
 * combustible, y las llamas bajan a medida que se acaba. Con 0 queda apagada,
 * y si se le agrega madera se vuelve a prender.
 *
 * Se anima sola: el tiempo avanza cada vez que se dibuja.
 */
public class Fogata extends Estructura {

    private static final int COLUMNAS = 4;
    private static final int FILAS = 4;

    // ---- Combustible (en segundos) ----

    // Una fogata recien armada arranca prendida con este combustible
    private static final float COMBUSTIBLE_INICIAL = 60f;


    public static final float SEGUNDOS_POR_MADERA = 45f;

    // Cuanto combustible queda para pasar de una etapa a la siguiente
    private static final float UMBRAL_ALTA = 30f;
    private static final float UMBRAL_MEDIA = 20f;
    private static final float UMBRAL_BAJA = 12f;
    private static final float UMBRAL_BRASAS = 5f;

    // Tras apagarse, un ratito sigue saliendo humo
    private static final float TIEMPO_HUMO = 4f;

    // ---- Frames de cada etapa ----

    private static final int[] LLAMA_ALTA = { 0, 1, 2, 4, 5, 6 };
    private static final int[] LLAMA_MEDIA = { 3, 8 };
    private static final int[] LLAMA_BAJA = { 9, 7 };
    private static final int[] BRASAS = { 10, 12 };

    private static final int BRASAS_FINALES = 13;
    private static final int HUMO = 14;
    private static final int APAGADA = 15;

    private static final float DURACION_ALTA = 0.12f;
    private static final float DURACION_MEDIA = 0.15f;
    private static final float DURACION_BAJA = 0.18f;
    private static final float DURACION_BRASAS = 0.35f;

    private final TextureRegion[] frames;

    private float combustible = COMBUSTIBLE_INICIAL;
    private float tiempoAnimacion;
    private float tiempoApagada;

    public Fogata(float x, float y) {
        super(
                "Fogata",
                "estructuras/fogata.png",
                x, y, 64f, 64f
        );

        // El sprite es de 128x128 y se dibuja a 64x64
        textura.setFilter(
                Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear
        );

        TextureRegion[][] celdas = TextureRegion.split(
                textura,
                textura.getWidth() / COLUMNAS,
                textura.getHeight() / FILAS
        );

        frames = new TextureRegion[COLUMNAS * FILAS];

        for (int fila = 0; fila < FILAS; fila++) {

            for (int col = 0; col < COLUMNAS; col++) {

                frames[fila * COLUMNAS + col] = celdas[fila][col];
            }
        }
    }

    // =====================================================
    // COMBUSTIBLE
    // =====================================================

    /*
     * Agrega madera a la fogata. Si estaba apagada, se vuelve a prender.
     * Devuelve false si ya esta llena de combustible (para no gastar la
     * madera del inventario).
     */

    public boolean encender() {

    if (estaEncendida()) {
        return false;
    }

    combustible = COMBUSTIBLE_INICIAL;
    tiempoApagada = 0f;
    tiempoAnimacion = 0f;

    return true;
}


    public boolean estaEncendida() {

        return combustible > 0f;
    }
    
    

    // =====================================================
    // DIBUJAR
    // =====================================================

    @Override
    public void dibujar(SpriteBatch batch) {

        avanzar(Gdx.graphics.getDeltaTime());

        batch.draw(
                frames[frameActual()],
                x,
                y,
                ancho,
                alto
        );
    }

    private void avanzar(float delta) {

        tiempoAnimacion += delta;

        if (combustible > 0f) {

            combustible = Math.max(0f, combustible - delta);

            if (combustible == 0f) {

                tiempoApagada = 0f;
            }

        } else {

            tiempoApagada += delta;
        }
    }

    private int frameActual() {

        if (combustible <= 0f) {

            return tiempoApagada < TIEMPO_HUMO ? HUMO : APAGADA;
        }

        if (combustible > UMBRAL_ALTA) {

            return ciclo(LLAMA_ALTA, DURACION_ALTA);
        }

        if (combustible > UMBRAL_MEDIA) {

            return ciclo(LLAMA_MEDIA, DURACION_MEDIA);
        }

        if (combustible > UMBRAL_BAJA) {

            return ciclo(LLAMA_BAJA, DURACION_BAJA);
        }

        if (combustible > UMBRAL_BRASAS) {

            return ciclo(BRASAS, DURACION_BRASAS);
        }

        return BRASAS_FINALES;
    }

    private int ciclo(int[] lista, float duracion) {

        int indice = (int) (tiempoAnimacion / duracion) % lista.length;

        return lista[indice];
    }
}