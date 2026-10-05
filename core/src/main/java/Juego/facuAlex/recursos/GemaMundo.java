package Juego.facuAlex.recursos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GemaMundo {

    private float posicionX;
    private float posicionY;

    private Texture textura;

    private boolean recogida;

    public GemaMundo(float posicionX, float posicionY) {

        this.posicionX = posicionX;
        this.posicionY = posicionY;

        this.textura = new Texture("objetos/gemaAzul.png");

        this.recogida = false;
    }

    public void dibujar(SpriteBatch batch) {

        if (recogida) return;

        batch.draw(
            textura,
            posicionX,
            posicionY,
            48,
            48
        );
    }

    public boolean estaCerca(float jugadorX, float jugadorY) {

        float diferenciaX = jugadorX - posicionX;
        float diferenciaY = jugadorY - posicionY;

        float distancia =
            (float) Math.sqrt(
                diferenciaX * diferenciaX +
                diferenciaY * diferenciaY
            );

        return distancia <= 70f;
    }

    public void recoger() {
        recogida = true;
    }

    public boolean estaRecogida() {
        return recogida;
    }

    public float getPosicionX() {
        return posicionX;
    }

    public float getPosicionY() {
        return posicionY;
    }

    public void dispose() {
        textura.dispose();
    }
}