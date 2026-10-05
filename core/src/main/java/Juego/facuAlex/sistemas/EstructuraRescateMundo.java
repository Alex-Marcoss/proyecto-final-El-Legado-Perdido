package Juego.facuAlex.sistemas;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import Juego.facuAlex.Jugador;
import Juego.facuAlex.objetoMundo;

public class EstructuraRescateMundo extends objetoMundo {

    // ==========================================================
    // CONFIGURACIÓN
    // ==========================================================

    // Cuántos píxeles del mundo ocupa cada píxel del sprite.
    // Todas las fases usan la misma escala, así la estructura
    // "crece" de verdad a medida que se construye.
    // Subilo para hacerla más grande, bajalo para más chica.
    private static final float ESCALA = 0.8f;

    // Distancia (desde el centro de la base de la estructura)
    // a la que el jugador puede interactuar
    private static final float DISTANCIA_INTERACCION = 130f;

    // El jugador se dibuja de 64x64: lo usamos para medir desde su centro
    private static final float MITAD_JUGADOR = 32f;

    /*
     * Spritesheet: 6 sprites en una fila, todos en celdas del mismo ancho.
     * El tamaño de cada celda se calcula solo (ancho total / 6).
     * Cada sprite se recorta a su parte visible y se dibuja centrado
     * sobre el mismo punto de la base, así no "salta" entre fases.
     */
    private static final String RUTA_SPRITESHEET =
            "estructuras/estructuraRescate.png";

    private static final int CANTIDAD_SPRITES = 6;

    /*
     * Qué sprite (0 a 5) se muestra en cada fase de construcción.
     * Posición 0 = fase 1 ... posición 4 = estructura completa sin activar
     * (la estructura tiene 4 fases, así que faseActual llega hasta 5).
     *
     * El último sprite (el del rayo azul) NO está acá: se muestra
     * únicamente cuando se activa el rescate.
     */
    private static final int[] SPRITE_POR_FASE = { 0, 1, 2, 3, 4 };

    // Transparencia mínima (0 a 255) para considerar que un píxel es parte del sprite
    private static final int UMBRAL_ALPHA = 40;

    // ==========================================================
    // POSICIÓN (esquina inferior izquierda de la "caja" de la estructura)
    // ==========================================================

    private float posicionX;
    private float posicionY;

    // Tamaño de la caja de referencia (una celda del spritesheet, ya escalada)
    private float anchoReferencia;
    private float altoReferencia;

    // ==========================================================
    // LÓGICA
    // ==========================================================

    private EstructuraRescate estructura;

    // ==========================================================
    // TEXTURA
    // ==========================================================

    private Texture spritesheet;
    private TextureRegion[] fases;

    // Tamaño con el que se dibuja cada fase (ya escalado)
    private float[] anchoFase;
    private float[] altoFase;

    // ==========================================================
    // CONSTRUCTOR
    // ==========================================================

    public EstructuraRescateMundo(float x, float y) {

        super("Estructura de Rescate");

        posicionX = x;
        posicionY = y;

        estructura = new EstructuraRescate();

        cargarTextura();
    }

    // ==========================================================
    // CARGAR TEXTURA
    // ==========================================================

    private void cargarTextura() {

        spritesheet = new Texture(RUTA_SPRITESHEET);

        spritesheet.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
        );

        int anchoCelda = spritesheet.getWidth() / CANTIDAD_SPRITES;
        int altoCelda = spritesheet.getHeight();

        anchoReferencia = anchoCelda * ESCALA;
        altoReferencia = altoCelda * ESCALA;

        fases = new TextureRegion[CANTIDAD_SPRITES];
        anchoFase = new float[CANTIDAD_SPRITES];
        altoFase = new float[CANTIDAD_SPRITES];

        // Leemos los píxeles para recortar cada sprite a su parte visible
        TextureData data = spritesheet.getTextureData();

        if (!data.isPrepared()) {
            data.prepare();
        }

        Pixmap pixmap = data.consumePixmap();

