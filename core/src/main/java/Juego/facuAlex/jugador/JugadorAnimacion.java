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

    // =========================
    // TALAR (cortar arbol con el hacha)
    // =========================
    //
    // La hoja talar.png tiene 5 columnas x 2 filas (igual que minar):
    //   fila 0 = mirando a la IZQUIERDA
    //   fila 1 = mirando a la DERECHA
    // Se reproduce una sola vez por golpe de hacha.
    // El alto de cada celda en "pixeles de diseno" es TALAR_CELDA_ALTO;
    // el ancho se calcula solo a partir de la imagen real.

    private Texture talarTexture;
    private TextureRegion[] talarIzquierda;
    private TextureRegion[] talarDerecha;

    private float talarFactor = 1f;

    private static final int TALAR_CELDA_ALTO = 171;

    // Frames 0-3: el hacha se levanta y baja. Frame 4: impacto.
    private static final float[] TALAR_DURACION_FRAMES = {
        0.08f, 0.10f, 0.10f, 0.08f, 0.16f
    };

    // =========================
    // GOLPEAR (pegar con el puno)
    // =========================
    //
    // La hoja golpear.png tiene 4 columnas (frames) x 4 filas:
    //   fila 0 = mirando ABAJO (de frente)
    //   fila 1 = mirando ARRIBA (de espaldas)
    //   fila 2 = mirando a la DERECHA
    //   fila 3 = mirando a la IZQUIERDA
    // Frames: 0 = prepara el brazo, 1 = estira, 2 = IMPACTO (con
    // chispa), 3 = vuelve. Se reproduce una sola vez por golpe.

    private Texture golpearTexture;
    private TextureRegion[][] golpear;

    // Pixeles de diseno por pixel real: hace que el personaje de
    // la hoja se vea del mismo tamano que al caminar.
    private static final float GOLPEAR_FACTOR = 0.796f;

    private static final float[] GOLPEAR_DURACION_FRAMES = {
        0.06f, 0.06f, 0.10f, 0.10f
    };

    // Posicion X de los pies dentro de la celda de caminar (102 px)
    public static final float GOLPEAR_PIES_X_FRENTE = 50f;

    // =========================
    // HERIDO (recibir dano)
    // =========================
    //
    // La hoja dano.png tiene 3 columnas (frames) x 4 filas:
    //   fila 0 = ABAJO, 1 = ARRIBA, 2 = DERECHA, 3 = IZQUIERDA
    // Frames: 0 = el golpe, 1 = el dolor (sangre), 2 = se recupera.
    // Misma escala que golpear.png (GOLPEAR_FACTOR).

    private Texture danoTexture;
    private TextureRegion[][] dano;

    private static final float[] DANO_DURACION_FRAMES = {
        0.10f, 0.14f, 0.10f
    };

    // =========================
    // RECOLECTAR (bayas y fibra)
    // =========================
    //
    // La hoja recolectar.png tiene 8 columnas (frames) x 2 filas
    // (igual que minar y talar):
    //   fila 0 = mirando a la IZQUIERDA
    //   fila 1 = mirando a la DERECHA
    // Frames: 0 = de pie, 1 = empieza a agacharse, 2-3-4 = agachado
    // estirando la mano, 5 = AGARRA y empieza a levantarse (ya tiene
    // el objeto en la mano), 6-7 = vuelve a pararse.
    // Se reproduce una sola vez por cada recoleccion.
    //
    // Los pies de cada frame estan centrados en la celda, asi que se
    // usan los mismos MINAR_PIES_X_* para ubicarlo.

    private Texture recolectarTexture;
    private TextureRegion[] recolectarIzquierda;
    private TextureRegion[] recolectarDerecha;

    private static final int RECOLECTAR_COLUMNAS = 8;

    // Pixeles de diseno por pixel real: hace que el personaje de la
    // hoja se vea del mismo tamano que al caminar.
    private static final float RECOLECTAR_FACTOR = 0.741f;

    private static final float[] RECOLECTAR_DURACION_FRAMES = {
        0.05f, 0.05f, 0.07f, 0.08f, 0.09f, 0.08f, 0.07f, 0.08f
    };

    // Frame en el que la mano agarra el objeto: ahi se entrega
    // la baya o la fibra.
    private static final int RECOLECTAR_FRAME_IMPACTO = 5;

    // =========================
    // COMER
    // =========================
    //
    // La hoja comer.png tiene 10 columnas (frames) x 4 filas:
    //   fila 0 = mirando ABAJO (de frente)
    //   fila 1 = mirando ARRIBA (de espaldas)
    //   fila 2 = mirando a la DERECHA
    //   fila 3 = mirando a la IZQUIERDA
    // (mismo orden que golpear.png y dano.png).
    // La mano sube a la boca y mastica. Se reproduce una sola vez
    // cada vez que se come.
    //
    // Los pies de cada frame estan centrados en la celda, asi que se
    // ubica con getPiesXGolpear() igual que golpear y dano.

    private Texture comerTexture;
    private TextureRegion[][] comer;

    private static final int COMER_COLUMNAS = 10;

    // Pixeles de diseno por pixel real: hace que el personaje de
    // la hoja se vea del mismo tamano que al caminar.
    private static final float COMER_FACTOR = 0.729f;

    private static final float[] COMER_DURACION_FRAMES = {
        0.09f, 0.09f, 0.09f, 0.09f, 0.09f,
        0.09f, 0.09f, 0.09f, 0.09f, 0.09f
    };

    // Frame en el que se da el mordisco: ahi se aplica el efecto
    // (sube el hambre y se gasta la comida del inventario).
    private static final int COMER_FRAME_IMPACTO = 4;

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
        cargarAnimacionTalar();
        cargarAnimacionGolpear();
        cargarAnimacionDano();
        cargarAnimacionRecolectar();
        cargarAnimacionComer();

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

    private void cargarAnimacionTalar() {

        talarTexture = new Texture(
            Gdx.files.internal("sprites/talar.png")
        );

        // Pixel art: sin suavizado
        talarTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        int anchoReal = talarTexture.getWidth();
        int altoReal = talarTexture.getHeight();

        int celdaAncho = anchoReal / 5;
        int celdaAlto = altoReal / 2;

        // Pasa de pixeles reales a pixeles de diseno
        talarFactor = (TALAR_CELDA_ALTO * 2f) / altoReal;

        TextureRegion[][] frames =
            TextureRegion.split(
                talarTexture,
                celdaAncho,
                celdaAlto
            );

        // Fila 0 = izquierda, fila 1 = derecha
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

    private void cargarAnimacionDano() {

        danoTexture = new Texture(
            Gdx.files.internal("sprites/dano.png")
        );

        // Pixel art: sin suavizado
        danoTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        int celdaAncho = danoTexture.getWidth() / 3;
        int celdaAlto = danoTexture.getHeight() / 4;

        dano = TextureRegion.split(
            danoTexture,
            celdaAncho,
            celdaAlto
        );
    }

    private void cargarAnimacionRecolectar() {

        recolectarTexture = new Texture(
            Gdx.files.internal("sprites/recolectar.png")
        );

        // Pixel art: sin suavizado
        recolectarTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        int celdaAncho =
            recolectarTexture.getWidth() / RECOLECTAR_COLUMNAS;

        int celdaAlto = recolectarTexture.getHeight() / 2;

        TextureRegion[][] frames =
            TextureRegion.split(
                recolectarTexture,
                celdaAncho,
                celdaAlto
            );

        // Fila 0 = izquierda, fila 1 = derecha
        recolectarIzquierda = frames[0];
        recolectarDerecha = frames[1];
    }

    private void cargarAnimacionComer() {

        comerTexture = new Texture(
            Gdx.files.internal("sprites/comer.png")
        );

        // Pixel art: sin suavizado
        comerTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        int celdaAncho = comerTexture.getWidth() / COMER_COLUMNAS;
        int celdaAlto = comerTexture.getHeight() / 4;

        comer = TextureRegion.split(
            comerTexture,
            celdaAncho,
            celdaAlto
        );
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
    // RECOLECTAR
    // =========================

    // Duracion total de la animacion de recolectar
    public static float getDuracionRecolectar() {

        float total = 0f;

        for (float duracion : RECOLECTAR_DURACION_FRAMES) {
            total += duracion;
        }

        return total;
    }

    // Momento en que la mano agarra el objeto: cuando empieza el
    // frame RECOLECTAR_FRAME_IMPACTO. Ahi se entrega la baya o la fibra.
    public static float getTiempoImpactoRecolectar() {

        float total = 0f;

        for (int i = 0; i < RECOLECTAR_FRAME_IMPACTO; i++) {
            total += RECOLECTAR_DURACION_FRAMES[i];
        }

        return total;
    }

    public TextureRegion getFrameRecolectar(
        boolean haciaLaIzquierda,
        float tiempo
    ) {

        TextureRegion[] frames =
            haciaLaIzquierda ? recolectarIzquierda : recolectarDerecha;

        float acumulado = 0f;

        for (int i = 0; i < RECOLECTAR_DURACION_FRAMES.length; i++) {

            acumulado += RECOLECTAR_DURACION_FRAMES[i];

            if (tiempo < acumulado) {
                return frames[i];
            }
        }

        return frames[frames.length - 1];
    }

    public static float getRecolectarFactor() {
        return RECOLECTAR_FACTOR;
    }

    // =========================
    // COMER
    // =========================

    public static float getDuracionComer() {

        float total = 0f;

        for (float duracion : COMER_DURACION_FRAMES) {
            total += duracion;
        }

        return total;
    }

    // Momento del mordisco: cuando empieza el frame COMER_FRAME_IMPACTO
    public static float getTiempoImpactoComer() {

        float total = 0f;

        for (int i = 0; i < COMER_FRAME_IMPACTO; i++) {
            total += COMER_DURACION_FRAMES[i];
        }

        return total;
    }

    public TextureRegion getFrameComer(
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

        TextureRegion[] frames = comer[fila];

        float acumulado = 0f;

        for (int i = 0; i < COMER_DURACION_FRAMES.length; i++) {

            acumulado += COMER_DURACION_FRAMES[i];

            if (tiempo < acumulado) {
                return frames[i];
            }
        }

        return frames[frames.length - 1];
    }

    public static float getComerFactor() {
        return COMER_FACTOR;
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

    // =========================
    // HERIDO
    // =========================

    public static float getDuracionDano() {

        float total = 0f;

        for (float duracion : DANO_DURACION_FRAMES) {
            total += duracion;
        }

        return total;
    }

    public TextureRegion getFrameDano(
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

        TextureRegion[] frames = dano[fila];

        float acumulado = 0f;

        for (int i = 0; i < DANO_DURACION_FRAMES.length; i++) {

            acumulado += DANO_DURACION_FRAMES[i];

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

        if (danoTexture != null) {
            danoTexture.dispose();
        }

        if (recolectarTexture != null) {
            recolectarTexture.dispose();
        }

        if (comerTexture != null) {
            comerTexture.dispose();
        }
    }
}
