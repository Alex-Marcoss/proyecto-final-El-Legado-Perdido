package Juego.facuAlex;

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

        stateTime = 0f;
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
    // DISPOSE
    // =========================

    public void dispose() {

        if (sheetTexture != null) {
            sheetTexture.dispose();
        }
    }
}