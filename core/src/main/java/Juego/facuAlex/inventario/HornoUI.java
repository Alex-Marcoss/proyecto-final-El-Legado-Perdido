package Juego.facuAlex.inventario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import Juego.facuAlex.Proporcion;
import Juego.facuAlex.estructuras.Horno;
import Juego.facuAlex.jugador.Jugador;
import Juego.facuAlex.recursos.Item;
import Juego.facuAlex.recursos.Recursos;

public class HornoUI {

    private static final float ANCHO_VIRTUAL = 900f;
    private static final float ALTO_VIRTUAL = 600f;

    private static final int COLUMNAS = 10;
    private static final int FILAS = 3;

    private static final float TAM = 56f;
    private static final float GAP = 6f;
    private static final float PAD = 18f;
    private static final float ALTO_TITULO = 28f;
    private static final float SEPARACION = 16f;

    private static final float ANCHO_PANEL =
            COLUMNAS * TAM + (COLUMNAS - 1) * GAP + PAD * 2f;

    private static final float ALTO_PANEL_JUGADOR =
            FILAS * TAM + (FILAS - 1) * GAP + PAD * 2f + ALTO_TITULO;

    private static final float ALTO_PANEL_HORNO = 206f;

    private static final float PANEL_X = (ANCHO_VIRTUAL - ANCHO_PANEL) / 2f;

    private static final float Y_JUGADOR =
            (ALTO_VIRTUAL
                    - (ALTO_PANEL_HORNO + ALTO_PANEL_JUGADOR + SEPARACION))
            / 2f;

    private static final float Y_HORNO =
            Y_JUGADOR + ALTO_PANEL_JUGADOR + SEPARACION;

    // Posiciones de los slots del horno (relativas al panel)
    private static final float X_ENTRADA = PANEL_X + 180f;
    private static final float X_SALIDA = PANEL_X + 380f;

    private static final int PANEL_HORNO = 0;
    private static final int PANEL_JUGADOR = 1;

    private static final float PORCENTAJE_ICONO = 0.70f;
    private static final float MARGEN_CANTIDAD = 3f;

    private final Jugador jugador;
    private final SpriteBatch batch;
    private final Texture pixel;
    private final BitmapFont fuente;
    private final GlyphLayout layout = new GlyphLayout();

    private Horno horno;
    private boolean abierto;

    private boolean mousePrevio;
    private boolean arrastrando;
    private int panelOrigen;
    private int slotOrigen;
    private float mouseX;
    private float mouseY;

