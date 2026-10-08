package Juego.facuAlex.recursos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import Juego.facuAlex.Herramientas.tipoHerramienta;

public class arbustoBayas extends objetoMundo {

    private int bayasDisponibles;

    private Texture spritesheet;

    private TextureRegion arbustoConBayas;
    private TextureRegion arbustoSinBayas;

    private float posicionX;
    private float posicionY;

    // ==========================================================
    // REGENERACIÓN
    // ==========================================================

    private static final float TIEMPO_REGENERACION = 30f;

    private float tiempoRegeneracion;

    // ==========================================================
    // TAMAÑO
    // ==========================================================

    private static final float ANCHO = 80f;
    private static final float ALTO = 80f;

    private static final float DISTANCIA_RECOLECCION = 90f;

    // ==========================================================
    // CONSTRUCTOR
    // ==========================================================

    public arbustoBayas(
        float x,
        float y
    ) {

        super("Arbusto de bayas");

        posicionX = x;
        posicionY = y;

        bayasDisponibles = 1;

        tiempoRegeneracion = 0f;

        spritesheet = new Texture(
            Gdx.files.internal(
                "recursos/arbustoBayas.png"
            )
        );

        spritesheet.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        // La imagen tiene 2 frames:
        // 1 = con bayas
        // 2 = sin bayas

        int frameWidth =
            spritesheet.getWidth() / 2;

        int frameHeight =
            spritesheet.getHeight();

        arbustoConBayas =
            new TextureRegion(
                spritesheet,
                0,
                0,
                frameWidth,
                frameHeight
            );

        arbustoSinBayas =
            new TextureRegion(
                spritesheet,
                frameWidth,
                0,
                frameWidth,
                frameHeight
            );
    }

    // ==========================================================
    // DIBUJAR
    // ==========================================================

    public void dibujar(
        SpriteBatch batch
    ) {

        TextureRegion frame;

        if (bayasDisponibles > 0) {

            // Frame con bayas
            frame = arbustoConBayas;

        } else {

            // Frame sin bayas
            frame = arbustoSinBayas;
        }

        batch.draw(
            frame,
            posicionX - ANCHO / 2f,
            posicionY,
            ANCHO,
            ALTO
        );
    }

    // ==========================================================
    // HERRAMIENTA NECESARIA
    // ==========================================================

    @Override
    public tipoHerramienta getHerramientaNecesaria() {

        // No necesita herramienta

        return null;
    }

    // ==========================================================
    // RECOLECTAR
    // ==========================================================

    @Override
    public Recursos recolectarRecurso() {

        if (bayasDisponibles <= 0) {
            return null;
        }

        // Al recolectar las bayas,
        // el arbusto queda sin bayas.

        bayasDisponibles = 0;

        tiempoRegeneracion = 0f;

        // Se crea el recurso que va al inventario.

        Comida bayas =
        	    new Comida(
        	        "Bayas",
        	        1,
        	        10
        	    );

        	bayas.cargarIcono( "objetos/baya.png");
        	

        return bayas;
    }

    // ==========================================================
    // ACTUALIZAR
    // ==========================================================

    public void actualizar(
        float delta
    ) {

        // Si todavía tiene bayas,
        // no necesita regenerarse.

        if (bayasDisponibles > 0) {
            return;
        }

        tiempoRegeneracion += delta;

        if (
            tiempoRegeneracion >=
            TIEMPO_REGENERACION
        ) {

            bayasDisponibles = 1;

            tiempoRegeneracion = 0f;

            System.out.println(
                "El arbusto volvio a producir bayas."
            );
        }
    }

    // ==========================================================
    // DISTANCIA
    // ==========================================================

    public boolean estaCerca(
        float jugadorX,
        float jugadorY
    ) {

        float diferenciaX =
            jugadorX - posicionX;

        float diferenciaY =
            jugadorY - posicionY;

        float distancia =
            (float) Math.sqrt(
                diferenciaX * diferenciaX +
                diferenciaY * diferenciaY
            );

        return distancia <=
            DISTANCIA_RECOLECCION;
    }

    // ==========================================================
    // GETTERS
    // ==========================================================

    public int getBayasDisponibles() {
        return bayasDisponibles;
    }

    public boolean tieneBayas() {
        return bayasDisponibles > 0;
    }

    public float getPosicionX() {
        return posicionX;
    }

    public float getPosicionY() {
        return posicionY;
    }

    public float getYOrden() {
        return posicionY;
    }

    public float getTiempoRegeneracion() {
        return tiempoRegeneracion;
    }

    public float getTiempoRestanteRegeneracion() {

        if (bayasDisponibles > 0) {
            return 0f;
        }

        return Math.max(
            0f,
            TIEMPO_REGENERACION -
            tiempoRegeneracion
        );
    }

    // ==========================================================
    // ESTADO
    // ==========================================================

    public boolean estaRecolectado() {
        return bayasDisponibles <= 0;
    }

    public boolean estaAgotado() {
        return bayasDisponibles <= 0;
    }

    // ==========================================================
    // COMPATIBILIDAD
    // ==========================================================

    public boolean recogerBayas() {

        Recursos bayas =
            recolectarRecurso();

        return bayas != null;
    }

    // ==========================================================
    // LIBERAR TEXTURA
    // ==========================================================

    public void dispose() {

        if (spritesheet != null) {
            spritesheet.dispose();
        }
    }
}