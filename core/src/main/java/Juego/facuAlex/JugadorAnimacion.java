package Juego.facuAlex;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class JugadorAnimacion {

    private Texture sheetTexture;

    // IDLE
    private Animation<TextureRegion> idleUp;
    private Animation<TextureRegion> idleLeft;
    private Animation<TextureRegion> idleRight;
    private Animation<TextureRegion> idleDown;

    // CAMINAR
    private Animation<TextureRegion> walkUp;
    private Animation<TextureRegion> walkLeft;
    private Animation<TextureRegion> walkRight;
    private Animation<TextureRegion> walkDown;

    // CORRER
    private Animation<TextureRegion> runUp;
    private Animation<TextureRegion> runLeft;
    private Animation<TextureRegion> runRight;
    private Animation<TextureRegion> runDown;

    private float stateTime;

    public JugadorAnimacion() {

        sheetTexture = new Texture(
            Gdx.files.internal("sprites/jugador.png")
        );

        sheetTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        cargarAnimaciones();

        stateTime = 0f;
    }

    private void cargarAnimaciones() {

        int frameWidth =
            sheetTexture.getWidth() / 10;

        int frameHeight =
            sheetTexture.getHeight() / 4;

        TextureRegion[][] frames =
            TextureRegion.split(
                sheetTexture,
                frameWidth,
                frameHeight
            );

        // ==========================
        // IDLE
        // ==========================

        idleDown = new Animation<TextureRegion>(
            0.15f,
            new TextureRegion(frames[0][0])
        );

        idleUp = new Animation<TextureRegion>(
            0.15f,
            new TextureRegion(frames[1][0])
        );

        idleLeft = new Animation<TextureRegion>(
            0.15f,
            new TextureRegion(frames[2][0])
        );

        idleRight = new Animation<TextureRegion>(
            0.15f,
            new TextureRegion(frames[3][0])
        );

        // ==========================
        // CAMINAR
        // ==========================

        walkDown = new Animation<TextureRegion>(
            0.10f,
            extraerFrames(frames, 0)
        );

        walkUp = new Animation<TextureRegion>(
            0.10f,
            extraerFrames(frames, 1)
        );

        walkLeft = new Animation<TextureRegion>(
            0.10f,
            extraerFrames(frames, 2)
        );

        walkRight = new Animation<TextureRegion>(
            0.10f,
            extraerFrames(frames, 3)
        );

        // ==========================
        // CORRER
        // ==========================

        runDown = new Animation<TextureRegion>(
            0.05f,
            extraerFrames(frames, 0)
        );

        runUp = new Animation<TextureRegion>(
            0.05f,
            extraerFrames(frames, 1)
        );

        runLeft = new Animation<TextureRegion>(
            0.05f,
            extraerFrames(frames, 2)
        );

        runRight = new Animation<TextureRegion>(
            0.05f,
            extraerFrames(frames, 3)
        );
    }

    private TextureRegion[] extraerFrames(
        TextureRegion[][] frames,
        int fila
    ) {

        TextureRegion[] resultado =
            new TextureRegion[10];

        for (int i = 0; i < 10; i++) {

            resultado[i] =
                new TextureRegion(frames[fila][i]);
        }

        return resultado;
    }

    public void actualizar(float delta) {

        stateTime += delta;
    }

    public TextureRegion getFrame(
        Animation<TextureRegion> animacion
    ) {

        return animacion.getKeyFrame(
            stateTime,
            true
        );
    }

    // ==========================
    // IDLE
    // ==========================

    public Animation<TextureRegion> getIdleUp() {
        return idleUp;
    }

    public Animation<TextureRegion> getIdleLeft() {
        return idleLeft;
    }

    public Animation<TextureRegion> getIdleRight() {
        return idleRight;
    }

    public Animation<TextureRegion> getIdleDown() {
        return idleDown;
    }

    // ==========================
    // CAMINAR
    // ==========================

    public Animation<TextureRegion> getWalkUp() {
        return walkUp;
    }

    public Animation<TextureRegion> getWalkLeft() {
        return walkLeft;
    }

    public Animation<TextureRegion> getWalkRight() {
        return walkRight;
    }

    public Animation<TextureRegion> getWalkDown() {
        return walkDown;
    }

    // ==========================
    // CORRER
    // ==========================

    public Animation<TextureRegion> getRunUp() {
        return runUp;
    }

    public Animation<TextureRegion> getRunLeft() {
        return runLeft;
    }

    public Animation<TextureRegion> getRunRight() {
        return runRight;
    }

    public Animation<TextureRegion> getRunDown() {
        return runDown;
    }

    public void dispose() {

        if (sheetTexture != null) {
            sheetTexture.dispose();
        }
    }
}