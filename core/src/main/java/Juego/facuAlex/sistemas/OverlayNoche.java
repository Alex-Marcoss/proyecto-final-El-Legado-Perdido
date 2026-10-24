package Juego.facuAlex.sistemas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class OverlayNoche {

	private static final float RADIO_DIA = 1000f;
	private static final float RADIO_NOCHE = 160f;
	private static final float ALPHA_AMBIENTE = 0.4f;  // oscurece todo parejo
	private static final float ALPHA_MAXIMO = 0.96f;   // oscuridad fuera de la luz

    private final Texture mascara;
    private final Texture pixel;

    public OverlayNoche() {
        int t = 256;
        float c = t / 2f;

        Pixmap pm = new Pixmap(t, t, Pixmap.Format.RGBA8888);
        pm.setBlending(Pixmap.Blending.None);

        for (int y = 0; y < t; y++) {
            for (int x = 0; x < t; x++) {
                float d = (float) Math.hypot(x - c, y - c) / c;
                float k = MathUtils.clamp((d - 0.3f) / 0.7f, 0f, 1f);
                float alpha = k * k * (3f - 2f * k);
                pm.drawPixel(x, y, Color.rgba8888(1f, 1f, 1f, alpha));
            }
        }

        mascara = new Texture(pm);
        mascara.setFilter(Texture.TextureFilter.Linear,
                          Texture.TextureFilter.Linear);
        pm.dispose();

        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(Color.WHITE);
        p.fill();
        pixel = new Texture(p);
        p.dispose();
    }

    public void dibujar(SpriteBatch batch, OrthographicCamera cam,
                    float cx, float cy, float oscuridad) {

    if (oscuridad <= 0.01f) return;

    float radio = MathUtils.lerp(RADIO_DIA, RADIO_NOCHE, oscuridad);

    float mitadW = cam.viewportWidth * cam.zoom / 2f + 50f;
    float mitadH = cam.viewportHeight * cam.zoom / 2f + 50f;
    float izq = cam.position.x - mitadW, der = cam.position.x + mitadW;
    float aba = cam.position.y - mitadH, arr = cam.position.y + mitadH;

    batch.setProjectionMatrix(cam.combined);
    batch.begin();

    // 1) Oscurecimiento general: empieza en cuanto cae el sol
    batch.setColor(0.02f, 0.03f, 0.10f, oscuridad * ALPHA_AMBIENTE);
    rect(batch, izq, aba, der - izq, arr - aba);

    // 2) Visión limitada alrededor del jugador
    batch.setColor(0.02f, 0.03f, 0.10f, oscuridad * ALPHA_MAXIMO);

    batch.draw(mascara, cx - radio, cy - radio, radio * 2f, radio * 2f);

    rect(batch, izq, cy + radio, der - izq, arr - (cy + radio));
    rect(batch, izq, aba, der - izq, (cy - radio) - aba);
    rect(batch, izq, cy - radio, (cx - radio) - izq, radio * 2f);
    rect(batch, cx + radio, cy - radio, der - (cx + radio), radio * 2f);

    batch.setColor(Color.WHITE);
    batch.end();
}
    private void rect(SpriteBatch batch, float x, float y, float w, float h) {
        if (w > 0f && h > 0f) batch.draw(pixel, x, y, w, h);
    }

    public void dispose() {
        mascara.dispose();
        pixel.dispose();
    }
}