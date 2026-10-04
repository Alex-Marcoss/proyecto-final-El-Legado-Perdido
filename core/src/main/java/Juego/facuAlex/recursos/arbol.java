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

    // Hitbox en la base del arbol (donde se ve el tronco y el inicio de la copa).
    // El sprite tiene ~18px transparentes abajo, por eso la hitbox arranca
    // un poco mas arriba de la posicion del arbol.
    private static final float HITBOX_ANCHO = 44f;
    private static final float HITBOX_ALTO = 26f;
    private static final float HITBOX_OFFSET_Y = 18f;

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
            posicionY + HITBOX_OFFSET_Y,
            HITBOX_ANCHO,
            HITBOX_ALTO
        );
    }

    // Altura (Y) que se usa para decidir si el arbol se dibuja
    // detras o delante del jugador.
    public float getYOrden() {
        return posicionY + HITBOX_OFFSET_Y + HITBOX_ALTO / 2f;
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
