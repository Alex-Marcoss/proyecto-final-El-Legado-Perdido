package Juego.facuAlex.enemigos.guardian;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GuardianAnimacion {

	
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
// PREPARAR ATAQUE
// =========================

private Animation<TextureRegion> prepararDown;
private Animation<TextureRegion> prepararUp;
private Animation<TextureRegion> prepararLeft;
private Animation<TextureRegion> prepararRight;

// =========================
// ATACAR
// =========================

private Animation<TextureRegion> atacarDown;
private Animation<TextureRegion> atacarUp;
private Animation<TextureRegion> atacarLeft;
private Animation<TextureRegion> atacarRight;

// =========================
// RECUPERACIÓN
// =========================

private Animation<TextureRegion> recuperarDown;
private Animation<TextureRegion> recuperarUp;
private Animation<TextureRegion> recuperarLeft;
private Animation<TextureRegion> recuperarRight;

private float stateTime;

public GuardianAnimacion() {

    sheetTexture = new Texture(
        Gdx.files.internal("sprites/guardian.png")
    );

    sheetTexture.setFilter(
        Texture.TextureFilter.Nearest,
        Texture.TextureFilter.Nearest
    );

    cargarAnimaciones();

    stateTime = 0f;
}

private void cargarAnimaciones() {

    /*
     * Guardian:
     *
     * 8 columnas
     * 4 filas
     *
     * Cada frame ocupa:
     *
     * ancho total / 8
     * alto total / 4
     */

    int frameWidth = sheetTexture.getWidth() / 8;
    int frameHeight = sheetTexture.getHeight() / 4;

    TextureRegion[][] frames =
        TextureRegion.split(
            sheetTexture,
            frameWidth,
            frameHeight
        );

    // =====================================================
    // IDLE
    // =====================================================

    // Fila 0 = frente
    idleDown = new Animation<TextureRegion>(
        0.15f,
        frames[0][5]
    );

    // Fila 1 = espalda
    idleUp = new Animation<TextureRegion>(
        0.15f,
        frames[1][5]
    );

    // Fila 2 = izquierda
    idleLeft = new Animation<TextureRegion>(
        0.15f,
        frames[2][5]
    );

    // Fila 3 = derecha
    idleRight = new Animation<TextureRegion>(
        0.15f,
        frames[3][5]
    );

    // =====================================================
    // CAMINAR
    // =====================================================

    walkDown = new Animation<TextureRegion>(
        0.10f,
        frames[0][0],
        frames[0][1],
        frames[0][2],
        frames[0][3],
        frames[0][4]
    );

    walkUp = new Animation<TextureRegion>(
        0.10f,
        frames[1][0],
        frames[1][1],
        frames[1][2],
        frames[1][3],
        frames[1][4]
    );

    walkLeft = new Animation<TextureRegion>(
        0.10f,
        frames[2][0],
        frames[2][1],
        frames[2][2],
        frames[2][3],
        frames[2][4]
    );

    walkRight = new Animation<TextureRegion>(
        0.10f,
        frames[3][0],
        frames[3][1],
        frames[3][2],
        frames[3][3],
        frames[3][4]
    );

    // =====================================================
    // PREPARAR ATAQUE
    // =====================================================

    /*
     * Columna 5:
     * postura de preparación.
     *
     * Es estática porque el Guardian pasa
     * 0.8 segundos preparando el ataque.
     */

    prepararDown = new Animation<TextureRegion>(
        0.10f,
        frames[0][5]
    );

    prepararUp = new Animation<TextureRegion>(
        0.10f,
        frames[1][5]
    );

    prepararLeft = new Animation<TextureRegion>(
        0.10f,
        frames[2][5]
    );

    prepararRight = new Animation<TextureRegion>(
        0.10f,
        frames[3][5]
    );

    // =====================================================
    // ATACAR
    // =====================================================

    /*
     * Columnas 6 y 7:
     * ejecución y recuperación del ataque.
     */

    atacarDown = new Animation<TextureRegion>(
        0.07f,
        frames[0][6],
        frames[0][7]
    );

    atacarUp = new Animation<TextureRegion>(
        0.07f,
        frames[1][6],
        frames[1][7]
    );

    atacarLeft = new Animation<TextureRegion>(
        0.07f,
        frames[2][6],
        frames[2][7]
    );

    atacarRight = new Animation<TextureRegion>(
        0.07f,
        frames[3][6],
        frames[3][7]
    );

    // =====================================================
    // RECUPERACIÓN
    // =====================================================

    recuperarDown = new Animation<TextureRegion>(
        0.10f,
        frames[0][7]
    );

    recuperarUp = new Animation<TextureRegion>(
        0.10f,
        frames[1][7]
    );

    recuperarLeft = new Animation<TextureRegion>(
        0.10f,
        frames[2][7]
    );

    recuperarRight = new Animation<TextureRegion>(
        0.10f,
        frames[3][7]
    );
}

// =====================================================
// ACTUALIZAR
// =====================================================

public void actualizar(float delta) {

    stateTime += delta;
}

// =====================================================
// OBTENER FRAME
// =====================================================

public TextureRegion getFrame(
        Animation<TextureRegion> animacion) {

    return animacion.getKeyFrame(
        stateTime,
        true
    );
}

// =====================================================
// IDLE
// =====================================================

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

// =====================================================
// CAMINAR
// =====================================================

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

// =====================================================
// PREPARAR ATAQUE
// =====================================================

public Animation<TextureRegion> getPrepararDown() {
    return prepararDown;
}

public Animation<TextureRegion> getPrepararUp() {
    return prepararUp;
}

public Animation<TextureRegion> getPrepararLeft() {
    return prepararLeft;
}

public Animation<TextureRegion> getPrepararRight() {
    return prepararRight;
}

// =====================================================
// ATACAR
// =====================================================

public Animation<TextureRegion> getAtacarDown() {
    return atacarDown;
}

public Animation<TextureRegion> getAtacarUp() {
    return atacarUp;
}

public Animation<TextureRegion> getAtacarLeft() {
    return atacarLeft;
}

public Animation<TextureRegion> getAtacarRight() {
    return atacarRight;
}

// =====================================================
// RECUPERACIÓN
// =====================================================

public Animation<TextureRegion> getRecuperarDown() {
    return recuperarDown;
}

public Animation<TextureRegion> getRecuperarUp() {
    return recuperarUp;
}

public Animation<TextureRegion> getRecuperarLeft() {
    return recuperarLeft;
}

public Animation<TextureRegion> getRecuperarRight() {
    return recuperarRight;
}

// =====================================================
// DISPOSE
// =====================================================

public void dispose() {

    if (sheetTexture != null) {
        sheetTexture.dispose();
    }
}


}
