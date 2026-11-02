package Juego.facuAlex.recursos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import Juego.facuAlex.Herramientas.tipoHerramienta;

public class roca extends objetoMundo {

    // Veces que se puede minar
    protected int usosRestantes;

    private Texture textura;
    private final Color tinte;      // null = sin tinte

    private float posicionX;
    private float posicionY;

    private static final float ANCHO = 80f;
    private static final float ALTO = 80f;

    // El sprite tiene ~9px transparentes abajo, por eso la hitbox
    // arranca un poco mas arriba de la posicion de la roca.
    private static final float HITBOX_ANCHO = 48f;
    private static final float HITBOX_ALTO = 26f;
    private static final float HITBOX_OFFSET_Y = 8f;

    public roca(float x, float y) {
        this("Roca", x, y, "recursos/Roca.png", null);
    }

    // Para las menas
    protected roca(String nombre, float x, float y,
                   String rutaTextura, Color tinte) {

        super(nombre);

        posicionX = x;
        posicionY = y;
        usosRestantes = 3;
        this.tinte = tinte;

        textura = new Texture(rutaTextura);

        textura.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );
    }

    public void dibujar(SpriteBatch batch) {

        if (tinte != null) {
            batch.setColor(tinte);
        }

        batch.draw(
            textura,
            posicionX - ANCHO / 2f,
            posicionY,
            ANCHO,
            ALTO
        );

        if (tinte != null) {
            batch.setColor(Color.WHITE);
        }
    }

    public Rectangle getHitbox() {

        return new Rectangle(
            posicionX - HITBOX_ANCHO / 2f,
            posicionY + HITBOX_OFFSET_Y,
            HITBOX_ANCHO,
            HITBOX_ALTO
        );
    }

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
        return tipoHerramienta.PICO;
    }

    // Nivel de pico que hace falta: 1 = cualquiera, 2 = pico de hierro
    public int getNivelMinimo() {
        return 1;
    }

    @Override
    public Recursos recolectarRecurso() {

        if (usosRestantes <= 0) {
            return null;
        }

        // Entrega entre 1 y 2 piedras
        int cantidad = (int) (Math.random() * 2) + 1;

        usosRestantes--;

        Recursos piedra = new Recursos("Piedra", cantidad);
        piedra.cargarIcono("objetos/piedra.png");

        return piedra;
    }

    @Override
    public int getEnergiaNecesaria() {
        return 5;
    }

    public boolean estaAgotada() {
        return usosRestantes <= 0;
    }

    public boolean estaCerca(float jugadorX, float jugadorY, float distancia) {

        float dx = jugadorX - posicionX;
        float dy = jugadorY - posicionY;

        float distanciaReal = (float) Math.sqrt(dx * dx + dy * dy);

        return distanciaReal <= distancia;
    }

    public void dispose() {

        if (textura != null) {
            textura.dispose();
        }
    }
}