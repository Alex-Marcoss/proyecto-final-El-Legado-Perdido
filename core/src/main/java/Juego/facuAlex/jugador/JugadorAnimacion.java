package Juego.facuAlex.jugador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class JugadorAnimacion {

    private Texture sheetTexture;

    // =========================
    // IDLE
    // =========================

    private Animation<TextureRegion> idleDown;
    private Animation<TextureRegion> idleUp;
    private Animation<TextureRegion> idleLeft;
    private Animation<TextureRegion> idleRight;

    // =========================
    // CAMINAR
    // =========================

    private Animation<TextureRegion> walkDown;
    private Animation<TextureRegion> walkUp;
    private Animation<TextureRegion> walkLeft;
    private Animation<TextureRegion> walkRight;

    // =========================
    // CORRER
    // =========================

    private Animation<TextureRegion> runDown;
    private Animation<TextureRegion> runUp;
    private Animation<TextureRegion> runLeft;
    private Animation<TextureRegion> runRight;

    private float stateTime;

    // =========================
    // MINAR (picar roca)
    // =========================
    //
    // La hoja minar_frames.png tiene 5 columnas x 2 filas:
    //   fila 0 = mirando a la IZQUIERDA
    //   fila 1 = mirando a la DERECHA
    // Cada celda es de 154 x 171 px (mas grande que la de caminar
    // porque tiene que entrar el pico levantado).
    //
    // La animacion NO se repite en loop: se reproduce una sola vez
    // cada vez que se pica. Por eso se maneja con un tiempo propio
    // (lo lleva JugadorControl) en vez del stateTime global.

    private Texture minarTexture;
    private TextureRegion[] minarIzquierda;
    private TextureRegion[] minarDerecha;

    // Factor para pasar de pixeles reales de la imagen a pixeles
    // "de diseno" (770 x 342). Vale 1 si la imagen mide justo eso.
    // Si la imagen tiene otra resolucion (por ejemplo el doble),
    // el codigo se adapta solo y el personaje se ve igual.
    private float minarFactor = 1f;

    private static final int MINAR_CELDA_ANCHO = 154;
    private static final int MINAR_CELDA_ALTO = 171;

    // Cuanto dura cada uno de los 5 frames (en segundos).
    // Los dos primeros levantan el pico, los otros bajan el golpe
    // y el ultimo (el impacto) dura un poco mas para que se note.
    private static final float[] MINAR_DURACION_FRAMES = {
        0.09f, 0.09f, 0.07f, 0.07f, 0.16f
    };

    // Posicion X de los pies dentro de la celda de CAMINAR (102 px de
    // ancho). Sirve para que, al empezar a picar, el personaje no
    // "salte" de lugar: se dibuja con los pies en el mismo punto.
    public static final float MINAR_PIES_X_IZQUIERDA = 57f;
    public static final float MINAR_PIES_X_DERECHA = 45f;

    public JugadorAnimacion() {

        // Cargar sprite sheet
        sheetTexture = new Texture(
            Gdx.files.internal("sprites/jugador.png")
        );

        // Pixel art: sin suavizado
        sheetTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        cargarAnimaciones();
        cargarAnimacionMinar();

        stateTime = 0f;
    }

    private void cargarAnimacionMinar() {

        minarTexture = new Texture(
            Gdx.files.internal("sprites/minarr.png")
        );

        // Pixel art: sin suavizado
        minarTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        // La hoja tiene 5 columnas x 2 filas. En vez de suponer cuantos
        // pixeles mide cada celda, se calcula con el tamano REAL de la
        // imagen. Asi funciona aunque la imagen tenga otra resolucion.
        int anchoReal = minarTexture.getWidth();
        int altoReal = minarTexture.getHeight();

        int celdaAncho = anchoReal / 5;
        int celdaAlto = altoReal / 2;

        minarFactor = (MINAR_CELDA_ANCHO * 5f) / anchoReal;

        System.out.println(
            "minar_frames.png: " + anchoReal + " x " + altoReal
            + " px (celda " + celdaAncho + " x " + celdaAlto + ")"
        );

        // Aviso si la proporcion no es la esperada (2,25 : 1)
        float proporcion = (float) anchoReal / altoReal;
        float esperada = (MINAR_CELDA_ANCHO * 5f) / (MINAR_CELDA_ALTO * 2f);

        TextureRegion[][] frames =
            TextureRegion.split(
                minarTexture,
                celdaAncho,
                celdaAlto
            );

        // Fila 0 = izquierda, fila 1 = derecha
        minarIzquierda = frames[0];
        minarDerecha = frames[1];
    }

    private void cargarAnimaciones() {

        /*
         * Sprite sheet:
         *
         * 1020 x 576
         *
         * 10 columnas
         * 4 filas
         *
         * Cada frame:
         *
         * 102 x 144
         */

        int frameWidth = 102;
        int frameHeight = 144;

        TextureRegion[][] frames =
            TextureRegion.split(
                sheetTexture,
                frameWidth,
                frameHeight
            );

        // =========================
        // FILAS
        // =========================
        //
        // Fila 0 = DOWN
        // Fila 1 = UP
        // Fila 2 = LEFT
        // Fila 3 = RIGHT
        //

        // =========================
        // IDLE
        // =========================

        idleDown = new Animation<TextureRegion>(
            0.15f,
            frames[0][0]
        );

        idleUp = new Animation<TextureRegion>(
            0.15f,
            frames[1][0]
        );

        idleLeft = new Animation<TextureRegion>(
            0.15f,
            frames[2][0]
        );

        idleRight = new Animation<TextureRegion>(
            0.15f,
            frames[3][0]
        );

        // =========================
        // CAMINAR
        // =========================

        walkDown = new Animation<TextureRegion>(
            0.10f,
            frames[0]
        );

        walkUp = new Animation<TextureRegion>(
            0.10f,
            frames[1]
        );

        walkLeft = new Animation<TextureRegion>(
            0.10f,
            frames[2]
        );

        walkRight = new Animation<TextureRegion>(
            0.10f,
            frames[3]
        );

        // =========================
        // CORRER
        // =========================

        runDown = new Animation<TextureRegion>(
            0.05f,
            frames[0]
        );

        runUp = new Animation<TextureRegion>(
            0.05f,
            frames[1]
        );

        runLeft = new Animation<TextureRegion>(
            0.05f,
            frames[2]
        );

        runRight = new Animation<TextureRegion>(
            0.05f,
            frames[3]
        );
    }

    // =========================
    // ACTUALIZAR
    // =========================

    public void actualizar(float delta) {

        stateTime += delta;
    }

    // =========================
    // OBTENER FRAME
    // =========================

    public TextureRegion getFrame(
        Animation<TextureRegion> animacion
    ) {

        return animacion.getKeyFrame(
            stateTime,
            true
        );
    }

    // =========================
    // IDLE
    // =========================

    public Animation<TextureRegion> getIdleDown() {
        return idleDown;
    }

    public Animation<TextureRegion> getIdleUp() {
        return idleUp;
    }

    public Animation<TextureRegion> getIdleLeft() {
        return idleLeft;
    }

    public Animation<TextureRegion> getIdleRight() {
        return idleRight;
    }

    // =========================
    // CAMINAR
    // =========================

    public Animation<TextureRegion> getWalkDown() {
        return walkDown;
    }

    public Animation<TextureRegion> getWalkUp() {
        return walkUp;
    }

    public Animation<TextureRegion> getWalkLeft() {
        return walkLeft;
    }

    public Animation<TextureRegion> getWalkRight() {
        return walkRight;
    }

    // =========================
    // CORRER
    // =========================

    public Animation<TextureRegion> getRunDown() {
        return runDown;
    }

    public Animation<TextureRegion> getRunUp() {
        return runUp;
    }

    public Animation<TextureRegion> getRunLeft() {
        return runLeft;
    }

    public Animation<TextureRegion> getRunRight() {
        return runRight;
    }

    // =========================
    // MINAR
    // =========================

    // Duracion total de un golpe de pico (suma de todos los frames)
    public static float getDuracionMinar() {

        float total = 0f;

        for (float duracion : MINAR_DURACION_FRAMES) {
            total += duracion;
        }

        return total;
    }

    // Momento exacto del golpe: cuando empieza el ultimo frame
    // (el pico toca la roca). Ahi se entrega la piedra.
    public static float getTiempoImpactoMinar() {

        return getDuracionMinar()
            - MINAR_DURACION_FRAMES[MINAR_DURACION_FRAMES.length - 1];
    }

    // Devuelve el frame que corresponde al tiempo transcurrido
    // desde que empezo el golpe (0 = recien empieza).
    public TextureRegion getFrameMinar(
        boolean haciaLaIzquierda,
        float tiempo
    ) {

        TextureRegion[] frames =
            haciaLaIzquierda ? minarIzquierda : minarDerecha;

        float acumulado = 0f;

        for (int i = 0; i < MINAR_DURACION_FRAMES.length; i++) {

            acumulado += MINAR_DURACION_FRAMES[i];

            if (tiempo < acumulado) {
                return frames[i];
            }
        }

        // Si se paso del tiempo, queda en el ultimo frame
        return frames[frames.length - 1];
    }

    // Convierte pixeles reales de un frame de minar a pixeles de
    // diseno (los mismos que usa el sprite de caminar).
    public float getMinarFactor() {
        return minarFactor;
    }

    // Ancho y alto de la celda de minar (en pixeles del sprite)
    public static int getMinarCeldaAncho() {
        return MINAR_CELDA_ANCHO;
    }

    // =========================
    // DISPOSE
    // =========================

    public void dispose() {

        if (sheetTexture != null) {
            sheetTexture.dispose();
        }

        if (minarTexture != null) {
            minarTexture.dispose();
        }
    }
}
