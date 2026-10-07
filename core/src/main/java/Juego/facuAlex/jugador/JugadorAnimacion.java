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



    private Texture minarTexture;
    private TextureRegion[] minarIzquierda;
    private TextureRegion[] minarDerecha;


    private float minarFactor = 1f;

    private static final int MINAR_CELDA_ANCHO = 154;
    private static final int MINAR_CELDA_ALTO = 171;


    private static final float[] MINAR_DURACION_FRAMES = {
        0.09f, 0.09f, 0.07f, 0.07f, 0.16f
    };


    public static final float MINAR_PIES_X_IZQUIERDA = 57f;
    public static final float MINAR_PIES_X_DERECHA = 45f;

    // =========================
    // TALAR 
    // =========================

    private Texture talarTexture;
    private TextureRegion[] talarIzquierda;
    private TextureRegion[] talarDerecha;

    private float talarFactor = 1f;

    private static final int TALAR_CELDA_ALTO = 171;


    private static final float[] TALAR_DURACION_FRAMES = {
        0.08f, 0.10f, 0.10f, 0.08f, 0.16f
    };

    // =========================
    // GOLPEAR 
    // =========================

    private Texture golpearTexture;
    private TextureRegion[][] golpear;

    private static final float GOLPEAR_FACTOR = 0.796f;

    private static final float[] GOLPEAR_DURACION_FRAMES = {
        0.06f, 0.06f, 0.10f, 0.10f
    };


    public static final float GOLPEAR_PIES_X_FRENTE = 50f;

    public JugadorAnimacion() {


        sheetTexture = new Texture(
            Gdx.files.internal("sprites/jugador.png")
        );


        sheetTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        cargarAnimaciones();
        cargarAnimacionMinar();
        cargarAnimacionTalar();
        cargarAnimacionGolpear();

        stateTime = 0f;
    }

    private void cargarAnimacionMinar() {

        minarTexture = new Texture(
            Gdx.files.internal("sprites/minarr.png")
        );


        minarTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );


        int anchoReal = minarTexture.getWidth();
        int altoReal = minarTexture.getHeight();

        int celdaAncho = anchoReal / 5;
        int celdaAlto = altoReal / 2;

        minarFactor = (MINAR_CELDA_ANCHO * 5f) / anchoReal;

        System.out.println(
            "minar_frames.png: " + anchoReal + " x " + altoReal
            + " px (celda " + celdaAncho + " x " + celdaAlto + ")"
        );


        float proporcion = (float) anchoReal / altoReal;
        float esperada = (MINAR_CELDA_ANCHO * 5f) / (MINAR_CELDA_ALTO * 2f);

        TextureRegion[][] frames =
            TextureRegion.split(
                minarTexture,
                celdaAncho,
                celdaAlto
            );


        minarIzquierda = frames[0];
        minarDerecha = frames[1];
    }

    private void cargarAnimacionTalar() {

        talarTexture = new Texture(
            Gdx.files.internal("sprites/talar.png")
        );


        talarTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        int anchoReal = talarTexture.getWidth();
        int altoReal = talarTexture.getHeight();

        int celdaAncho = anchoReal / 5;
        int celdaAlto = altoReal / 2;


        talarFactor = (TALAR_CELDA_ALTO * 2f) / altoReal;

        TextureRegion[][] frames =
            TextureRegion.split(
                talarTexture,
                celdaAncho,
                celdaAlto
            );


        talarIzquierda = frames[0];
        talarDerecha = frames[1];
    }

    private void cargarAnimacionGolpear() {

        golpearTexture = new Texture(
            Gdx.files.internal("sprites/golpear.png")
        );

        // Pixel art: sin suavizado
        golpearTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        int celdaAncho = golpearTexture.getWidth() / 4;
        int celdaAlto = golpearTexture.getHeight() / 4;

        golpear = TextureRegion.split(
            golpearTexture,
            celdaAncho,
            celdaAlto
        );
    }

    private void cargarAnimaciones() {



        int frameWidth = 102;
        int frameHeight = 144;

        TextureRegion[][] frames =
            TextureRegion.split(
                sheetTexture,
                frameWidth,
                frameHeight
            );



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

    // =========================
    // TALAR
    // =========================

    // Duracion total de un golpe de hacha
    public static float getDuracionTalar() {

        float total = 0f;

        for (float duracion : TALAR_DURACION_FRAMES) {
            total += duracion;
        }

        return total;
    }

    // Momento del golpe: cuando empieza el ultimo frame.
    // Ahi se entrega la madera.
    public static float getTiempoImpactoTalar() {

        return getDuracionTalar()
            - TALAR_DURACION_FRAMES[TALAR_DURACION_FRAMES.length - 1];
    }

    public TextureRegion getFrameTalar(
        boolean haciaLaIzquierda,
        float tiempo
    ) {

        TextureRegion[] frames =
            haciaLaIzquierda ? talarIzquierda : talarDerecha;

        float acumulado = 0f;

        for (int i = 0; i < TALAR_DURACION_FRAMES.length; i++) {

            acumulado += TALAR_DURACION_FRAMES[i];

            if (tiempo < acumulado) {
                return frames[i];
            }
        }

        return frames[frames.length - 1];
    }

    public float getTalarFactor() {
        return talarFactor;
    }

    // =========================
    // GOLPEAR
    // =========================

    public static float getDuracionGolpear() {

        float total = 0f;

        for (float duracion : GOLPEAR_DURACION_FRAMES) {
            total += duracion;
        }

        return total;
    }

    // Momento del golpe: cuando empieza el frame de impacto (el 3ro)
    public static float getTiempoImpactoGolpear() {

        return GOLPEAR_DURACION_FRAMES[0]
            + GOLPEAR_DURACION_FRAMES[1];
    }

    public TextureRegion getFrameGolpear(
        JugadorControl.Direccion direccion,
        float tiempo
    ) {

        int fila;

        switch (direccion) {
            case ARRIBA:    fila = 1; break;
            case DERECHA:   fila = 2; break;
            case IZQUIERDA: fila = 3; break;
            default:        fila = 0; break;
        }

        TextureRegion[] frames = golpear[fila];

        float acumulado = 0f;

        for (int i = 0; i < GOLPEAR_DURACION_FRAMES.length; i++) {

            acumulado += GOLPEAR_DURACION_FRAMES[i];

            if (tiempo < acumulado) {
                return frames[i];
            }
        }

        return frames[frames.length - 1];
    }

    public static float getGolpearFactor() {
        return GOLPEAR_FACTOR;
    }

    // Donde estan los pies (en pixeles de la celda de caminar)
    public static float getPiesXGolpear(
        JugadorControl.Direccion direccion
    ) {

        switch (direccion) {
            case DERECHA:   return MINAR_PIES_X_DERECHA;
            case IZQUIERDA: return MINAR_PIES_X_IZQUIERDA;
            default:        return GOLPEAR_PIES_X_FRENTE;
        }
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

        if (talarTexture != null) {
            talarTexture.dispose();
        }

        if (golpearTexture != null) {
            golpearTexture.dispose();
        }
    }
}
