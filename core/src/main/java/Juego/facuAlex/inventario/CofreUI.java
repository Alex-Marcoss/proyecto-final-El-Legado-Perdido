package Juego.facuAlex.inventario;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import Juego.facuAlex.Proporcion;
import Juego.facuAlex.estructuras.Cofre;
import Juego.facuAlex.jugador.Jugador;
import Juego.facuAlex.recursos.Item;
import Juego.facuAlex.recursos.Recursos;

public class CofreUI {

    // Resolución virtual (la misma que usa InventarioUI)
    private static final float ANCHO_VIRTUAL = 900f;
    private static final float ALTO_VIRTUAL = 600f;

    private static final int COLUMNAS = 10;
    private static final int FILAS = 3;

    private static final float TAM = 56f;       // lado del slot
    private static final float GAP = 6f;        // espacio entre slots
    private static final float PAD = 18f;       // margen interno del panel
    private static final float ALTO_TITULO = 28f;
    private static final float SEPARACION = 16f; // entre los dos paneles

    private static final float ANCHO_PANEL =
            COLUMNAS * TAM + (COLUMNAS - 1) * GAP + PAD * 2f;

    private static final float ALTO_PANEL =
            FILAS * TAM + (FILAS - 1) * GAP + PAD * 2f + ALTO_TITULO;

    private static final float PANEL_X = (ANCHO_VIRTUAL - ANCHO_PANEL) / 2f;

    private static final float Y_JUGADOR =
            (ALTO_VIRTUAL - (ALTO_PANEL * 2f + SEPARACION)) / 2f;

    private static final float Y_COFRE =
            Y_JUGADOR + ALTO_PANEL + SEPARACION;

    private static final int PANEL_COFRE = 0;
    private static final int PANEL_JUGADOR = 1;

    private static final float PORCENTAJE_ICONO = 0.70f;
    private static final float MARGEN_CANTIDAD = 3f;

    private final Jugador jugador;
    private final SpriteBatch batch;
    private final Texture pixel;
    private final BitmapFont fuente;
    private final GlyphLayout layout = new GlyphLayout();

    private static final Map<Texture, TextureRegion> cacheIconos =
            new HashMap<Texture, TextureRegion>();

    private Cofre cofre;
    private boolean abierto;

    // Arrastre
    private boolean mousePrevio;
    private boolean arrastrando;
    private int panelOrigen;
    private int slotOrigen;
    private float mouseX;
    private float mouseY;

    public CofreUI(Jugador jugador) {

        this.jugador = jugador;

        batch = new SpriteBatch();
        fuente = new BitmapFont();

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        pixel = new Texture(pm);
        pm.dispose();
    }

    // =====================================================
    // ABRIR / CERRAR
    // =====================================================

    public void abrir(Cofre cofre) {

        this.cofre = cofre;
        abierto = true;
        arrastrando = false;
        mousePrevio = true; // evita tomar como click el que ya estaba apretado
    }

    public void cerrar() {

        abierto = false;
        arrastrando = false;
        cofre = null;
    }

    public boolean estaAbierto() {
        return abierto;
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================

    public void actualizar() {

        if (!abierto) {
            mousePrevio = false;
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)
                || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
                || Gdx.input.isKeyJustPressed(Input.Keys.I)) {

            cerrar();
            return;
        }

        boolean presionado = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        mouseX = Proporcion.mouseX();
        mouseY = Proporcion.mouseY();

        // ---------- CLICK INICIAL ----------
        if (presionado && !mousePrevio) {

            boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                    || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

            for (int p = 0; p < 2; p++) {

                int slot = slotBajoMouse(p, mouseX, mouseY);

                if (slot != -1 && inventarioDe(p).getItem(slot) != null) {

                    if (shift) {
                        transferirRapido(p, slot);
                    } else {
                        arrastrando = true;
                        panelOrigen = p;
                        slotOrigen = slot;
                    }

                    break;
                }
            }
        }

        // ---------- SOLTAR ----------
        if (!presionado && mousePrevio && arrastrando) {

            for (int p = 0; p < 2; p++) {

                int slot = slotBajoMouse(p, mouseX, mouseY);

                if (slot != -1) {
                    transferir(panelOrigen, slotOrigen, p, slot);
                    break;
                }
            }

            arrastrando = false;
        }

        mousePrevio = presionado;
    }

    // =====================================================
    // TRANSFERENCIAS
    // =====================================================

