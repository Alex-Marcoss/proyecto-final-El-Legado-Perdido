package Juego.facuAlex;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import Juego.facuAlex.recursos.Item;
import Juego.facuAlex.recursos.Recursos;

public class InventarioUI {

    private Jugador jugador;

    private SpriteBatch batchInventario;

    private Texture panelTexture;
    private Texture slotTexture;

    private BitmapFont fuenteCantidad;
    private GlyphLayout layoutCantidad;

    private boolean abierto;
    private boolean mousePresionadoAnteriormente = false;

    private int slotSeleccionado;

    // =====================================================
    // DRAG & DROP
    // =====================================================

    private boolean arrastrando;
    private int slotArrastrado;
    private float mouseArrastreX;
    private float mouseArrastreY;

    // =====================================================
    // RESOLUCIÓN VIRTUAL
    // =====================================================

    private static final float ANCHO_VIRTUAL = 900f;
    private static final float ALTO_VIRTUAL = 600f;

    // =====================================================
    // INVENTARIO
    // =====================================================

    private static final int CANTIDAD_SLOTS = 30;
    private static final int COLUMNAS = 10;
    private static final int FILAS = 3;

    // =====================================================
    // PANEL
    // =====================================================

    private static final float ANCHO_PANEL = 850f;
    private float altoPanel;

    // =====================================================
    // GRILLA (fracciones del panel, medidas desde ARRIBA)
    // =====================================================

    private static final float GRILLA_IZQ = 0.094f;
    private static final float GRILLA_DER = 0.913f;
    private static final float GRILLA_SUP = 0.300f;
    private static final float GRILLA_INF = 0.822f;

    private static final float ESPACIO = 8f;

    // =====================================================
    // ICONOS Y CANTIDAD
    // =====================================================

    private static final float PORCENTAJE_ICONO = 0.70f;
    private static final float MARGEN_CANTIDAD = 4f;

    private final Map<Texture, TextureRegion> cacheIconos = new HashMap<>();

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public InventarioUI(Jugador jugador) {

        this.jugador = jugador;

        batchInventario = new SpriteBatch();

        panelTexture = new Texture(Gdx.files.internal("inventario/panel.png"));
        slotTexture = new Texture(Gdx.files.internal("inventario/slot.png"));

        panelTexture.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
        );

