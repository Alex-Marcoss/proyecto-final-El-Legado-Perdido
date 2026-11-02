package Juego.facuAlex.receta;

import java.util.HashMap;
import java.util.List;
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

import Juego.facuAlex.Proporcion;
import Juego.facuAlex.jugador.Jugador;
import Juego.facuAlex.recursos.Catalogo;
import Juego.facuAlex.recursos.Item;
import Juego.facuAlex.recursos.Recursos;

public class RecetarioUI {

    private Jugador jugador;

    private SpriteBatch batch;

    private Texture panelTexture;
    private Texture slotTexture;
    private Texture slotSeleccionadoTexture;

    private BitmapFont fuente;
    private GlyphLayout layout;

    private Recetario recetario;

    private boolean abierto;

    private int recetaSeleccionada;

    private boolean mousePresionadoAnteriormente;

    private final Map<Texture, TextureRegion> cacheIconos =
            new HashMap<>();

    // Íconos de muestra para ingredientes que el jugador no tiene
    // (se crean una sola vez)
    private final Map<String, Item> itemsDeMuestra = new HashMap<>();

    // Aviso de recetas nuevas
    private static final float AVISO_Y = 40f;   // desde abajo; ajustalo al arte del panel
    private String aviso;
    private float tiempoAviso;

    // Principal lo actualiza en cada frame: ¿hay una mesa de fabricación cerca?
    private boolean mesaCerca;

    // =====================================================
    // RESOLUCIÓN
    // =====================================================

    private static final float ANCHO_VIRTUAL = 900f;
    private static final float ALTO_VIRTUAL = 600f;

    // =====================================================
    // PANEL
    // =====================================================

    private static final float PANEL_X = 0f;
    private static final float PANEL_Y = 0f;

    private static final float PANEL_ANCHO = 900f;
    private static final float PANEL_ALTO = 600f;

    // =====================================================
    // SLOTS DE RECETAS (coordenadas dadas por el JSON)
    // =====================================================

    private static final float[] RECETAS_X = {
        80, 144, 208, 272,
        80, 144, 208, 272,
        80, 144, 208, 272,
        80, 144, 208, 272,
        80, 144, 208, 272,
        80, 144, 208, 272
    };

    private static final float[] RECETAS_Y = {
        126, 126, 126, 126,
        192, 192, 192, 192,
        258, 258, 258, 258,
        324, 324, 324, 324,
        390, 390, 390, 390,
        456, 456, 456, 456
    };

    private static final float TAMANO_SLOT_RECETA = 56f;

    // =====================================================
    // INGREDIENTES
    // =====================================================

    private static final float[] INGREDIENTES_X = {
        383, 467, 551,
        383, 467, 551,
        383, 467, 551
    };

    private static final float[] INGREDIENTES_Y = {
        182, 182, 182,
        262, 262, 262,
        342, 342, 342
    };

    private static final float TAMANO_SLOT_INGREDIENTE = 56f;

    // =====================================================
    // RESULTADO
    // =====================================================

    private static final float RESULTADO_X = 685f;
    private static final float RESULTADO_Y = 190f;
    private static final float RESULTADO_TAMANO = 96f;

    // =====================================================
    // BOTÓN
    // =====================================================

    private static final float BOTON_X = 653f;
    private static final float BOTON_Y = 467f;
    private static final float BOTON_ANCHO = 160f;
    private static final float BOTON_ALTO = 52f;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public RecetarioUI(Jugador jugador) {

        this.jugador = jugador;

        batch = new SpriteBatch();

        panelTexture = new Texture(
                Gdx.files.internal("inventario/craftingPanel.png")
        );

        slotTexture = new Texture(
                Gdx.files.internal("inventario/slot.png")
        );

        slotSeleccionadoTexture = new Texture(
                Gdx.files.internal("inventario/slotSeleccionado.png")
        );

        panelTexture.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
        );