    public HornoUI(Jugador jugador) {

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

    public void abrir(Horno horno) {

        this.horno = horno;
        abierto = true;
        arrastrando = false;
        mousePrevio = true;
    }

    public void cerrar() {

        abierto = false;
        arrastrando = false;
        horno = null;
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

        return panel == PANEL_HORNO
                ? horno.getSlots()
                : jugador.getInventario();
    }

    private int cantidadSlots(int panel) {

        return panel == PANEL_HORNO
                ? 3
                : Math.min(COLUMNAS * FILAS,
                        jugador.getInventario().getCantidadSlots());
    }

    // ¿Se puede dejar este item en ese slot?
    private boolean puedeColocar(int panel, int slot, Item item) {

        if (panel == PANEL_HORNO) {
            return horno.acepta(slot, item);
        }

        return true;
    }

    private void transferir(int pOrigen, int sOrigen,
                            int pDestino, int sDestino) {

        inventario origen = inventarioDe(pOrigen);
        inventario destino = inventarioDe(pDestino);

        // Dentro del inventario del jugador: mover o intercambiar
        if (pOrigen == pDestino) {

            if (pOrigen == PANEL_JUGADOR && sOrigen != sDestino) {
                origen.moverItem(sOrigen, sDestino);
            }

            return;
        }

        Item item = origen.getItem(sOrigen);

        if (item == null || !puedeColocar(pDestino, sDestino, item)) {
            return;
        }

        Item enDestino = destino.getItem(sDestino);

        // Mismo recurso en el slot de destino: se suman
        if (item instanceof Recursos && enDestino instanceof Recursos
                && enDestino.getNombre().equals(item.getNombre())) {

            ((Recursos) enDestino)
                    .agregarCantidad(((Recursos) item).getCantidad());
            origen.quitarItem(sOrigen);
            return;
        }

        // Hacia el jugador: se apila con el mismo recurso de cualquier slot
        if (pDestino == PANEL_JUGADOR && item instanceof Recursos) {

            Recursos existente = destino.buscarRecurso(item.getNombre());

            if (existente != null) {
                existente.agregarCantidad(((Recursos) item).getCantidad());
                origen.quitarItem(sOrigen);
                return;
            }
        }

        if (enDestino == null) {
            origen.quitarItem(sOrigen);
            destino.ponerItem(sDestino, item);
            return;
        }

        // Intercambio: solo si lo que vuelve también cabe en su slot
        if (!puedeColocar(pOrigen, sOrigen, enDestino)) {
            return;
        }

        origen.quitarItem(sOrigen);
        destino.quitarItem(sDestino);
        origen.ponerItem(sOrigen, enDestino);
        destino.ponerItem(sDestino, item);
    }

    // Shift + click
    private void transferirRapido(int panel, int slot) {

        Item item = inventarioDe(panel).getItem(slot);

        if (item == null) {
            return;
        }

        if (panel == PANEL_HORNO) {

            // Al inventario: apila o va al primer slot libre
            int libre = jugador.getInventario().primerSlotLibre();

            transferir(PANEL_HORNO, slot, PANEL_JUGADOR, Math.max(libre, 0));
            return;
        }

     // Al horno
        boolean sirveEntrada = horno.acepta(Horno.SLOT_ENTRADA, item);
        boolean sirveCombustible = horno.acepta(Horno.SLOT_COMBUSTIBLE, item);

        if (sirveEntrada && sirveCombustible) {

            // Madera: el primer Shift + click la carga de combustible,
            // el siguiente la pone a cocinar
            boolean combustibleVacio =
                    horno.getSlots().getItem(Horno.SLOT_COMBUSTIBLE) == null;

            transferir(PANEL_JUGADOR, slot, PANEL_HORNO,
                    combustibleVacio ? Horno.SLOT_COMBUSTIBLE : Horno.SLOT_ENTRADA);

        } else if (sirveEntrada) {

            transferir(PANEL_JUGADOR, slot, PANEL_HORNO, Horno.SLOT_ENTRADA);

        } else if (sirveCombustible) {

            transferir(PANEL_JUGADOR, slot, PANEL_HORNO, Horno.SLOT_COMBUSTIBLE);
        }
    }

    // =====================================================
    // GEOMETRÍA
    // =====================================================

    private float panelY(int panel) {
        return panel == PANEL_HORNO ? Y_HORNO : Y_JUGADOR;
    }

    private float altoPanel(int panel) {
        return panel == PANEL_HORNO ? ALTO_PANEL_HORNO : ALTO_PANEL_JUGADOR;
    }

    private float slotX(int panel, int i) {

        if (panel == PANEL_HORNO) {
            return i == Horno.SLOT_SALIDA ? X_SALIDA : X_ENTRADA;
        }

        return PANEL_X + PAD + (i % COLUMNAS) * (TAM + GAP);
    }

    private float slotY(int panel, int i) {

        if (panel == PANEL_HORNO) {

            switch (i) {
                case Horno.SLOT_ENTRADA:
                    return Y_HORNO + 104f;
                case Horno.SLOT_COMBUSTIBLE:
                    return Y_HORNO + 18f;
                default:
                    return Y_HORNO + 61f;
            }
        }

        float tope = Y_JUGADOR + ALTO_PANEL_JUGADOR - PAD - ALTO_TITULO;
        int fila = i / COLUMNAS;

        return tope - (fila + 1) * TAM - fila * GAP;
    }

    private int slotBajoMouse(int panel, float mx, float my) {

        float margen = GAP / 2f;

        for (int i = 0; i < cantidadSlots(panel); i++) {

            float x = slotX(panel, i);
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

        batch.setColor(0f, 0f, 0f, 0.55f);
        batch.draw(pixel, 0f, 0f, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        dibujarPanel(PANEL_HORNO, "Horno");
        dibujarPanel(PANEL_JUGADOR, "Inventario");
        dibujarIndicadores();

        if (!arrastrando) {
            dibujarNombreBajoMouse();
        }

        if (arrastrando) {

            Item item = inventarioDe(panelOrigen).getItem(slotOrigen);

            if (item != null) {
                dibujarIcono(item, mouseX, mouseY, 0.85f);
                dibujarCantidad(item, mouseX - TAM / 2f, mouseY - TAM / 2f);
            }
        }

        batch.setColor(Color.WHITE);

        String ayuda = "Arrastra para mover  |  Shift + click: mover rapido  |  "
                + "E / ESC: cerrar";

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
        float alto = altoPanel(panel);

        batch.setColor(0.42f, 0.32f, 0.22f, 1f);
        batch.draw(pixel, PANEL_X - 3f, py - 3f,
                ANCHO_PANEL + 6f, alto + 6f);

        batch.setColor(0.09f, 0.08f, 0.08f, 0.98f);
        batch.draw(pixel, PANEL_X, py, ANCHO_PANEL, alto);

        batch.setColor(Color.WHITE);
        fuente.draw(batch, titulo, PANEL_X + PAD, py + alto - 10f);

        inventario inv = inventarioDe(panel);
        int hover = slotBajoMouse(panel, mouseX, mouseY);

        Item enMano = arrastrando
                ? inventarioDe(panelOrigen).getItem(slotOrigen)
                : null;

        for (int i = 0; i < cantidadSlots(panel); i++) {

            float x = slotX(panel, i);
            float y = slotY(panel, i);

            batch.setColor(0.30f, 0.25f, 0.20f, 1f);
            batch.draw(pixel, x - 1f, y - 1f, TAM + 2f, TAM + 2f);

            batch.setColor(0.15f, 0.13f, 0.12f, 1f);
            batch.draw(pixel, x, y, TAM, TAM);

            // Marca en verde dónde entra lo que llevás en la mano
            if (panel == PANEL_HORNO && enMano != null
                    && horno.acepta(i, enMano)) {

                batch.setColor(0.3f, 0.9f, 0.3f, 0.25f);
                batch.draw(pixel, x, y, TAM, TAM);
            }

            if (i == hover) {
                batch.setColor(1f, 1f, 1f, 0.15f);
                batch.draw(pixel, x, y, TAM, TAM);
            }

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

    // Llama de combustible, flecha de progreso y etiquetas
    private void dibujarIndicadores() {

        float py = Y_HORNO;

        // Llama (entre entrada y combustible)
        float fx = X_ENTRADA + TAM / 2f - 8f;
        float fy = py + 77f;

        batch.setColor(0.30f, 0.25f, 0.20f, 1f);
        batch.draw(pixel, fx - 1f, fy - 1f, 18f, 26f);
        batch.setColor(0.12f, 0.10f, 0.10f, 1f);
        batch.draw(pixel, fx, fy, 16f, 24f);
        batch.setColor(1f, 0.55f, 0.1f, 1f);
        batch.draw(pixel, fx, fy, 16f, 24f * horno.getLlama());

        // Flecha de progreso (entre las entradas y el resultado)
        float ax = PANEL_X + 255f;
        float ay = py + 81f;

        batch.setColor(0.30f, 0.25f, 0.20f, 1f);
        batch.draw(pixel, ax - 1f, ay - 1f, 92f, 18f);
        batch.setColor(0.12f, 0.10f, 0.10f, 1f);
        batch.draw(pixel, ax, ay, 90f, 16f);
        batch.setColor(1f, 0.75f, 0.2f, 1f);
        batch.draw(pixel, ax, ay, 90f * horno.getProgreso(), 16f);

        // Etiquetas
        batch.setColor(Color.WHITE);

        fuente.draw(batch, "Entrada", PANEL_X + PAD,
                slotY(PANEL_HORNO, Horno.SLOT_ENTRADA) + TAM / 2f + 6f);

        fuente.draw(batch, "Combustible", PANEL_X + PAD,
                slotY(PANEL_HORNO, Horno.SLOT_COMBUSTIBLE) + TAM / 2f + 6f);

        fuente.draw(batch, "Resultado", X_SALIDA + TAM + 10f,
                slotY(PANEL_HORNO, Horno.SLOT_SALIDA) + TAM / 2f + 6f);
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

        TextureRegion region = CofreUI.obtenerRegionRecortada(textura);

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

    public void dispose() {

        batch.dispose();
        pixel.dispose();
        fuente.dispose();
    }
}