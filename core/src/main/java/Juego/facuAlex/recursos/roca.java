package Juego.facuAlex.recursos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import Juego.facuAlex.Herramientas.tipoHerramienta;

public class roca extends objetoMundo {

    private int piedraDisponible;

    private Texture textura;

    private float posicionX;
    private float posicionY;

    // ==========================================================
    // TAMAÑO VISUAL
    // ==========================================================

    private static final float ANCHO = 80f;
    private static final float ALTO = 80f;

    // ==========================================================
    // HITBOX
    // ==========================================================

    // El sprite tiene ~9px transparentes abajo, por eso la hitbox
    // arranca un poco mas arriba de la posicion de la roca.
    private static final float HITBOX_ANCHO = 48f;
    private static final float HITBOX_ALTO = 26f;
    private static final float HITBOX_OFFSET_Y = 8f;

    // ==========================================================
    // CONSTRUCTOR
    // ==========================================================

    public roca(float x, float y) {

        super("Roca");

        posicionX = x;
        posicionY = y;

        // Cantidad de veces que se puede minar
        piedraDisponible = 3;

        // Cargar textura
        textura = new Texture("recursos/Roca.png");

        textura.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );
    }

    // ==========================================================
    // DIBUJAR
    // ==========================================================

    public void dibujar(SpriteBatch batch) {

        batch.draw(
            textura,
            posicionX - ANCHO / 2f,
            posicionY,
            ANCHO,
            ALTO
        );
    }

    // ==========================================================
    // HITBOX
    // ==========================================================

    public Rectangle getHitbox() {

        return new Rectangle(
            posicionX - HITBOX_ANCHO / 2f,
            posicionY + HITBOX_OFFSET_Y,
            HITBOX_ANCHO,
            HITBOX_ALTO
        );
    }

    // Altura (Y) que se usa para decidir si la roca se dibuja
    // detras o delante del jugador.
    public float getYOrden() {
        return posicionY + HITBOX_OFFSET_Y + HITBOX_ALTO / 2f;
    }

    // ==========================================================
    // POSICIÓN
    // ==========================================================

    public float getPosicionX() {
        return posicionX;
    }

    public float getPosicionY() {
        return posicionY;
    }

    // ==========================================================
    // HERRAMIENTA NECESARIA
    // ==========================================================

    @Override
    public tipoHerramienta getHerramientaNecesaria() {

        return tipoHerramienta.PICO;
    }

    // ==========================================================
    // RECOLECTAR PIEDRA
    // ==========================================================

    @Override
    public Recursos recolectarRecurso() {

        if (piedraDisponible <= 0) {
            return null;
        }

        // Entrega entre 1 y 2 piedras
        int cantidad =
            (int) (Math.random() * 2) + 1;

        piedraDisponible--;

        Recursos piedra =
            new Recursos("Piedra", cantidad);

        piedra.cargarIcono(
            "objetos/piedra.png"
        );

        return piedra;
    }

    // ==========================================================
    // ENERGÍA NECESARIA
    // ==========================================================

    @Override
    public int getEnergiaNecesaria() {

        return 5;
    }

    // ==========================================================
    // COMPROBAR SI SE AGOTÓ
    // ==========================================================

    public boolean estaAgotada() {

        return piedraDisponible <= 0;
    }

    // ==========================================================
    // COMPROBAR DISTANCIA
    // ==========================================================

    public boolean estaCerca(
        float jugadorX,
        float jugadorY,
        float distancia
    ) {

        float dx =
            jugadorX - posicionX;

        float dy =
            jugadorY - posicionY;

        float distanciaReal =
            (float) Math.sqrt(
                dx * dx + dy * dy
            );

        return distanciaReal <= distancia;
    }

    // ==========================================================
    // DISPOSE
    // ==========================================================

    public void dispose() {

        if (textura != null) {
            textura.dispose();
        }
    }
}
