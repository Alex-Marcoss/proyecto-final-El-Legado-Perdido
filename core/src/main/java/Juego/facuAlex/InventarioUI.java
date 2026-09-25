package Juego.facuAlex;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import Juego.facuAlex.Herramientas.Herramienta;
import Juego.facuAlex.Herramientas.hacha;
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
    private int slotSeleccionado;

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
    private static final float ALTO_PANEL = 425f;

    // =====================================================
    // POSICIÓN DE LOS SLOTS
    // =====================================================

    private static final float INICIO_SLOT_X = 74.71f;

    private static final float INICIO_SLOT_Y = 86.33f;

    private static final float TAMANO_SLOT_X = 64.77f;
    private static final float TAMANO_SLOT_Y = 68.07f;

    private static final float ESPACIO_X = 6.64f;
    private static final float ESPACIO_Y = 6.64f;

    // =====================================================
    // TAMAÑO VISUAL DE LOS OBJETOS
    // =====================================================

    /*
     * Todos los objetos intentarán ocupar como máximo
     * este tamaño.
     *
     * Se mantiene la proporción original de cada textura.
     */

    private static final float TAMANO_MAX_ICONO = 40f;

    // =====================================================
    // POSICIÓN DE LA CANTIDAD
    // =====================================================

    private static final float MARGEN_CANTIDAD = 4f;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public InventarioUI(Jugador jugador) {

        this.jugador = jugador;

        batchInventario = new SpriteBatch();

        panelTexture = new Texture(
            Gdx.files.internal("inventario/panel.png")
        );

        slotTexture = new Texture(
            Gdx.files.internal("inventario/slot.png")
        );

        panelTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        slotTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );
      
        // =================================================
        // FUENTE PARA CANTIDADES
        // =================================================

        fuenteCantidad = new BitmapFont();
        layoutCantidad = new GlyphLayout();
        
        fuenteCantidad.getRegion().getTexture().setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        abierto = false;
        slotSeleccionado = -1;
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================

    public void actualizar() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {

            abierto = !abierto;

            if (!abierto) {
                slotSeleccionado = -1;
            }
        }

        if (!abierto) {
            return;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

            float mouseX = convertirMouseX();
            float mouseY = convertirMouseY();

            seleccionarSlot(mouseX, mouseY);
        }
    }

    // =====================================================
    // CONVERTIR MOUSE
    // =====================================================

    private float convertirMouseX() {

        return
            Gdx.input.getX()
            * ANCHO_VIRTUAL
            / Gdx.graphics.getWidth();
    }

    private float convertirMouseY() {

        return
            (Gdx.graphics.getHeight() - Gdx.input.getY())
            * ALTO_VIRTUAL
            / Gdx.graphics.getHeight();
    }

    // =====================================================
    // POSICIÓN X DEL SLOT
    // =====================================================

    private float obtenerSlotX(
        float panelX,
        int columna
    ) {

        return
            panelX
            + INICIO_SLOT_X
            + columna * (
                TAMANO_SLOT_X
                + ESPACIO_X
            );
    }

    // =====================================================
    // POSICIÓN Y DEL SLOT
    // =====================================================

    private float obtenerSlotY(
        float panelY,
        int fila
    ) {

        // Invertimos las filas porque
        // LibGDX utiliza coordenadas desde abajo.

        int filaInvertida =
            FILAS - 1 - fila;

        return
            panelY
            + INICIO_SLOT_Y
            + filaInvertida * (
                TAMANO_SLOT_Y
                + ESPACIO_Y
            );
    }

    // =====================================================
    // SELECCIONAR SLOT
    // =====================================================

    private void seleccionarSlot(
        float mouseX,
        float mouseY
    ) {

        float panelX =
            (ANCHO_VIRTUAL - ANCHO_PANEL) / 2f;

        float panelY =
            (ALTO_VIRTUAL - ALTO_PANEL) / 2f;

        for (int i = 0; i < CANTIDAD_SLOTS; i++) {

            int fila =
                i / COLUMNAS;

            int columna =
                i % COLUMNAS;

            float x =
                obtenerSlotX(
                    panelX,
                    columna
                );

            float y =
                obtenerSlotY(
                    panelY,
                    fila
                );

            if (
                mouseX >= x &&
                mouseX <= x + TAMANO_SLOT_X &&
                mouseY >= y &&
                mouseY <= y + TAMANO_SLOT_Y
            ) {

                slotSeleccionado = i;

                Item item =
                    jugador
                        .getInventario()
                        .getItem(i);

                // =================================================
                // EQUIPAR HERRAMIENTA
                // =================================================

                if (item instanceof Herramienta) {

                    Herramienta herramienta =
                        (Herramienta) item;

                    jugador.equiparHerramienta(
                        herramienta
                    );

                    System.out.println(
                        "Equipaste: "
                        + herramienta.getNombre()
                    );
                }

                return;
            }
        }
    }

    // =====================================================
    // DIBUJAR INVENTARIO
    // =====================================================

    public void dibujar() {

        if (!abierto) {
            return;
        }

        batchInventario
            .getProjectionMatrix()
            .setToOrtho2D(
                0,
                0,
                ANCHO_VIRTUAL,
                ALTO_VIRTUAL
            );

        float panelX =
            (ANCHO_VIRTUAL - ANCHO_PANEL) / 2f;

        float panelY =
            (ALTO_VIRTUAL - ALTO_PANEL) / 2f;

        batchInventario.begin();

        batchInventario.setColor(
            1f,
            1f,
            1f,
            1f
        );

        // =================================================
        // PANEL
        // =================================================

        batchInventario.draw(
            panelTexture,
            panelX,
            panelY,
            ANCHO_PANEL,
            ALTO_PANEL
        );

        // =================================================
        // SLOTS
        // =================================================

        for (int i = 0; i < CANTIDAD_SLOTS; i++) {

            int fila =
                i / COLUMNAS;

            int columna =
                i % COLUMNAS;

            float x =
                obtenerSlotX(
                    panelX,
                    columna
                );

            float y =
                obtenerSlotY(
                    panelY,
                    fila
                );

            // =================================================
            // SLOT SELECCIONADO
            // =================================================

            if (i == slotSeleccionado) {

                batchInventario.setColor(
                    1f,
                    1f,
                    1f,
                    0.30f
                );

                batchInventario.draw(
                    slotTexture,
                    x,
                    y,
                    TAMANO_SLOT_X,
                    TAMANO_SLOT_Y
                );

                batchInventario.setColor(
                    1f,
                    1f,
                    1f,
                    1f
                );
            }

            // =================================================
            // ITEM
            // =================================================

            Item item =
                jugador
                    .getInventario()
                    .getItem(i);

            if (item == null) {
                continue;
            }

            Texture icono =
                item.getIcono();

            if (icono != null) {

                dibujarIcono(
                    icono,
                    x,
                    y
                );
            }

            // =================================================
            // CANTIDAD
            // =================================================

            if (item instanceof Recursos) {

                Recursos recurso =
                    (Recursos) item;

                dibujarCantidad(
                    recurso,
                    x,
                    y
                );
            }
        }

        batchInventario.setColor(
            1f,
            1f,
            1f,
            1f
        );

        batchInventario.end();
    }

    // =====================================================
    // DIBUJAR ICONO NORMALIZADO
    // =====================================================

    private void dibujarIcono(
    Texture icono,
    float slotX,
    float slotY
) {

    float anchoOriginal = icono.getWidth();
    float altoOriginal = icono.getHeight();

    float dimensionMayor = Math.max(
        anchoOriginal,
        altoOriginal
    );

    float escala =
        TAMANO_MAX_ICONO / dimensionMayor;

    float multiplicador = 1f;
    float DESPLAZAMIENTO_HACHA_X = 10f;
    
    Item itemActual = null;

    for (int i = 0; i < 30; i++) {

        Item item = jugador
            .getInventario()
            .getItem(i);

        if (item != null && item.getIcono() == icono) {
            itemActual = item;
            break;
        }
    }

    if (itemActual instanceof hacha) {
        multiplicador = 3.30f;
    }

    escala *= multiplicador;

    float ancho =
        anchoOriginal * escala;

    float alto =
        altoOriginal * escala;

    float x =
    	    slotX +
    	    (TAMANO_SLOT_X - ancho) / 2f;

    	float y =
    	    slotY +
    	    (TAMANO_SLOT_Y - alto) / 2f;

    	if (itemActual instanceof hacha) {
    	    x += DESPLAZAMIENTO_HACHA_X;
    	}

    batchInventario.draw(
        icono,
        x,
        y,
        ancho,
        alto
    );
}

    // =====================================================
    // DIBUJAR CANTIDAD
    // =====================================================

    private void dibujarCantidad(Recursos recurso, float slotX, float slotY) {

    int cantidad = recurso.getCantidad();

    if (cantidad <= 0) {
        return;
    }

    String texto = String.valueOf(cantidad);

    // Calculamos el tamaño real del texto
    layoutCantidad.setText(fuenteCantidad, texto);

    float anchoTexto = layoutCantidad.width;

    float x = slotX + TAMANO_SLOT_X - anchoTexto - MARGEN_CANTIDAD;
    float y = slotY + MARGEN_CANTIDAD + layoutCantidad.height;

    fuenteCantidad.draw(
        batchInventario,
        texto,
        x,
        y
    );
}

    // =====================================================
    // ESTADO
    // =====================================================

    public boolean estaAbierto() {
        return abierto;
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