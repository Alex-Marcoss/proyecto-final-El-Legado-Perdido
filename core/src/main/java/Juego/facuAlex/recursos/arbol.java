package Juego.facuAlex.recursos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import Juego.facuAlex.objetoMundo;
import Juego.facuAlex.Herramientas.tipoHerramienta;

public class arbol extends objetoMundo {

    private int maderaDisponible;

    private Texture textura;

    private float posicionX;
    private float posicionY;

    // Tamaño visual del árbol
    private static final float ANCHO = 96f;
    private static final float ALTO = 96f;

    // Hitbox solamente en la base del tronco
    private static final float HITBOX_ANCHO = 32f;
    private static final float HITBOX_ALTO = 16f;

    public arbol(float x, float y) {

        // Llama al constructor de objetoMundo
        super("Arbol");

        posicionX = x;
        posicionY = y;

        // Cantidad de veces que se puede talar
        maderaDisponible = 3;

        // Cargar textura
        textura = new Texture("recursos/Arbol.png");

        textura.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );
    }

    public void dibujar(SpriteBatch batch) {

        batch.draw(
            textura,
            posicionX - ANCHO / 2f,
            posicionY,
            ANCHO,
            ALTO
        );
    }

    public Rectangle getHitbox() {

        return new Rectangle(
            posicionX - HITBOX_ANCHO / 2f,
            posicionY,
            HITBOX_ANCHO,
            HITBOX_ALTO
        );
    }

    public float getPosicionX() {
        return posicionX;
    }

    public float getPosicionY() {
        return posicionY;
    }

    @Override
    public tipoHerramienta getHerramientaNecesaria() {

        return tipoHerramienta.HACHA;
    }

    @Override
    public Recursos recolectarRecurso() {

        if (maderaDisponible <= 0) {
            return null;
        }

        // Entrega entre 1 y 2 de madera
        int cantidad = (int) (Math.random() * 2) + 1;

        maderaDisponible--;

        Recursos madera = new Recursos("Madera", cantidad);
        madera.cargarIcono("objetos/madera.png");
        return madera;
    }

    @Override
    public int getEnergiaNecesaria() {

        return 3;
    }

    public boolean estaTalado() {

        return maderaDisponible <= 0;
    }
    
    public boolean estaCerca(float jugadorX, float jugadorY, float distancia) {

        float dx = jugadorX - posicionX;
        float dy = jugadorY - posicionY;

        float distanciaReal = (float) Math.sqrt(
            dx * dx + dy * dy
        );

        return distanciaReal <= distancia;
    }

    public void dispose() {

        if (textura != null) {
            textura.dispose();
        }
    }
}