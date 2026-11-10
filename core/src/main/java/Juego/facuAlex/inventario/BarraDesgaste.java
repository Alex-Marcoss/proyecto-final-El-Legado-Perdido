package Juego.facuAlex.inventario;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import Juego.facuAlex.Herramientas.Herramienta;

// Barra de desgaste de las herramientas (como en Minecraft):
// aparece en la parte de abajo del slot solo cuando la herramienta ya se uso,
// y va de verde (nueva) a rojo (por romperse).
public class BarraDesgaste {

    private static final float PORCENTAJE_ANCHO = 0.80f;
    private static final float ALTO = 3f;
    private static final float MARGEN_INFERIOR = 3f;

    private BarraDesgaste() {
    }

    // El batch tiene que estar iniciado.
    // pixelBlanco: textura de 1x1 pixel blanco.
    public static void dibujar(
            SpriteBatch batch,
            Texture pixelBlanco,
            Herramienta herramienta,
            float slotX,
            float slotY,
            float slotAncho) {

        if (herramienta == null || !herramienta.estaDesgastada()) {
            return;
        }

        float fraccion = herramienta.getFraccionDurabilidad();

        if (fraccion < 0f) {
            fraccion = 0f;
        }

        float ancho = slotAncho * PORCENTAJE_ANCHO;
        float x = slotX + (slotAncho - ancho) / 2f;
        float y = slotY + MARGEN_INFERIOR;

        // Fondo oscuro
        batch.setColor(0f, 0f, 0f, 0.9f);
        batch.draw(pixelBlanco, x - 1f, y - 1f, ancho + 2f, ALTO + 2f);

        // Verde -> amarillo -> rojo
        float r = fraccion > 0.5f ? (1f - fraccion) * 2f : 1f;
        float g = fraccion > 0.5f ? 1f : fraccion * 2f;

        batch.setColor(r, g, 0f, 1f);
        batch.draw(pixelBlanco, x, y, ancho * fraccion, ALTO);

        batch.setColor(1f, 1f, 1f, 1f);
    }
}