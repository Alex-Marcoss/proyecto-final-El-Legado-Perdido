package Juego.facuAlex.enemigos.guardian;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Animaciones del Guardian.
 *
 * sprites/guardian.png = 768 x 384, grilla EXACTA de 8 x 4 (celdas de 96 x 96).
 *   Filas:    0 = abajo, 1 = arriba, 2 = izquierda, 3 = derecha
 *   Columnas: 0     = idle
 *             1..4  = caminar (se usan 2, 3, 4)
 *             5     = preparar ataque
 *             6     = ataque (el rayo / patada)
 *             7     = recuperacion
 *
 * sprites/guardian_muerte.png = 576 x 384, grilla de 6 x 4 (celdas de 96 x 96).
 *   Mismas filas que arriba. Las 6 columnas son los 6 frames de la muerte
 *   (golpe, retroceso, de rodillas, cae, en el suelo, apagado).
 *   Si ese PNG no existe, el juego NO falla: el Guardian usa el idle.
 *
 * Todos los frames se dibujan con el MISMO ancho/alto y en la MISMA posicion.
 */
public class GuardianAnimacion {

    // Tamano de cada celda en los PNG. Dibujar siempre con este ancho/alto
    // (multiplicado por tu escala) y centrado en el x del Guardian.
    public static final int CELDA = 96;

    private static final int FILA_ABAJO = 0;
    private static final int FILA_ARRIBA = 1;
    private static final int FILA_IZQUIERDA = 2;
    private static final int FILA_DERECHA = 3;

    private static final String RUTA_MUERTE = "sprites/guardian_muerte.png";

    private Texture sheetTexture;
    private TextureRegion[][] frames;

    private Animation<TextureRegion> idleDown, idleUp, idleLeft, idleRight;
    private Animation<TextureRegion> walkDown, walkUp, walkLeft, walkRight;
    private Animation<TextureRegion> prepararDown, prepararUp, prepararLeft, prepararRight;
    private Animation<TextureRegion> atacarDown, atacarUp, atacarLeft, atacarRight;
    private Animation<TextureRegion> recuperarDown, recuperarUp, recuperarLeft, recuperarRight;

    // Muerte: quedan en null si no existe el PNG de muerte
    private Texture muerteTexture;
    private Animation<TextureRegion> muerteDown, muerteUp, muerteLeft, muerteRight;

    // Tiempo del ESTADO actual (se reinicia cada vez que cambia el estado)
    private float stateTime;

    public GuardianAnimacion() {
        sheetTexture = new Texture(Gdx.files.internal("sprites/guardian.png"));
        sheetTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        frames = TextureRegion.split(sheetTexture, CELDA, CELDA);
        cargarAnimaciones();
        cargarMuerte();
        stateTime = 0f;
    }

    // Crea una animacion con las columnas indicadas de una fila de "origen"
    private Animation<TextureRegion> crearDe(TextureRegion[][] origen, float duracion,
                                             PlayMode modo, int fila, int... columnas) {
        TextureRegion[] f = new TextureRegion[columnas.length];
        for (int i = 0; i < columnas.length; i++) {
            f[i] = origen[fila][columnas[i]];
        }
        Animation<TextureRegion> a = new Animation<TextureRegion>(duracion, f);
        a.setPlayMode(modo);
        return a;
    }

    // Lo mismo, pero sobre el spritesheet principal
    private Animation<TextureRegion> crear(float duracion, PlayMode modo, int fila, int... columnas) {
        return crearDe(frames, duracion, modo, fila, columnas);
    }

    private void cargarAnimaciones() {

        // IDLE: columna 0
        idleDown  = crear(0.15f, PlayMode.LOOP, FILA_ABAJO, 0);
        idleUp    = crear(0.15f, PlayMode.LOOP, FILA_ARRIBA, 0);
        idleLeft  = crear(0.15f, PlayMode.LOOP, FILA_IZQUIERDA, 0);
        idleRight = crear(0.15f, PlayMode.LOOP, FILA_DERECHA, 0);

        // CAMINAR: columnas 2, 3, 4 en ida y vuelta
        walkDown  = crear(0.12f, PlayMode.LOOP_PINGPONG, FILA_ABAJO, 2, 3, 4);
        walkUp    = crear(0.12f, PlayMode.LOOP_PINGPONG, FILA_ARRIBA, 2, 3, 4);
        walkLeft  = crear(0.12f, PlayMode.LOOP_PINGPONG, FILA_IZQUIERDA, 2, 3, 4);
        walkRight = crear(0.12f, PlayMode.LOOP_PINGPONG, FILA_DERECHA, 2, 3, 4);

        // PREPARAR: columna 5, estatica (dura 0.8 s)
        prepararDown  = crear(0.10f, PlayMode.LOOP, FILA_ABAJO, 5);
        prepararUp    = crear(0.10f, PlayMode.LOOP, FILA_ARRIBA, 5);
        prepararLeft  = crear(0.10f, PlayMode.LOOP, FILA_IZQUIERDA, 5);
        prepararRight = crear(0.10f, PlayMode.LOOP, FILA_DERECHA, 5);

        // ATACAR: columna 6 -> 7, UNA sola vez (NORMAL) y se queda en el ultimo frame
        atacarDown  = crear(0.12f, PlayMode.NORMAL, FILA_ABAJO, 6, 7);
        atacarUp    = crear(0.12f, PlayMode.NORMAL, FILA_ARRIBA, 6, 7);
        atacarLeft  = crear(0.12f, PlayMode.NORMAL, FILA_IZQUIERDA, 6, 7);
        atacarRight = crear(0.12f, PlayMode.NORMAL, FILA_DERECHA, 6, 7);

        // RECUPERAR: columna 7, estatica
        recuperarDown  = crear(0.10f, PlayMode.LOOP, FILA_ABAJO, 7);
        recuperarUp    = crear(0.10f, PlayMode.LOOP, FILA_ARRIBA, 7);
        recuperarLeft  = crear(0.10f, PlayMode.LOOP, FILA_IZQUIERDA, 7);
        recuperarRight = crear(0.10f, PlayMode.LOOP, FILA_DERECHA, 7);
    }