    private inventario inventarioDe(int panel) {

        return panel == PANEL_COFRE
                ? cofre.getInventarioCofre()
                : jugador.getInventario();
    }

    private void transferir(int pOrigen, int sOrigen,
                            int pDestino, int sDestino) {

        inventario origen = inventarioDe(pOrigen);
        inventario destino = inventarioDe(pDestino);

        // Dentro del mismo inventario: mover o intercambiar
        if (pOrigen == pDestino) {

            if (sOrigen != sDestino) {
                origen.moverItem(sOrigen, sDestino);
            }

            return;
        }

        Item item = origen.getItem(sOrigen);

        if (item == null) {
            return;
        }

        // Mismo recurso en el otro lado: se apilan
        if (item instanceof Recursos) {

            Recursos recurso = (Recursos) item;
            Recursos existente = destino.buscarRecurso(recurso.getNombre());

            if (existente != null) {
                existente.agregarCantidad(recurso.getCantidad());
                origen.quitarItem(sOrigen);
                return;
            }
        }

        Item enDestino = destino.getItem(sDestino);

        // Slot vacío: se coloca ahí
        if (enDestino == null) {
            origen.quitarItem(sOrigen);
            destino.ponerItem(sDestino, item);
            return;
        }

        // Slot ocupado por otra cosa: se intercambian
        origen.quitarItem(sOrigen);
        destino.quitarItem(sDestino);
        origen.ponerItem(sOrigen, enDestino);
        destino.ponerItem(sDestino, item);
    }

    // Shift + click: manda el item al otro inventario
    private void transferirRapido(int panel, int slot) {

        inventario origen = inventarioDe(panel);
        inventario destino = inventarioDe(1 - panel);

        Item item = origen.getItem(slot);

        if (item == null) {
            return;
        }

        if (item instanceof Recursos) {

            Recursos recurso = (Recursos) item;
            Recursos existente = destino.buscarRecurso(recurso.getNombre());

            if (existente != null) {
                existente.agregarCantidad(recurso.getCantidad());
                origen.quitarItem(slot);
                return;
            }
        }

        int libre = destino.primerSlotLibre();

        if (libre == -1) {
            System.out.println("No hay espacio en el otro inventario.");
            return;
        }

        origen.quitarItem(slot);
        destino.ponerItem(libre, item);
    }

    // =====================================================
    // GEOMETRÍA
    // =====================================================

    private float panelY(int panel) {
        return panel == PANEL_COFRE ? Y_COFRE : Y_JUGADOR;
    }

    private float slotX(int i) {
        return PANEL_X + PAD + (i % COLUMNAS) * (TAM + GAP);
    }

    private float slotY(int panel, int i) {

        float tope = panelY(panel) + ALTO_PANEL - PAD - ALTO_TITULO;
        int fila = i / COLUMNAS;

        return tope - (fila + 1) * TAM - fila * GAP;
    }