        for (int i = 0; i < CANTIDAD_SPRITES; i++) {

            int inicioX = i * anchoCelda;

            int minX = anchoCelda;
            int minY = altoCelda;
            int maxX = -1;
            int maxY = -1;

            for (int py = 0; py < altoCelda; py++) {
                for (int px = 0; px < anchoCelda; px++) {

                    int alpha = pixmap.getPixel(inicioX + px, py) & 0xff;

                    if (alpha > UMBRAL_ALPHA) {

                        if (px < minX) minX = px;
                        if (px > maxX) maxX = px;
                        if (py < minY) minY = py;
                        if (py > maxY) maxY = py;
                    }
                }
            }

            // Celda vacía: usamos la celda completa
            if (maxX < 0) {
                minX = 0;
                minY = 0;
                maxX = anchoCelda - 1;
                maxY = altoCelda - 1;
            }

            int ancho = maxX - minX + 1;
            int alto = maxY - minY + 1;

            fases[i] = new TextureRegion(
                    spritesheet,
                    inicioX + minX,
                    minY,
                    ancho,
                    alto
            );

            anchoFase[i] = ancho * ESCALA;
            altoFase[i] = alto * ESCALA;
        }

        if (data.disposePixmap()) {
            pixmap.dispose();
        }
    }

    // ==========================================================
    // DIBUJAR
    // ==========================================================

    public void dibujar(SpriteBatch batch) {

        int indiceSprite;

        if (estructura.estaActiva()) {

            // Rescate activado: torre con el rayo azul
            indiceSprite = CANTIDAD_SPRITES - 1;

        } else {

            // faseActual va de 1 a 5 (5 = completa), el array empieza en 0
            int fase = Math.max(
                    1,
                    Math.min(estructura.getFaseActual(), SPRITE_POR_FASE.length)
            );

            indiceSprite = SPRITE_POR_FASE[fase - 1];
        }

        // Todas las fases comparten el mismo punto de apoyo:
        // centradas en X y apoyadas sobre posicionY
        float x = posicionX + anchoReferencia / 2f - anchoFase[indiceSprite] / 2f;

        batch.draw(
            fases[indiceSprite],
            x,
            posicionY,
            anchoFase[indiceSprite],
            altoFase[indiceSprite]
        );
    }

    // ==========================================================
    // DISTANCIA (desde el centro de la base de la estructura
    // hasta el centro del jugador)
    // ==========================================================

    private float distanciaAlJugador(float jugadorX, float jugadorY) {

        float centroX = posicionX + anchoReferencia / 2f;
        float centroBaseY = posicionY + altoReferencia * 0.2f;

        float dx = (jugadorX + MITAD_JUGADOR) - centroX;
        float dy = (jugadorY + MITAD_JUGADOR) - centroBaseY;

        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public boolean estaCerca(
            float jugadorX,
            float jugadorY) {

        return distanciaAlJugador(jugadorX, jugadorY)
                <= DISTANCIA_INTERACCION;
    }

    // ==========================================================
    // INTERACCIÓN
    // ==========================================================

    @Override
    public void interactuar(Jugador jugador) {

        if (jugador == null) {
            return;
        }

        if (!estaCerca(
                jugador.getPosicionX(),
                jugador.getPosicionY())) {

            System.out.println(
                    "Estas demasiado lejos de la estructura."
            );

            return;
        }

        // ======================================================
        // FASES DE CONSTRUCCIÓN
        // ======================================================

        if (!estructura.estaCompletamenteConstruida()) {

            System.out.println(
                    "Interactuando con la estructura..."
            );

            estructura.mostrarMateriales(
                    jugador.getInventario()
            );

            if (estructura.puedeConstruir(
                    jugador.getInventario())) {

                estructura.construir(
                        jugador.getInventario()
                );

            } else {

                System.out.println(
                        "Te faltan materiales para esta fase."
                );
            }

            return;
        }

        // ======================================================
        // COLOCAR GEMA
        // ======================================================

        if (!estructura.tieneGema()) {

            if (jugador.getInventario()
                    .tieneItem("Gema Azul")) {

                estructura.colocarGema(
                        jugador.getInventario()
                );

            } else {

                System.out.println(
                        "Necesitas la Gema Azul."
                );
            }

            return;
        }

        // ======================================================
        // ACTIVAR
        // ======================================================

        if (!estructura.estaActiva()) {

            estructura.activar();

            return;
        }

        System.out.println(
                "La estructura de rescate ya esta activa."
        );
    }

    // ==========================================================
    // GETTERS
    // ==========================================================

    public float getPosicionX() {
        return posicionX;
    }

    public float getPosicionY() {
        return posicionY;
    }

    public float getAncho() {
        return anchoReferencia;
    }

    public float getAlto() {
        return altoReferencia;
    }

    public EstructuraRescate getEstructura() {
        return estructura;
    }

    // ==========================================================
    // DISPOSE
    // ==========================================================

    public void dispose() {

        if (spritesheet != null) {
            spritesheet.dispose();
        }
    }
}