    // =====================================================
    // MUERTE
    // =====================================================

    private void cargarMuerte() {

        // Si el PNG no esta, no pasa nada: tieneAnimacionMuerte() da false
        if (!Gdx.files.internal(RUTA_MUERTE).exists()) {
            return;
        }

        muerteTexture = new Texture(Gdx.files.internal(RUTA_MUERTE));
        muerteTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        TextureRegion[][] m = TextureRegion.split(muerteTexture, CELDA, CELDA);

        // NORMAL = se reproduce una sola vez y queda en el ultimo frame
        muerteDown  = crearDe(m, 0.12f, PlayMode.NORMAL, FILA_ABAJO,     0, 1, 2, 3, 4, 5);
        muerteUp    = crearDe(m, 0.12f, PlayMode.NORMAL, FILA_ARRIBA,    0, 1, 2, 3, 4, 5);
        muerteLeft  = crearDe(m, 0.12f, PlayMode.NORMAL, FILA_IZQUIERDA, 0, 1, 2, 3, 4, 5);
        muerteRight = crearDe(m, 0.12f, PlayMode.NORMAL, FILA_DERECHA,   0, 1, 2, 3, 4, 5);
    }

    public boolean tieneAnimacionMuerte() {
        return muerteDown != null;
    }

    // Frame de muerte segun hacia donde miraba el Guardian
    public TextureRegion obtenerFrameMuerte(Guardian.Direccion direccion) {

        Animation<TextureRegion> anim;

        switch (direccion) {
            case ARRIBA:    anim = muerteUp;    break;
            case IZQUIERDA: anim = muerteLeft;  break;
            case DERECHA:   anim = muerteRight; break;
            default:        anim = muerteDown;  break;
        }

        return anim.getKeyFrame(stateTime);
    }

    // Version sin direccion (mira hacia abajo)
    public TextureRegion obtenerFrameMuerte() {
        return muerteDown.getKeyFrame(stateTime);
    }

    public boolean terminoAnimacionMuerte() {
        return muerteDown != null && muerteDown.isAnimationFinished(stateTime);
    }

    // =====================================================
    // TIEMPO
    // =====================================================

    public void actualizar(float delta) {
        stateTime += delta;
    }

    /**
     * LLAMAR cada vez que el Guardian cambia de estado
     * (idle -> caminar -> preparar -> atacar -> recuperar -> muerto), para que
     * la animacion arranque desde su primer frame.
     */
    public void reiniciar() {
        stateTime = 0f;
    }

    // Respeta el PlayMode de cada animacion (loop, pingpong o una sola vez)
    public TextureRegion getFrame(Animation<TextureRegion> animacion) {
        return animacion.getKeyFrame(stateTime);
    }

    public boolean terminoAnimacion(Animation<TextureRegion> animacion) {
        return animacion.isAnimationFinished(stateTime);
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public Animation<TextureRegion> getIdleDown()  { return idleDown; }
    public Animation<TextureRegion> getIdleUp()    { return idleUp; }
    public Animation<TextureRegion> getIdleLeft()  { return idleLeft; }
    public Animation<TextureRegion> getIdleRight() { return idleRight; }

    public Animation<TextureRegion> getWalkDown()  { return walkDown; }
    public Animation<TextureRegion> getWalkUp()    { return walkUp; }
    public Animation<TextureRegion> getWalkLeft()  { return walkLeft; }
    public Animation<TextureRegion> getWalkRight() { return walkRight; }

    public Animation<TextureRegion> getPrepararDown()  { return prepararDown; }
    public Animation<TextureRegion> getPrepararUp()    { return prepararUp; }
    public Animation<TextureRegion> getPrepararLeft()  { return prepararLeft; }
    public Animation<TextureRegion> getPrepararRight() { return prepararRight; }

    public Animation<TextureRegion> getAtacarDown()  { return atacarDown; }
    public Animation<TextureRegion> getAtacarUp()    { return atacarUp; }
    public Animation<TextureRegion> getAtacarLeft()  { return atacarLeft; }
    public Animation<TextureRegion> getAtacarRight() { return atacarRight; }

    public Animation<TextureRegion> getRecuperarDown()  { return recuperarDown; }
    public Animation<TextureRegion> getRecuperarUp()    { return recuperarUp; }
    public Animation<TextureRegion> getRecuperarLeft()  { return recuperarLeft; }
    public Animation<TextureRegion> getRecuperarRight() { return recuperarRight; }

    public void dispose() {
        if (sheetTexture != null) {
            sheetTexture.dispose();
        }
        if (muerteTexture != null) {
            muerteTexture.dispose();
        }
    }
}