    private int slotBajoMouse(int panel, float mx, float my) {

        float margen = GAP / 2f;
        int total = Math.min(COLUMNAS * FILAS,
                inventarioDe(panel).getCantidadSlots());

        for (int i = 0; i < total; i++) {

            float x = slotX(i);
            float y = slotY(panel, i);

            if (mx >= x - margen && mx <= x + TAM + margen
                    && my >= y - margen && my <= y + TAM + margen) {

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

        batch.getProjectionMatrix()
                .setToOrtho2D(0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        batch.begin();

        // Fondo atenuado
        batch.setColor(0f, 0f, 0f, 0.55f);
        batch.draw(pixel, 0f, 0f, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        dibujarPanel(PANEL_COFRE, "Cofre");
        dibujarPanel(PANEL_JUGADOR, "Inventario");

        // Nombre del item bajo el mouse
        if (!arrastrando) {
            dibujarNombreBajoMouse();
        }

        // Item arrastrado, por encima de todo
        if (arrastrando) {

            Item item = inventarioDe(panelOrigen).getItem(slotOrigen);

            if (item != null) {
                dibujarIcono(item, mouseX, mouseY, 0.85f);
                dibujarCantidad(item, mouseX - TAM / 2f, mouseY - TAM / 2f);
            }
        }

        // Ayuda
        batch.setColor(Color.WHITE);

        String ayuda = "Arrastra para mover  |  Shift + click: pasar al otro "
                + "inventario  |  E / ESC: cerrar";

        layout.setText(fuente, ayuda);
        fuente.draw(
                batch,
                ayuda,
                (ANCHO_VIRTUAL - layout.width) / 2f,
                ALTO_VIRTUAL - 18f
        );

        batch.end();
    }

    private void dibujarPanel(int panel, String titulo) {

        float py = panelY(panel);

        // Borde y fondo
        batch.setColor(0.42f, 0.32f, 0.22f, 1f);
        batch.draw(pixel, PANEL_X - 3f, py - 3f,
                ANCHO_PANEL + 6f, ALTO_PANEL + 6f);

        batch.setColor(0.09f, 0.08f, 0.08f, 0.98f);
        batch.draw(pixel, PANEL_X, py, ANCHO_PANEL, ALTO_PANEL);

        // Título
        batch.setColor(Color.WHITE);
        fuente.draw(batch, titulo, PANEL_X + PAD, py + ALTO_PANEL - 10f);

        inventario inv = inventarioDe(panel);
        int total = Math.min(COLUMNAS * FILAS, inv.getCantidadSlots());

        int hover = slotBajoMouse(panel, mouseX, mouseY);

        for (int i = 0; i < total; i++) {

            float x = slotX(i);
            float y = slotY(panel, i);

            // Slot
            batch.setColor(0.30f, 0.25f, 0.20f, 1f);
            batch.draw(pixel, x - 1f, y - 1f, TAM + 2f, TAM + 2f);

            batch.setColor(0.15f, 0.13f, 0.12f, 1f);
            batch.draw(pixel, x, y, TAM, TAM);

            if (i == hover) {
                batch.setColor(1f, 1f, 1f, 0.15f);
                batch.draw(pixel, x, y, TAM, TAM);
            }

            // El que se está arrastrando no se dibuja en su slot
            if (arrastrando && panel == panelOrigen && i == slotOrigen) {
                continue;
            }

            Item item = inv.getItem(i);

            if (item == null) {
                continue;
            }

            dibujarIcono(item, x + TAM / 2f, y + TAM / 2f, 1f);
            dibujarCantidad(item, x, y);
        }

        batch.setColor(Color.WHITE);
    }

    private void dibujarNombreBajoMouse() {

        for (int p = 0; p < 2; p++) {

            int slot = slotBajoMouse(p, mouseX, mouseY);

            if (slot == -1) {
                continue;
            }

            Item item = inventarioDe(p).getItem(slot);

            if (item == null) {
                return;
            }

            layout.setText(fuente, item.getNombre());

            float x = mouseX + 14f;
            float y = mouseY + 8f;

            batch.setColor(0f, 0f, 0f, 0.85f);
            batch.draw(pixel, x - 4f, y - layout.height - 4f,
                    layout.width + 8f, layout.height + 8f);

            batch.setColor(Color.WHITE);
            fuente.draw(batch, item.getNombre(), x, y);

            return;
        }
    }

    private void dibujarIcono(Item item, float cx, float cy, float alpha) {

        Texture textura = item.getIcono();

        if (textura == null) {
            return;
        }

        TextureRegion region = obtenerRegionRecortada(textura);

        float escala = Math.min(
                TAM * PORCENTAJE_ICONO / region.getRegionWidth(),
                TAM * PORCENTAJE_ICONO / region.getRegionHeight()
        );

        float ancho = region.getRegionWidth() * escala;
        float alto = region.getRegionHeight() * escala;

        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(region, cx - ancho / 2f, cy - alto / 2f, ancho, alto);
        batch.setColor(Color.WHITE);
    }

    private void dibujarCantidad(Item item, float slotX, float slotY) {

        if (!(item instanceof Recursos)) {
            return;
        }

        int cantidad = ((Recursos) item).getCantidad();

        if (cantidad <= 0) {
            return;
        }

        String texto = String.valueOf(cantidad);
        layout.setText(fuente, texto);

        fuente.draw(
                batch,
                texto,
                slotX + TAM - layout.width - MARGEN_CANTIDAD,
                slotY + MARGEN_CANTIDAD + layout.height
        );
    }

    // Recorta el icono a su parte visible (igual que InventarioUI)
    static TextureRegion obtenerRegionRecortada(Texture textura) {

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
                textura, minX, minY,
                maxX - minX + 1, maxY - minY + 1
        );

        if (data.disposePixmap()) {
            pixmap.dispose();
        }

        cacheIconos.put(textura, region);

        return region;
    }

    public void dispose() {

        batch.dispose();
        pixel.dispose();
        fuente.dispose();
    }
}