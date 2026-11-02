package Juego.facuAlex.estructuras;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import Juego.facuAlex.inventario.inventario;
import Juego.facuAlex.recursos.Item;
import Juego.facuAlex.recursos.Recursos;
import Juego.facuAlex.sistemas.Luces;

/*
 * Horno con tres slots (como Minecraft):
 *   0 = entrada (lo que se cocina)
 *   1 = combustible
 *   2 = resultado (solo se puede sacar)
 *
 * estructuras/horno.png: 2 frames de 128x128, 0 = apagado, 1 = prendido.
 */
public class Horno extends Estructura {

    public static final int SLOT_ENTRADA = 0;
    public static final int SLOT_COMBUSTIBLE = 1;
    public static final int SLOT_SALIDA = 2;

    private static final int COLUMNAS = 2;
    private static final int FRAME_APAGADO = 0;
    private static final int FRAME_ENCENDIDO = 1;

    private static final float RADIO_LUZ = 190f;

    private final TextureRegion[] frames;
    private final inventario slots = new inventario();

    // Combustible que se está quemando ahora
    private float combustibleRestante;
    private float combustibleTotal;

    // Progreso de la receta actual
    private float progreso;
    private float tiempoReceta;
    private String recetaEnCurso;

    private float tiempoAnimacion;

    public Horno(float x, float y) {
        super(
                "Horno",
                "estructuras/horno.png",
                x, y, 64f, 64f
        );

        textura.setFilter(
                Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear
        );

        frames = TextureRegion.split(
                textura,
                textura.getWidth() / COLUMNAS,
                textura.getHeight()
        )[0];
    }

    // =====================================================
    // CONSULTAS PARA LA INTERFAZ
    // =====================================================

    public inventario getSlots() {
        return slots;
    }

    public boolean estaEncendido() {
        return combustibleRestante > 0f;
    }

    // 0..1 de la flecha de cocción
    public float getProgreso() {
        return tiempoReceta > 0f
                ? MathUtils.clamp(progreso / tiempoReceta, 0f, 1f)
                : 0f;
    }

    // 0..1 de la llama (combustible que queda de la unidad actual)
    public float getLlama() {
        return combustibleTotal > 0f
                ? MathUtils.clamp(combustibleRestante / combustibleTotal, 0f, 1f)
                : 0f;
    }

    // ¿Puede ir este item en este slot?
    public boolean acepta(int slot, Item item) {

        if (!(item instanceof Recursos)) {
            return false;
        }

        String nombre = item.getNombre();

        switch (slot) {

            case SLOT_ENTRADA:
                return RecetasHorno.getReceta(nombre) != null;

            case SLOT_COMBUSTIBLE:
                return RecetasHorno.getDuracionCombustible(nombre) > 0f;

            default:
                return false; // el resultado solo se saca
        }
    }

    // =====================================================
    // COCCIÓN
    // =====================================================

    private void avanzar(float delta) {

        tiempoAnimacion += delta;

        Item itemEntrada = slots.getItem(SLOT_ENTRADA);

        RecetasHorno.Receta receta = null;

        if (itemEntrada instanceof Recursos) {
            receta = RecetasHorno.getReceta(itemEntrada.getNombre());
        }

        // Si cambió lo que hay en la entrada, se reinicia la cocción
        String nombreReceta = receta == null ? null : receta.entrada;

        if (nombreReceta == null || !nombreReceta.equals(recetaEnCurso)) {
            progreso = 0f;
            recetaEnCurso = nombreReceta;
        }

        tiempoReceta = receta == null ? 0f : receta.tiempo;

        boolean puedeCocinar = receta != null && haySitioParaSalida(receta);

        // El combustible encendido se consume solo
        if (combustibleRestante > 0f) {
            combustibleRestante = Math.max(0f, combustibleRestante - delta);
        }

        // Si hay algo para cocinar y no hay fuego, se enciende otra unidad
        if (puedeCocinar && combustibleRestante <= 0f) {
            encenderConSiguienteCombustible();
        }

        if (puedeCocinar && combustibleRestante > 0f) {

            progreso += delta;

            if (progreso >= receta.tiempo) {
                completar(receta);
                progreso = 0f;
            }

        } else if (progreso > 0f) {

            // Sin fuego o sin lugar, el progreso se va perdiendo
            progreso = Math.max(0f, progreso - delta * 2f);
        }
    }

    private boolean haySitioParaSalida(RecetasHorno.Receta receta) {

        Item salida = slots.getItem(SLOT_SALIDA);

        return salida == null
                || (salida instanceof Recursos
                    && salida.getNombre().equals(receta.salida));
    }

    private void encenderConSiguienteCombustible() {

        Item item = slots.getItem(SLOT_COMBUSTIBLE);

        if (!(item instanceof Recursos)) {
            return;
        }

        float duracion =
                RecetasHorno.getDuracionCombustible(item.getNombre());

        if (duracion <= 0f) {
            return;
        }

        combustibleTotal = duracion;
        combustibleRestante = duracion;

        gastarUnidad(SLOT_COMBUSTIBLE);
    }

    private void completar(RecetasHorno.Receta receta) {

        gastarUnidad(SLOT_ENTRADA);

        Item salida = slots.getItem(SLOT_SALIDA);

        if (salida instanceof Recursos) {
            ((Recursos) salida).agregarCantidad(1);
        } else {
            slots.ponerItem(SLOT_SALIDA, receta.crearSalida());
        }
    }

    private void gastarUnidad(int slot) {

        Item item = slots.getItem(slot);

        if (!(item instanceof Recursos)) {
            return;
        }

        Recursos r = (Recursos) item;
        r.agregarCantidad(-1);

        if (r.getCantidad() <= 0) {
            slots.quitarItem(slot);
        }
    }

    // =====================================================
    // DIBUJAR
    // =====================================================

    @Override
    public void dibujar(SpriteBatch batch) {

        avanzar(Gdx.graphics.getDeltaTime());

        boolean encendido = estaEncendido();

        if (encendido) {

            float parpadeo = 1f + 0.04f * MathUtils.sin(tiempoAnimacion * 11f);

            Luces.agregar(
                    x + ancho / 2f,
                    y + alto * 0.4f,
                    RADIO_LUZ * parpadeo,
                    0.85f
            );
        }

        batch.draw(
                frames[encendido ? FRAME_ENCENDIDO : FRAME_APAGADO],
                x,
                y,
                ancho,
                alto
        );
    }
}