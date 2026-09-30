package Juego.facuAlex;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import Juego.facuAlex.Herramientas.Herramienta;
import Juego.facuAlex.recursos.Item;
import Juego.facuAlex.recursos.Recursos;

public class BarraRapidaUI {

    // =====================================================
    // RESOLUCIÓN VIRTUAL (la misma que usa InventarioUI)
    // =====================================================

    private static final float ANCHO_VIRTUAL = 900f;
    private static final float ALTO_VIRTUAL = 600f;

    // =====================================================
    // BARRA
    // =====================================================

    private static final int CANTIDAD_SLOTS = 9;

    // La barra rápida muestra la FILA DE ABAJO del inventario
    // (slots 20 a 28; el inventario tiene 3 filas de 10 slots)
    private static final int PRIMER_SLOT_INVENTARIO = 20;

    // Ancho de la barra en pantalla (bajalo si la querés más chica)
    private static final float ANCHO_BARRA = 400f;

    // Distancia entre el borde inferior de la pantalla y la barra
    private static final float MARGEN_INFERIOR = 10f;

    // =====================================================
    // GRILLA DE SLOTS (fracciones de la barra ya recortada)
    // Si algún slot no coincide con el dibujo, ajustá estos valores
    // =====================================================

    private static final float SLOTS_IZQ = 0.012f;
    private static final float SLOTS_DER = 0.988f;
    private static final float SLOTS_SUP = 0.14f; // medido desde ARRIBA
    private static final float SLOTS_INF = 0.88f; // medido desde ARRIBA
    private static final float ESPACIO_FRAC = 0.007f; // separación entre slots (fracción del ancho)

    // =====================================================
    // ICONOS Y SELECCIÓN
    // =====================================================

    private static final float PORCENTAJE_ICONO = 0.70f;

    private static final float GROSOR_BORDE = 2f;
    private static final Color COLOR_BORDE = new Color(1f, 0.85f, 0.2f, 1f);

    // =====================================================
    // ATRIBUTOS
    // =====================================================

    private Texture texturaBarra;
    private TextureRegion regionBarra; // barra sin los bordes transparentes
    private Texture pixelBlanco;       // 1x1 para dibujar el resaltado

    private SpriteBatch batch;
    private BitmapFont fuente;
    private GlyphLayout layout;

    private Jugador jugador;
    private inventario inventario;

    private int slotSeleccionado;

    // Último item del slot seleccionado que ya se sincronizó con el jugador
    private Item ultimoItemSincronizado;

    private final java.util.Map<Texture, TextureRegion> cacheIconos =
            new java.util.HashMap<>();

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public BarraRapidaUI(Jugador jugador) {

        this.jugador = jugador;
        this.inventario = jugador.getInventario();

        batch = new SpriteBatch();

        fuente = new BitmapFont();
        fuente.getData().setScale(0.8f);
        layout = new GlyphLayout();

        slotSeleccionado = 0;

        texturaBarra = new Texture("inventario/barraRapida.png");
        texturaBarra.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        // Recortamos los márgenes transparentes de la imagen,
        // así la barra queda realmente pegada abajo y centrada
        regionBarra = recortarTransparencia(texturaBarra);

        // Textura de 1 pixel blanco para el resaltado del slot
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        pixelBlanco = new Texture(pm);
        pm.dispose();

        registrarRuedaDelMouse();
    }

    // =====================================================
    // RUEDA DEL MOUSE
    // =====================================================

    private void registrarRuedaDelMouse() {

        InputAdapter ruedaMouse = new InputAdapter() {

            @Override
            public boolean scrolled(float amountX, float amountY) {
                cambiarSlotConRueda(amountY);
                return false; // no bloquea a los demás procesadores
            }
        };

        // Si ya había un procesador de input, lo conservamos
        InputProcessor anterior = Gdx.input.getInputProcessor();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(ruedaMouse);

        if (anterior != null) {
            multiplexer.addProcessor(anterior);
        }

        Gdx.input.setInputProcessor(multiplexer);
    }

    private void cambiarSlotConRueda(float amountY) {

        if (amountY > 0) {
            // Rueda hacia abajo: siguiente slot
            slotSeleccionado = (slotSeleccionado + 1) % CANTIDAD_SLOTS;
        } else if (amountY < 0) {
            // Rueda hacia arriba: slot anterior
            slotSeleccionado =
                    (slotSeleccionado - 1 + CANTIDAD_SLOTS) % CANTIDAD_SLOTS;
        }
    }

    // =====================================================
    // ACTUALIZAR (teclas 1 a 9)
    // =====================================================

