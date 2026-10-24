
package Juego.facuAlex.estructuras;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Estructura {

    protected float x;
    protected float y;
    protected float ancho;
    protected float alto;

    protected Texture textura;
    protected String nombre;

    protected Estructura(
            String nombre,
            String rutaTextura,
            float x,
            float y,
            float ancho,
            float alto) {

        this.nombre = nombre;
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;

        textura = new Texture(rutaTextura);
        textura.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
        );
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(textura, x, y, ancho, alto);
    }

    public boolean estaCerca(float jugadorX, float jugadorY) {
        float centroX = x + ancho / 2f;
        float centroY = y + alto / 2f;

        float dx = jugadorX + 32f - centroX;
        float dy = jugadorY + 32f - centroY;

        return dx * dx + dy * dy <= 130f * 130f;
    }

    public Rectangle getHitbox() {
        return new Rectangle(
                x + ancho * 0.15f,
                y,
                ancho * 0.70f,
                alto * 0.35f
        );
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getAncho() {
        return ancho;
    }

    public float getAlto() {
        return alto;
    }

    public String getNombre() {
        return nombre;
    }

    public void dispose() {
        if (textura != null) {
            textura.dispose();
        }
    }
}
