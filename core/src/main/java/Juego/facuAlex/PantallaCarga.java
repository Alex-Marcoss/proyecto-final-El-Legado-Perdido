package Juego.facuAlex;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

public class PantallaCarga {

    // =====================================================
    // RESOLUCIÓN VIRTUAL (la misma que el resto del juego)
    // =====================================================

    private static final float ANCHO_VIRTUAL = 900f;
    private static final float ALTO_VIRTUAL = 600f;

    // =====================================================
    // AJUSTES
    // =====================================================

    private static final String TITULO = "EL LEGADO PERDIDO";

    // Cuánto avanza la barra por segundo (1 = toda la barra)
    // Bajalo para que la carga dure más, subilo para que sea más rápida
    private static final float VELOCIDAD_BARRA = 0.6f;

    private static final float ANCHO_BARRA = 520f;
    private static final float ALTO_BARRA = 26f;
    private static final float Y_BARRA = 200f;

    private static final String[] CONSEJOS = {
            "Arrastr\u00e1 objetos a la \u00faltima fila del inventario para tenerlos en la barra r\u00e1pida.",
            "Equip\u00e1 un hacha para talar \u00e1rboles y un pico para minar rocas.",
            "Correr gasta energ\u00eda: administrala con cuidado.",
            "Us\u00e1 la rueda del mouse o las teclas 1 a 9 para cambiar de slot."
    };

    private static final Color DORADO = new Color(0.95f, 0.78f, 0.30f, 1f);

    // =====================================================
    // ATRIBUTOS
    // =====================================================

    private SpriteBatch batch;
    private Texture pixel;

    private BitmapFont fuenteTitulo;
    private BitmapFont fuenteTexto;
    private BitmapFont fuenteChica;
    private GlyphLayout layout;

    private float progresoMostrado;
    private float progresoObjetivo;
    private String mensaje;

    private int indiceConsejo = -1;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public PantallaCarga() {

        batch = new SpriteBatch();

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1f, 1f, 1f, 1f);
        pm.fill();
        pixel = new Texture(pm);
        pm.dispose();

        fuenteTitulo = Fuentes.tituloAjustado(TITULO, 600f, 52);
        fuenteTexto = Fuentes.texto(22);
        fuenteChica = Fuentes.texto(18);

        layout = new GlyphLayout();

        mostrar();
    }

    // =====================================================
    // MOSTRAR (reinicia la barra y cambia el consejo)
    // =====================================================

    public void mostrar() {

        progresoMostrado = 0f;
        progresoObjetivo = 0f;
        mensaje = "";

        indiceConsejo = (indiceConsejo + 1) % CONSEJOS.length;
    }

    // =====================================================
    // PROGRESO
    // =====================================================

    // Define hasta dónde tiene que llegar la barra y qué texto mostrar
    public void setProgreso(float objetivo, String mensaje) {

        this.progresoObjetivo = Math.min(1f, objetivo);
        this.mensaje = mensaje;
    }

    // La barra ya llegó al último objetivo pedido
    public boolean alcanzoObjetivo() {
        return progresoMostrado >= progresoObjetivo - 0.0001f;
    }

    // La barra está completamente llena
    public boolean estaCompleta() {
        return progresoMostrado >= 1f - 0.0001f;
    }

    public void actualizar(float delta) {

        if (progresoMostrado < progresoObjetivo) {

            progresoMostrado = Math.min(
                    progresoObjetivo,
                    progresoMostrado + VELOCIDAD_BARRA * delta
            );
        }
    }

    // =====================================================
    // DIBUJAR
    // =====================================================

    public void dibujar() {

        batch.getProjectionMatrix().setToOrtho2D(0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        batch.begin();

        // Fondo oscuro
        batch.setColor(0.04f, 0.05f, 0.07f, 1f);
        batch.draw(pixel, 0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        // Título
        layout.setText(fuenteTitulo, TITULO);

        float tituloX = (ANCHO_VIRTUAL - layout.width) / 2f;
        float tituloY = 400f;

        fuenteTitulo.setColor(0f, 0f, 0f, 1f);
        fuenteTitulo.draw(batch, TITULO, tituloX + 3f, tituloY - 3f);

        fuenteTitulo.setColor(DORADO);
        fuenteTitulo.draw(batch, TITULO, tituloX, tituloY);

        // Barra de carga
        float barraX = (ANCHO_VIRTUAL - ANCHO_BARRA) / 2f;
        float borde = 3f;

        // Borde
        batch.setColor(0.60f, 0.45f, 0.25f, 1f);
        batch.draw(pixel, barraX, Y_BARRA, ANCHO_BARRA, ALTO_BARRA);

        // Fondo interior
        float interiorX = barraX + borde + 1f;
        float interiorY = Y_BARRA + borde + 1f;
        float interiorAncho = ANCHO_BARRA - (borde + 1f) * 2f;
        float interiorAlto = ALTO_BARRA - (borde + 1f) * 2f;

        batch.setColor(0.08f, 0.06f, 0.05f, 1f);
        batch.draw(pixel, interiorX, interiorY, interiorAncho, interiorAlto);

        // Relleno según el progreso
        float anchoRelleno = interiorAncho * progresoMostrado;

        if (anchoRelleno > 0f) {

            batch.setColor(DORADO);
            batch.draw(pixel, interiorX, interiorY, anchoRelleno, interiorAlto);

            // Brillo en la parte de arriba para dar volumen
            batch.setColor(1f, 1f, 1f, 0.25f);
            batch.draw(
                    pixel,
                    interiorX,
                    interiorY + interiorAlto * 0.55f,
                    anchoRelleno,
                    interiorAlto * 0.45f
            );
        }

        batch.setColor(1f, 1f, 1f, 1f);

        // Mensaje (izquierda) y porcentaje (derecha)
        float textoY = Y_BARRA - 16f;

        fuenteTexto.setColor(0.90f, 0.90f, 0.90f, 1f);
        fuenteTexto.draw(batch, mensaje, barraX, textoY);

        String porcentaje = (int) (progresoMostrado * 100f) + "%";

        layout.setText(fuenteTexto, porcentaje);

        fuenteTexto.setColor(DORADO);
        fuenteTexto.draw(
                batch,
                porcentaje,
                barraX + ANCHO_BARRA - layout.width,
                textoY
        );

        // Consejo
        fuenteChica.setColor(0.65f, 0.65f, 0.65f, 1f);

        layout.setText(
                fuenteChica,
                "Consejo: " + CONSEJOS[indiceConsejo],
                fuenteChica.getColor(),
                680f,
                Align.center,
                true
        );

        fuenteChica.draw(batch, layout, (ANCHO_VIRTUAL - 680f) / 2f, 110f);

        batch.end();
    }

    // =====================================================
    // DISPOSE
    // =====================================================

    public void dispose() {

        batch.dispose();
        pixel.dispose();
        fuenteTitulo.dispose();
        fuenteTexto.dispose();
        fuenteChica.dispose();
    }
}