    public void actualizar() {

        for (int i = 0; i < CANTIDAD_SLOTS; i++) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                slotSeleccionado = i;
            }
        }

        sincronizarHerramienta();
    }

    // =====================================================
    // EQUIPAR LA HERRAMIENTA DEL SLOT SELECCIONADO
    // Solo actúa cuando cambia el item del slot seleccionado
    // (al cambiar de slot o al mover objetos), así no pisa
    // lo que equipes haciendo click en el inventario.
    // =====================================================

    private void sincronizarHerramienta() {

        Item actual = getItemSeleccionado();

        if (actual == ultimoItemSincronizado) {
            return;
        }

        ultimoItemSincronizado = actual;

        if (actual instanceof Herramienta) {

            jugador.equiparHerramienta((Herramienta) actual);

            System.out.println("Herramienta equipada: " + actual.getNombre());

        } else {

            // Slot vacío o con un recurso: manos libres
            jugador.equiparHerramienta(null);
        }
    }

    // =====================================================
    // GEOMETRÍA
    // =====================================================

    private float altoBarra() {
        return ANCHO_BARRA * regionBarra.getRegionHeight()
                / (float) regionBarra.getRegionWidth();
    }

    private float barraX() {
        return (ANCHO_VIRTUAL - ANCHO_BARRA) / 2f;
    }

    private float barraY() {
        return MARGEN_INFERIOR;
    }

    private float espacio() {
        return ANCHO_BARRA * ESPACIO_FRAC;
    }

    private float anchoSlot() {
        float anchoGrilla = ANCHO_BARRA * (SLOTS_DER - SLOTS_IZQ);
        return (anchoGrilla - espacio() * (CANTIDAD_SLOTS - 1)) / CANTIDAD_SLOTS;
    }

    private float altoSlot() {
        return altoBarra() * (SLOTS_INF - SLOTS_SUP);
    }

    private float slotX(int i) {
        return barraX() + ANCHO_BARRA * SLOTS_IZQ + i * (anchoSlot() + espacio());
    }

    private float slotY() {
        return barraY() + altoBarra() * (1f - SLOTS_INF);
    }

    // =====================================================
    // DIBUJAR
    // =====================================================

    public void dibujar() {

        // Mismo sistema de coordenadas virtual que el inventario
        batch.getProjectionMatrix()
                .setToOrtho2D(0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);

        // Barra
        batch.draw(regionBarra, barraX(), barraY(), ANCHO_BARRA, altoBarra());

        // Slot seleccionado (debajo de los iconos)
        dibujarSeleccion();

        // Objetos
        dibujarObjetos();

        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }

    // =====================================================
    // RESALTADO DEL SLOT SELECCIONADO
    // =====================================================

    private void dibujarSeleccion() {

        float x = slotX(slotSeleccionado);
        float y = slotY();
        float w = anchoSlot();
        float h = altoSlot();

        // Relleno suave
        batch.setColor(1f, 1f, 1f, 0.20f);
        batch.draw(pixelBlanco, x, y, w, h);

        // Borde
        batch.setColor(COLOR_BORDE);
        batch.draw(pixelBlanco, x, y, w, GROSOR_BORDE);                      // abajo
        batch.draw(pixelBlanco, x, y + h - GROSOR_BORDE, w, GROSOR_BORDE);   // arriba
        batch.draw(pixelBlanco, x, y, GROSOR_BORDE, h);                      // izquierda
        batch.draw(pixelBlanco, x + w - GROSOR_BORDE, y, GROSOR_BORDE, h);   // derecha

        batch.setColor(1f, 1f, 1f, 1f);
    }

    // =====================================================
    // OBJETOS
    // =====================================================

    private void dibujarObjetos() {

        for (int i = 0; i < CANTIDAD_SLOTS; i++) {

            Item item = inventario.getItem(PRIMER_SLOT_INVENTARIO + i);

            if (item == null) {
                continue;
            }

            Texture icono = item.getIcono();

            if (icono == null) {
                continue;
            }

            float sx = slotX(i);
            float sy = slotY();

            // Icono recortado a su parte visible, escala única y centrado
            TextureRegion region = obtenerRegionRecortada(icono);

            float maxAncho = anchoSlot() * PORCENTAJE_ICONO;
            float maxAlto = altoSlot() * PORCENTAJE_ICONO;

            float escala = Math.min(
                    maxAncho / region.getRegionWidth(),
                    maxAlto / region.getRegionHeight()
            );

            float ancho = region.getRegionWidth() * escala;
            float alto = region.getRegionHeight() * escala;

            float x = sx + (anchoSlot() - ancho) / 2f;
            float y = sy + (altoSlot() - alto) / 2f;

            batch.draw(region, x, y, ancho, alto);

            // Cantidad
            if (item instanceof Recursos) {

                int cantidad = ((Recursos) item).getCantidad();

                if (cantidad > 1) {

                    String texto = String.valueOf(cantidad);

                    layout.setText(fuente, texto);

                    float textoX = sx + anchoSlot() - layout.width - 3f;
                    float textoY = sy + 3f + layout.height;

                    fuente.draw(batch, texto, textoX, textoY);
                }
            }
        }
    }

    // =====================================================
    // RECORTE DE TRANSPARENCIA
    // =====================================================

    private TextureRegion obtenerRegionRecortada(Texture textura) {

        TextureRegion cacheada = cacheIconos.get(textura);

        if (cacheada != null) {
            return cacheada;
        }

        TextureRegion region = recortarTransparencia(textura);

        cacheIconos.put(textura, region);

        return region;
    }

    private TextureRegion recortarTransparencia(Texture textura) {

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

        return region;
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public int getSlotSeleccionado() {
        return slotSeleccionado;
    }

    public Item getItemSeleccionado() {
        return inventario.getItem(PRIMER_SLOT_INVENTARIO + slotSeleccionado);
    }

    // =====================================================
    // DISPOSE
    // =====================================================

    public void dispose() {

        if (texturaBarra != null) {
            texturaBarra.dispose();
        }

        if (pixelBlanco != null) {
            pixelBlanco.dispose();
        }

        if (batch != null) {
            batch.dispose();
        }

        if (fuente != null) {
            fuente.dispose();
        }
    }
}