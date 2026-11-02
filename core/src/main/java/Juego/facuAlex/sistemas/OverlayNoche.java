package Juego.facuAlex.sistemas;

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.math.Matrix4;

public class OverlayNoche {

    // --- Ajustes ---
    private static final float RADIO_DIA = 1000f;
    private static final float RADIO_NOCHE = 170f;     // visión del jugador
    private static final float ALPHA_OSCURIDAD = 0.96f; // oscuridad máxima
    private static final float LUZ_JUGADOR = 0.6f;      // 0..1
    private static final float LUZ_FOGATA = 0.92f;      // 0..1
    private static final float BRILLO_CALIDO = 0.15f;   // resplandor naranja
    private static final float ESCALA_MAPA_LUZ = 0.5f;  // 0.5 = mitad de resolución
    private final Matrix4 matrizPantalla = new Matrix4();
    private static final float R = 0.02f, G = 0.03f, B = 0.10f;

    private final Texture luz;
    private final Texture pixel;
    private FrameBuffer mapaLuz;
    private int mapaW, mapaH;

    private final IntBuffer viewport = BufferUtils.newIntBuffer(16);
    
    
    
    public OverlayNoche() {

        int t = 256;
        float c = t / 2f;

        Pixmap pm = new Pixmap(t, t, Pixmap.Format.RGBA8888);
        pm.setBlending(Pixmap.Blending.None);

        for (int y = 0; y < t; y++) {
            for (int x = 0; x < t; x++) {
                float d = (float) Math.hypot(x - c, y - c) / c;
                float k = MathUtils.clamp((d - 0.1f) / 0.9f, 0f, 1f);
                float alpha = 1f - k * k * (3f - 2f * k);   // 1 al centro, 0 en el borde
                pm.drawPixel(x, y, Color.rgba8888(1f, 1f, 1f, alpha));
            }
        }

        luz = new Texture(pm);
        luz.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pm.dispose();

        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(Color.WHITE);
        p.fill();
        pixel = new Texture(p);
        p.dispose();
    }

    private void prepararMapaLuz(int vw, int vh) {

    int w = Math.max(16, Math.round(vw * ESCALA_MAPA_LUZ));
    int h = Math.max(16, Math.round(vh * ESCALA_MAPA_LUZ));

    if (mapaLuz != null && w == mapaW && h == mapaH) {
        return;
    }

    if (mapaLuz != null) {
        mapaLuz.dispose();
    }

    mapaLuz = new FrameBuffer(Pixmap.Format.RGBA8888, w, h, false);
    mapaLuz.getColorBufferTexture().setFilter(
            Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    mapaW = w;
    mapaH = h;
    }

    public void dibujar(SpriteBatch batch, OrthographicCamera cam,
                    float cx, float cy, float oscuridad) {

    if (oscuridad <= 0.01f) {
        Luces.limpiar();
        return;
    }

    // Viewport real en este momento (el que usa el juego en pantalla)
    viewport.clear();
    Gdx.gl.glGetIntegerv(GL20.GL_VIEWPORT, viewport);
    int vx = viewport.get(0), vy = viewport.get(1);
    int vw = viewport.get(2), vh = viewport.get(3);

    prepararMapaLuz(vw, vh);

    float anchoVista = cam.viewportWidth * cam.zoom;
    float altoVista = cam.viewportHeight * cam.zoom;
    float izq = cam.position.x - anchoVista / 2f;
    float aba = cam.position.y - altoVista / 2f;
    
 // El scissor de Proporcion está pensado para la pantalla, no para esta textura
    boolean scissor = Gdx.gl.glIsEnabled(GL20.GL_SCISSOR_TEST);
    if (scissor) {
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
    }
    
    // ---------- 1) Mapa de luz ----------
    mapaLuz.begin();
    Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.setProjectionMatrix(cam.combined);
    batch.begin();

    batch.setBlendFunctionSeparate(
            GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,
            GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);

    batch.setColor(R, G, B, oscuridad * ALPHA_OSCURIDAD);
    batch.draw(pixel, izq, aba, anchoVista, altoVista);

    batch.setBlendFunctionSeparate(
            GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA,
            GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);

    float radio = MathUtils.lerp(RADIO_DIA, RADIO_NOCHE, oscuridad);
    batch.setColor(1f, 1f, 1f, LUZ_JUGADOR);
    dibujarLuz(batch, cx, cy, radio);

    for (Luces.Luz l : Luces.getLuces()) {
        batch.setColor(1f, 1f, 1f, l.intensidad * LUZ_FOGATA);
        dibujarLuz(batch, l.x, l.y, l.radio);
    }

    batch.end();
    mapaLuz.end(vx, vy, vw, vh);
    
    if (scissor) {
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
    }
    
    // ---------- 2) Pegado en coordenadas de pantalla ----------
    // (0,0)-(1,1) ocupa exactamente todo el viewport actual
    matrizPantalla.setToOrtho2D(0f, 0f, 1f, 1f);
    batch.setProjectionMatrix(matrizPantalla);
    batch.begin();

    batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
    batch.setColor(Color.WHITE);
    batch.draw(mapaLuz.getColorBufferTexture(),
            0f, 0f, 1f, 1f, 0f, 0f, 1f, 1f);

    // ---------- 3) Resplandor cálido (de nuevo en coordenadas del mundo) ----------
    batch.setProjectionMatrix(cam.combined);
    batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

    for (Luces.Luz l : Luces.getLuces()) {
        float intensidadBase = oscuridad * l.intensidad * BRILLO_CALIDO;

        // 1. Halo exterior (mantiene el radio original, más suave)
        batch.setColor(1f, 0.62f, 0.28f, intensidadBase);
        dibujarLuz(batch, l.x, l.y, l.radio * 0.8f);

        // 2. Núcleo interior de la fogata (más pequeño, intenso y con un toque más amarillento/blanco)
        batch.setColor(1f, 0.85f, 0.5f, intensidadBase * 1.7f);
        dibujarLuz(batch, l.x, l.y, l.radio * 0.35f);
    }

    batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    batch.setColor(Color.WHITE);
    batch.end();

    Luces.limpiar();
}

    private void dibujarLuz(SpriteBatch batch, float x, float y, float radio) {
        batch.draw(luz, x - radio, y - radio, radio * 2f, radio * 2f);
    }

    public void dispose() {
        luz.dispose();
        pixel.dispose();
        if (mapaLuz != null) {
            mapaLuz.dispose();
        }
    }
}