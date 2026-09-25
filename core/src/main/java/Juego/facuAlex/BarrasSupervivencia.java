package Juego.facuAlex;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class BarrasSupervivencia {

    private SpriteBatch batch;

    private Texture texturaHUD;
    private Texture texturaPixel;

    private Jugador jugador;

    private OrthographicCamera camaraHUD;


    // ==================================================
    // RESOLUCIÓN VIRTUAL
    // ==================================================

    private static final float ANCHO_VIRTUAL = 900f;
    private static final float ALTO_VIRTUAL = 600f;


    // ==================================================
    // POSICIÓN DEL HUD
    // ==================================================

    private static final float POSICION_X = 1f;
    private static final float POSICION_Y = 470f;


    // ==================================================
    // TAMAÑO ORIGINAL DE LA IMAGEN
    // ==================================================

    private static final float ANCHO_TEXTURE = 853f;
    private static final float ALTO_TEXTURE = 464f;

   

   

    
    // ==================================================
    // ESCALA
    // ==================================================

    private static final float ESCALA = 0.30f;


    // ==================================================
    // ÁREA DE RELLENO
    // ==================================================

    private static final float X_RELLENO = 157f;
    private static final float ANCHO_RELLENO = 624f;
    private static final float ALTO_RELLENO = 62f;


    // Coordenadas desde arriba del PNG

    private static final float Y_VIDA = 57f;
    private static final float Y_HAMBRE = 199f;
    private static final float Y_ENERGIA = 341f;



    // ==================================================
    // COLORES
    // ==================================================

    private static final Color COLOR_VIDA =
            new Color(0.85f, 0.08f, 0.08f, 1f);

    private static final Color COLOR_HAMBRE =
            new Color(1f, 0.45f, 0.05f, 1f);

    private static final Color COLOR_ENERGIA =
            new Color(0.10f, 0.45f, 1f, 1f);


    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public BarrasSupervivencia(Jugador jugador) {

        this.jugador = jugador;

        batch = new SpriteBatch();


        // Cámara independiente para el HUD
        camaraHUD = new OrthographicCamera();

        camaraHUD.setToOrtho(
                false,
                ANCHO_VIRTUAL,
                ALTO_VIRTUAL
        );


        // ==================================================
        // IMAGEN DEL HUD
        // ==================================================

        texturaHUD = new Texture(
                Gdx.files.internal("barras.png")
        );


        // ==================================================
        // PIXEL BLANCO
        // ==================================================

        Pixmap pixmap = new Pixmap(
                1,
                1,
                Pixmap.Format.RGBA8888
        );

        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        texturaPixel = new Texture(pixmap);

        pixmap.dispose();
    }


    // ==================================================
    // RESIZE
    // ==================================================

    public void resize(int width, int height) {

        /*
         * NO modificamos el viewport global.
         *
         * Solamente actualizamos la cámara
         * independiente del HUD.
         */

        camaraHUD.setToOrtho(
                false,
                ANCHO_VIRTUAL,
                ALTO_VIRTUAL
        );

        camaraHUD.update();
    }


    // ==================================================
    // DIBUJAR
    // ==================================================

    public void dibujar() {

        camaraHUD.update();

        batch.setProjectionMatrix(
                camaraHUD.combined
        );

        batch.begin();


        // ==================================================
        // IMAGEN BASE
        // ==================================================

        batch.setColor(Color.WHITE);

        batch.draw(
                texturaHUD,

                POSICION_X,
                POSICION_Y,

                ANCHO_TEXTURE * ESCALA,
                ALTO_TEXTURE * ESCALA
        );


        // ==================================================
        // RELLENOS
        // ==================================================

        dibujarBarra(
                jugador.getVida(),
                Y_VIDA,
                COLOR_VIDA
        );

        dibujarBarra(
                jugador.getHambre(),
                Y_HAMBRE,
                COLOR_HAMBRE
        );

        dibujarBarra(
                jugador.getEnergia(),
                Y_ENERGIA,
                COLOR_ENERGIA
        );


        batch.setColor(Color.WHITE);

        batch.end();
    }


    // ==================================================
    // DIBUJAR BARRA
    // ==================================================

    private void dibujarBarra(
            int valor,
            float yDesdeArriba,
            Color color
    ) {

        // Limitar entre 0 y 100

        if (valor < 0) {
            valor = 0;
        }

        if (valor > 100) {
            valor = 100;
        }


        // Porcentaje

        float porcentaje =
                valor / 100f;


        // Ancho correspondiente al porcentaje

        float anchoActual =
                ANCHO_RELLENO * porcentaje;


        if (anchoActual <= 0) {
            return;
        }


        // ==================================================
        // POSICIÓN X
        // ==================================================

        float x =
                POSICION_X
                + X_RELLENO * ESCALA;


        // ==================================================
        // POSICIÓN Y
        // ==================================================

        float y =
                POSICION_Y
                + (
                    ALTO_TEXTURE
                    - yDesdeArriba
                    - ALTO_RELLENO
                  ) * ESCALA;


        // ==================================================
        // TAMAÑO
        // ==================================================

        float ancho =
                anchoActual * ESCALA;

        float alto =
                ALTO_RELLENO * ESCALA;


        // ==================================================
        // COLOR
        // ==================================================

        batch.setColor(color);


        // ==================================================
        // DIBUJAR
        // ==================================================

        batch.draw(
                texturaPixel,
                x,
                y,
                ancho,
                alto
        );


        batch.setColor(Color.WHITE);
    }


    // ==================================================
    // DISPOSE
    // ==================================================

    public void dispose() {

        if (texturaHUD != null) {
            texturaHUD.dispose();
        }

        if (texturaPixel != null) {
            texturaPixel.dispose();
        }

        if (batch != null) {
            batch.dispose();
        }
    }
}