        slotTexture.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
        );

        slotSeleccionadoTexture.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
        );

        fuente = new BitmapFont();

        fuente.getRegion().getTexture().setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
        );

        layout = new GlyphLayout();

        recetario = new Recetario();

        abierto = false;

        recetaSeleccionada = -1;

        mousePresionadoAnteriormente = false;
    }

    // =====================================================
    // ABRIR / CERRAR
    // =====================================================

    // Desde la mesa de fabricación: muestra TODAS las recetas
    public void abrir() {
        abrirModo(false);
    }

    // Con la tecla C: solo las recetas que no necesitan mesa
    private void abrirComun() {
        abrirModo(true);
    }

    private void abrirModo(boolean comun) {

        recetario.setSoloSinMesa(comun);

        abierto = true;
        recetaSeleccionada = -1;
        mousePresionadoAnteriormente = false;

        revisarDesbloqueos();
    }

    private void cerrar() {

        abierto = false;
        recetaSeleccionada = -1;
    }

    public boolean estaAbierto() {
        return abierto;
    }

    // Busca recetas que se hayan desbloqueado y avisa
    private void revisarDesbloqueos() {

        List<String> nuevas =
                recetario.actualizarDesbloqueos(jugador.getInventario());

        if (nuevas.isEmpty()) {
            return;
        }

        aviso = nuevas.size() == 1
                ? "Nueva receta: " + nuevas.get(0)
                : "Nuevas recetas: " + String.join(", ", nuevas);

        tiempoAviso = 4f;
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================

    public void setMesaCerca(boolean mesaCerca) {
        this.mesaCerca = mesaCerca;
    }

    public void actualizar() {

        if (!abierto) {

            mousePresionadoAnteriormente = false;

            // C abre el recetario común desde cualquier lado
            // (solo las recetas que no necesitan mesa).
            if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
                abrirComun();
            }

            return;
        }

        // Se cierra con C, E o ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)
                || Gdx.input.isKeyJustPressed(Input.Keys.E)
                || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {

            cerrar();

            return;
        }

        if (tiempoAviso > 0f) {
            tiempoAviso -= Gdx.graphics.getDeltaTime();
        }

        boolean mousePresionado =
                Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        float mouseX = convertirMouseX();

        float mouseY = convertirMouseY();

        if (mousePresionado && !mousePresionadoAnteriormente) {

            // Buscar receta seleccionada
            int slotReceta =
                    obtenerRecetaDesdeMouse(mouseX, mouseY);

            if (slotReceta != -1) {

                if (slotReceta < recetario.getCantidadRecetas()) {

                    recetaSeleccionada = slotReceta;
                }
            }

            // Botón craftear
            if (estaDentroBoton(mouseX, mouseY)) {

                fabricarRecetaSeleccionada();
            }
        }

        mousePresionadoAnteriormente = mousePresionado;
    }

    // =====================================================
    // MOUSE
    // =====================================================

    private float convertirMouseX() {

        return Proporcion.mouseX();
    }

    private float convertirMouseY() {

        return Proporcion.mouseY();
    }

    private int obtenerRecetaDesdeMouse(
            float mouseX,
            float mouseY) {

        for (int i = 0; i < RECETAS_X.length; i++) {

            float x = RECETAS_X[i];

            float y = convertirY(
                    RECETAS_Y[i],
                    TAMANO_SLOT_RECETA
            );

            if (mouseX >= x
                    && mouseX <= x + TAMANO_SLOT_RECETA
                    && mouseY >= y
                    && mouseY <= y + TAMANO_SLOT_RECETA) {

                return i;
            }
        }

        return -1;
    }

    private boolean estaDentroBoton(
            float mouseX,
            float mouseY) {

        float y = convertirY(
                BOTON_Y,
                BOTON_ALTO
        );

        return mouseX >= BOTON_X
                && mouseX <= BOTON_X + BOTON_ANCHO
                && mouseY >= y
                && mouseY <= y + BOTON_ALTO;
    }

    // =====================================================
    // FABRICAR
    // =====================================================

    private void fabricarRecetaSeleccionada() {

        if (recetaSeleccionada < 0) {
            return;
        }

        Receta receta =
                recetario.getReceta(recetaSeleccionada);

        if (receta == null) {
            return;
        }

        if (receta.requiereMesa() && !mesaCerca) {
            System.out.println(
                    "Necesitas estar cerca de una mesa de fabricacion."
            );
            return;
        }

        if (!jugador.fabricar(receta)) {
            return;
        }

        // Fabricar algo puede desbloquear otras recetas
        recetario.registrarFabricado(receta);
        revisarDesbloqueos();

        // La lista pudo cambiar de orden: se vuelve a seleccionar la misma
        recetaSeleccionada = recetario.indiceDe(receta);
    }

    // =====================================================
    // DIBUJAR
    // =====================================================

    public void dibujar() {

        if (!abierto) {
            return;
        }

        batch.getProjectionMatrix()
                .setToOrtho2D(
                        0,
                        0,
                        ANCHO_VIRTUAL,
                        ALTO_VIRTUAL
                );

        batch.begin();

        batch.setColor(1f, 1f, 1f, 1f);

        batch.draw(
                panelTexture,
                PANEL_X,
                PANEL_Y,
                PANEL_ANCHO,
                PANEL_ALTO
        );

        dibujarListaRecetas();

        if (recetaSeleccionada != -1) {

            Receta receta =
                    recetario.getReceta(recetaSeleccionada);

            if (receta != null) {

                dibujarInformacionReceta(receta);
            }
        }

        // Aviso de recetas nuevas
        if (aviso != null && tiempoAviso > 0f) {

            float alpha = Math.min(1f, tiempoAviso / 0.5f);

            layout.setText(fuente, aviso);

            fuente.setColor(1f, 0.9f, 0.4f, alpha);
            fuente.draw(
                    batch,
                    aviso,
                    (ANCHO_VIRTUAL - layout.width) / 2f,
                    AVISO_Y
            );
            fuente.setColor(1f, 1f, 1f, 1f);
        }

        batch.setColor(1f, 1f, 1f, 1f);

        batch.end();
    }

    // =====================================================
    // LISTA DE RECETAS
    // =====================================================

    private void dibujarListaRecetas() {

        for (int i = 0; i < RECETAS_X.length; i++) {

            float x = RECETAS_X[i];

            float y = convertirY(
                    RECETAS_Y[i],
                    TAMANO_SLOT_RECETA
            );

            if (i == recetaSeleccionada) {

                batch.draw(
                        slotSeleccionadoTexture,
                        x,
                        y,
                        TAMANO_SLOT_RECETA,
                        TAMANO_SLOT_RECETA
                );

            } else {

                batch.draw(
                        slotTexture,
                        x,
                        y,
                        TAMANO_SLOT_RECETA,
                        TAMANO_SLOT_RECETA
                );
            }

            if (i < recetario.getCantidadRecetas()) {

                Receta receta =
                        recetario.getReceta(i);

                if (receta != null) {

                    dibujarIcono(
                            receta.getResultado(),
                            x + TAMANO_SLOT_RECETA / 2f,
                            y + TAMANO_SLOT_RECETA / 2f,
                            0.80f,
                            TAMANO_SLOT_RECETA,
                            TAMANO_SLOT_RECETA
                    );
                }
            }
        }
    }

    // =====================================================
    // INFORMACIÓN DE LA RECETA
    // =====================================================

    private void dibujarInformacionReceta(
            Receta receta) {

        // Título
        String nombre = receta.getResultado().getNombre();
        dibujarTextoCentrado(nombre, 374f, 116f, 456f, 32f);

        // Ingredientes
        Ingrediente[] ingredientes =
                receta.getIngredientes();

        for (int i = 0;
             i < ingredientes.length && i < 9;
             i++) {

            Ingrediente ingrediente =
                    ingredientes[i];

            float x = INGREDIENTES_X[i];

            float y = convertirY(
                    INGREDIENTES_Y[i],
                    TAMANO_SLOT_INGREDIENTE
            );

            batch.draw(
                    slotTexture,
                    x,
                    y,
                    TAMANO_SLOT_INGREDIENTE,
                    TAMANO_SLOT_INGREDIENTE
            );

            Item item =
                    obtenerItemParaMostrar(
                            ingrediente.getNombreRecurso()
                    );

            if (item != null) {

                dibujarIcono(
                        item,
                        x + TAMANO_SLOT_INGREDIENTE / 2f,
                        y + TAMANO_SLOT_INGREDIENTE / 2f,
                        0.75f,
                        TAMANO_SLOT_INGREDIENTE,
                        TAMANO_SLOT_INGREDIENTE
                );
            }

            dibujarCantidadIngrediente(
                    ingrediente,
                    x,
                    y
            );
        }

        // Resultado
        Item resultado =
                receta.getResultado();

        if (resultado != null) {

            dibujarIcono(
                    resultado,
                    RESULTADO_X + RESULTADO_TAMANO / 2f,
                    convertirY(
                            RESULTADO_Y,
                            RESULTADO_TAMANO
                    ) + RESULTADO_TAMANO / 2f,
                    1f,
                    RESULTADO_TAMANO,
                    RESULTADO_TAMANO
            );

            dibujarTextoCentrado(
                    resultado.getNombre(), 646f, 306f, 174f, 30f);
        }

        // Botón
        boolean puedeCrear =
                receta.puedeCrear(
                        jugador.getInventario()
                );

        String textoBoton =
                (receta.requiereMesa() && !mesaCerca)
                ? "NECESITAS UNA MESA"
                : puedeCrear
                    ? "FABRICAR"
                    : "FALTAN MATERIALES";

        dibujarTextoCentrado(
                textoBoton, BOTON_X, BOTON_Y, BOTON_ANCHO, BOTON_ALTO);
    }

    // =====================================================
    // TEXTO CENTRADO EN UNA CAJA
    // (si no entra, se achica solo)
    // =====================================================

    private void dibujarTextoCentrado(String texto, float x,
            float yDesdeArriba, float ancho, float alto) {

        float escalaOriginal = fuente.getData().scaleX;

        layout.setText(fuente, texto);

        if (layout.width > ancho - 8f) {
            fuente.getData().setScale(
                    escalaOriginal * (ancho - 8f) / layout.width);
            layout.setText(fuente, texto);
        }

        float y = convertirY(yDesdeArriba, alto);

        fuente.draw(
                batch,
                texto,
                x + (ancho - layout.width) / 2f,
                y + (alto + layout.height) / 2f
        );

        fuente.getData().setScale(escalaOriginal);
    }

    // =====================================================
    // BUSCAR ITEM EN INVENTARIO
    // =====================================================

    private Item buscarItemInventario(
            String nombre) {

        for (int i = 0;
             i < jugador.getInventario().getCantidadSlots();
             i++) {

            Item item =
                    jugador.getInventario().getItem(i);

            if (item == null) {
                continue;
            }

            if (item.getNombre().equals(nombre)) {

                return item;
            }
        }

        return null;
    }

    // =====================================================
    // CANTIDAD INGREDIENTE
    // =====================================================

    private void dibujarCantidadIngrediente(
            Ingrediente ingrediente,
            float x,
            float y) {

        int cantidadNecesaria =
                ingrediente.getCantidad();

        int cantidadJugador =
                obtenerCantidadRecurso(
                        ingrediente.getNombreRecurso()
                );

        String texto =
                cantidadJugador
                + " / "
                + cantidadNecesaria;

        layout.setText(
                fuente,
                texto
        );

        float centroX =
                x + TAMANO_SLOT_INGREDIENTE / 2f;

        float textoX =
                centroX
                - layout.width / 2f;

        float textoY =
                y - 6f;

        fuente.draw(
                batch,
                texto,
                textoX,
                textoY
        );
    }

    // =====================================================
    // ITEM PARA MOSTRAR EN LA RECETA
    // =====================================================

    private Item obtenerItemParaMostrar(String nombre) {

        // Primero el que ya tiene el jugador
        Item item = buscarItemInventario(nombre);

        if (item != null) {
            return item;
        }

        // Si no, uno de muestra, creado una sola vez
        if (itemsDeMuestra.containsKey(nombre)) {
            return itemsDeMuestra.get(nombre);
        }

        Item muestra = null;
        String ruta = Catalogo.rutaIcono(nombre);

        if (ruta != null && Gdx.files.internal(ruta).exists()) {

            Recursos r = new Recursos(nombre, 0);
            r.cargarIcono(ruta);
            muestra = r;
        }

        itemsDeMuestra.put(nombre, muestra);

        return muestra;
    }

    // =====================================================
    // OBTENER CANTIDAD
    // =====================================================

    private int obtenerCantidadRecurso(
            String nombre) {

        for (int i = 0;
             i < jugador.getInventario().getCantidadSlots();
             i++) {

            Item item =
                    jugador.getInventario().getItem(i);

            if (item == null) {
                continue;
            }

            if (item instanceof Recursos
                    && item.getNombre().equals(nombre)) {

                Recursos recurso =
                        (Recursos) item;

                return recurso.getCantidad();
            }
        }

        return 0;
    }

    // =====================================================
    // DIBUJAR ICONO
    // =====================================================

    private void dibujarIcono(
            Item item,
            float centroX,
            float centroY,
            float alpha,
            float maxAncho,
            float maxAlto) {

        if (item == null) {
            return;
        }

        Texture textura =
                item.getIcono();

        if (textura == null) {
            return;
        }

        TextureRegion region =
                obtenerRegionRecortada(textura);

        float escala =
                Math.min(
                        maxAncho * 0.72f
                                / region.getRegionWidth(),
                        maxAlto * 0.72f
                                / region.getRegionHeight()
                );

        float ancho =
                region.getRegionWidth()
                * escala;

        float alto =
                region.getRegionHeight()
                * escala;

        batch.setColor(
                1f,
                1f,
                1f,
                alpha
        );

        batch.draw(
                region,
                centroX - ancho / 2f,
                centroY - alto / 2f,
                ancho,
                alto
        );

        batch.setColor(
                1f,
                1f,
                1f,
                1f
        );
    }

    // =====================================================
    // RECORTAR ICONO
    // =====================================================

    private TextureRegion obtenerRegionRecortada(
            Texture textura) {

        TextureRegion cacheada =
                cacheIconos.get(textura);

        if (cacheada != null) {
            return cacheada;
        }

        TextureData data =
                textura.getTextureData();

        if (!data.isPrepared()) {
            data.prepare();
        }

        Pixmap pixmap =
                data.consumePixmap();

        int minX =
                pixmap.getWidth();

        int minY =
                pixmap.getHeight();

        int maxX = -1;
        int maxY = -1;

        for (int py = 0;
             py < pixmap.getHeight();
             py++) {

            for (int px = 0;
                 px < pixmap.getWidth();
                 px++) {

                int alpha =
                        pixmap.getPixel(px, py)
                        & 0xff;

                if (alpha > 10) {

                    if (px < minX) {
                        minX = px;
                    }

                    if (px > maxX) {
                        maxX = px;
                    }

                    if (py < minY) {
                        minY = py;
                    }

                    if (py > maxY) {
                        maxY = py;
                    }
                }
            }
        }

        if (maxX < 0) {

            minX = 0;
            minY = 0;

            maxX =
                    pixmap.getWidth() - 1;

            maxY =
                    pixmap.getHeight() - 1;
        }

        TextureRegion region =
                new TextureRegion(
                        textura,
                        minX,
                        minY,
                        maxX - minX + 1,
                        maxY - minY + 1
                );

        if (data.disposePixmap()) {
            pixmap.dispose();
        }

        cacheIconos.put(
                textura,
                region
        );

        return region;
    }

    // =====================================================
    // CONVERTIR Y DEL JSON
    // =====================================================

    private float convertirY(
            float yDesdeArriba,
            float alto) {

        return ALTO_VIRTUAL
                - yDesdeArriba
                - alto;
    }

    // =====================================================
    // DISPOSE
    // =====================================================

    public void dispose() {

        batch.dispose();

        panelTexture.dispose();

        slotTexture.dispose();

        slotSeleccionadoTexture.dispose();

        fuente.dispose();

        for (Item muestra : itemsDeMuestra.values()) {

            if (muestra != null) {
                muestra.liberarIcono();
            }
        }
    }
}