        slotTexture.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
        );

        altoPanel = ANCHO_PANEL * panelTexture.getHeight() / panelTexture.getWidth();

        fuenteCantidad = new BitmapFont();
        layoutCantidad = new GlyphLayout();

        fuenteCantidad.getRegion().getTexture().setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
        );

        abierto = false;
        slotSeleccionado = -1;
        arrastrando = false;
        slotArrastrado = -1;
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================

    public void actualizar() {

        // Abrir / cerrar
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {

            abierto = !abierto;

            if (!abierto) {
                arrastrando = false;
                slotArrastrado = -1;
                slotSeleccionado = -1;
            }
        }

        if (!abierto) {
            mousePresionadoAnteriormente = false;
            return;
        }

        boolean mousePresionado = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        float mouseX = convertirMouseX();
        float mouseY = convertirMouseY();

        // ---------- CLICK INICIAL ----------
        if (mousePresionado && !mousePresionadoAnteriormente) {

            int slot = obtenerSlotDesdeMouse(mouseX, mouseY);

            if (slot != -1) {

                Item item = jugador.getInventario().getItem(slot);

                if (item != null) {

                    slotSeleccionado = slot;

                    // Empezamos a arrastrar
                    arrastrando = true;
                    slotArrastrado = slot;

                    
                }
            }
        }

        // ---------- MIENTRAS SE ARRASTRA ----------
        if (mousePresionado && arrastrando) {
            mouseArrastreX = mouseX;
            mouseArrastreY = mouseY;
        }

        // ---------- SOLTAR ----------
        if (!mousePresionado && mousePresionadoAnteriormente && arrastrando) {

            int slotDestino = obtenerSlotDesdeMouse(mouseX, mouseY);

            if (slotDestino != -1 && slotDestino != slotArrastrado) {

                jugador.getInventario().moverItem(slotArrastrado, slotDestino);

                slotSeleccionado = slotDestino;
            }

            arrastrando = false;
            slotArrastrado = -1;
        }

        mousePresionadoAnteriormente = mousePresionado;
    }

    // =====================================================
    // CONVERTIR MOUSE
    // =====================================================

    private float convertirMouseX() {
        return Gdx.input.getX() * ANCHO_VIRTUAL / Gdx.graphics.getWidth();
    }

    private float convertirMouseY() {
        return (Gdx.graphics.getHeight() - Gdx.input.getY())
                * ALTO_VIRTUAL / Gdx.graphics.getHeight();
    }

    // =====================================================
    // GEOMETRÍA
    // =====================================================

    private float panelX() {
        return (ANCHO_VIRTUAL - ANCHO_PANEL) / 2f;
    }

    private float panelY() {
        return (ALTO_VIRTUAL - altoPanel) / 2f;
    }

    private float anchoSlot() {
        float anchoGrilla = ANCHO_PANEL * (GRILLA_DER - GRILLA_IZQ);
        return (anchoGrilla - ESPACIO * (COLUMNAS - 1)) / COLUMNAS;
    }

    private float altoSlot() {
        float altoGrilla = altoPanel * (GRILLA_INF - GRILLA_SUP);
        return (altoGrilla - ESPACIO * (FILAS - 1)) / FILAS;
    }

    private float obtenerSlotX(int columna) {
        return panelX()
                + ANCHO_PANEL * GRILLA_IZQ
                + columna * (anchoSlot() + ESPACIO);
    }

    private float obtenerSlotY(int fila) {
        // fila 0 = la de arriba
        float topeGrilla = panelY() + altoPanel * (1f - GRILLA_SUP);
        return topeGrilla - (fila + 1) * altoSlot() - fila * ESPACIO;
    }

    // =====================================================
    // SLOT BAJO EL MOUSE
    // Usa EXACTAMENTE la misma geometría con la que se dibuja,
    // y cubre la mitad del espacio entre slots para que soltar
    // un objeto en un borde no falle.
    // =====================================================

    private int obtenerSlotDesdeMouse(float mouseX, float mouseY) {

        float margen = ESPACIO / 2f;

        for (int i = 0; i < CANTIDAD_SLOTS; i++) {

            float x = obtenerSlotX(i % COLUMNAS);
            float y = obtenerSlotY(i / COLUMNAS);

            if (mouseX >= x - margen && mouseX <= x + anchoSlot() + margen
                    && mouseY >= y - margen && mouseY <= y + altoSlot() + margen) {

                return i;
            }
        }

        return -1;
    }

    // =====================================================
    // DIBUJAR
    // =====================================================

    public void dibujar() {

        if (!abierto) {
            return;
        }

        batchInventario.getProjectionMatrix()
                .setToOrtho2D(0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        batchInventario.begin();
        batchInventario.setColor(1f, 1f, 1f, 1f);

        // Panel
        batchInventario.draw(panelTexture, panelX(), panelY(), ANCHO_PANEL, altoPanel);

        // Slots
        for (int i = 0; i < CANTIDAD_SLOTS; i++) {

            float x = obtenerSlotX(i % COLUMNAS);
            float y = obtenerSlotY(i / COLUMNAS);

            // Slot seleccionado
            if (i == slotSeleccionado) {

                batchInventario.setColor(1f, 1f, 1f, 0.30f);
                batchInventario.draw(slotTexture, x, y, anchoSlot(), altoSlot());
                batchInventario.setColor(1f, 1f, 1f, 1f);
            }

            // El objeto que se está arrastrando no se dibuja en su slot
            if (arrastrando && i == slotArrastrado) {
                continue;
            }

            Item item = jugador.getInventario().getItem(i);

            if (item == null) {
                continue;
            }

            dibujarIcono(
                    item,
                    x + anchoSlot() / 2f,
                    y + altoSlot() / 2f,
                    1f
            );

            if (item instanceof Recursos) {
                dibujarCantidad((Recursos) item, x, y);
            }
        }

        // Objeto arrastrado (encima de todo)
        if (arrastrando && slotArrastrado != -1) {

            Item item = jugador.getInventario().getItem(slotArrastrado);

            if (item != null) {

                dibujarIcono(item, mouseArrastreX, mouseArrastreY, 0.85f);

                if (item instanceof Recursos) {
                    dibujarCantidad(
                            (Recursos) item,
                            mouseArrastreX - anchoSlot() / 2f,
                            mouseArrastreY - altoSlot() / 2f
                    );
                }
            }
        }

        batchInventario.setColor(1f, 1f, 1f, 1f);
        batchInventario.end();
    }

    // =====================================================
    // DIBUJAR ICONO (centrado en un punto)
    // =====================================================

    private void dibujarIcono(Item item, float centroX, float centroY, float alpha) {

        Texture textura = item.getIcono();

        if (textura == null) {
            return;
        }

        TextureRegion region = obtenerRegionRecortada(textura);

        float maxAncho = anchoSlot() * PORCENTAJE_ICONO;
        float maxAlto = altoSlot() * PORCENTAJE_ICONO;

        float escala = Math.min(
                maxAncho / region.getRegionWidth(),
                maxAlto / region.getRegionHeight()
        );

        float ancho = region.getRegionWidth() * escala;
        float alto = region.getRegionHeight() * escala;

        batchInventario.setColor(1f, 1f, 1f, alpha);

        batchInventario.draw(
                region,
                centroX - ancho / 2f,
                centroY - alto / 2f,
                ancho,
                alto
        );

        batchInventario.setColor(1f, 1f, 1f, 1f);
    }

    // =====================================================
    // DIBUJAR CANTIDAD (esquina inferior derecha del slot)
    // =====================================================

    private void dibujarCantidad(Recursos recurso, float slotX, float slotY) {

        int cantidad = recurso.getCantidad();

        if (cantidad <= 0) {
            return;
        }

        String texto = String.valueOf(cantidad);

        layoutCantidad.setText(fuenteCantidad, texto);

        float x = slotX + anchoSlot() - layoutCantidad.width - MARGEN_CANTIDAD;
        float y = slotY + MARGEN_CANTIDAD + layoutCantidad.height;

        fuenteCantidad.draw(batchInventario, texto, x, y);
    }

    // =====================================================
    // RECORTAR TEXTURA A SU PARTE VISIBLE
    // =====================================================

    private TextureRegion obtenerRegionRecortada(Texture textura) {

        TextureRegion cacheada = cacheIconos.get(textura);

        if (cacheada != null) {
            return cacheada;
        }

        TextureData data = textura.getTextureData();

        if (!data.isPrepared()) {
            data.prepare();
        }

        Pixmap pixmap = data.consumePixmap();

        int minX = pixmap.getWidth();
        int minY = pixmap.getHeight();
        int maxX = -1;
        int maxY = -1;

        for (int py = 0; py < pixmap.getHeight(); py++) {
            for (int px = 0; px < pixmap.getWidth(); px++) {

                int alpha = pixmap.getPixel(px, py) & 0xff;

                if (alpha > 10) {
                    if (px < minX) minX = px;
                    if (px > maxX) maxX = px;
                    if (py < minY) minY = py;
                    if (py > maxY) maxY = py;
                }
            }
        }

        if (maxX < 0) {
            minX = 0;
            minY = 0;
            maxX = pixmap.getWidth() - 1;
            maxY = pixmap.getHeight() - 1;
        }

        TextureRegion region = new TextureRegion(
                textura,
                minX,
                minY,
                maxX - minX + 1,
                maxY - minY + 1
        );

        if (data.disposePixmap()) {
            pixmap.dispose();
        }

        cacheIconos.put(textura, region);

        return region;
    }

    // =====================================================
    // ESTADO
    // =====================================================

    public boolean estaAbierto() {
        return abierto;
    }

    public int getSlotSeleccionado() {
        return slotSeleccionado;
    }

    // =====================================================
    // DISPOSE
    // =====================================================

    public void dispose() {

        batchInventario.dispose();
        panelTexture.dispose();
        slotTexture.dispose();
        fuenteCantidad.dispose();
    }
}