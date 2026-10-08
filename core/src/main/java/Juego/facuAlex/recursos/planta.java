package Juego.facuAlex.recursos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import Juego.facuAlex.Herramientas.tipoHerramienta;

public class planta extends objetoMundo {

    private int fibraDisponible;

    private Texture textura;

    private float posicionX;
    private float posicionY;

    private static final float ANCHO = 110f;
    private static final float ALTO = 110f;

    public planta(float x, float y) {

        super("Planta");

        posicionX = x;
        posicionY = y;

        fibraDisponible = 3;

        textura = new Texture(
            Gdx.files.internal(
                "recursos/plantaFibra.png"
            )
        );

        textura.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );
    }

    // ==========================================================
    // DIBUJAR
    // ==========================================================

    public void dibujar(
        SpriteBatch batch
    ) {

        if (estaRecolectada()) {
            return;
        }

        batch.draw(
            textura,
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

        if (fibraDisponible <= 0) {
            return null;
        }

        fibraDisponible--;

        int cantidad = (int) (Math.random() * 2) + 1;

        
        Recursos fibra = new Recursos("Fibra", cantidad);
        fibra.cargarIcono("objetos/fibra.png");
        
        return fibra;
    }

    // ==========================================================
    // ENERGÍA
    // ==========================================================

    @Override
    public int getEnergiaNecesaria() {
        return 1;
    }

    // ==========================================================
    // GETTERS
    // ==========================================================

    public int getFibraDisponible() {
        return fibraDisponible;
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

    // ==========================================================
    // ESTADO
    // ==========================================================

    public boolean estaRecolectada() {
        return fibraDisponible <= 0;
    }

    // Este método mantiene compatibilidad
    // con el Mapa y otros códigos.

    public boolean estaAgotada() {
        return fibraDisponible <= 0;
    }

    // ==========================================================
    // LIBERAR TEXTURA
    // ==========================================================

    public void dispose() {

        if (textura != null) {
            textura.dispose();
        }
    }
}