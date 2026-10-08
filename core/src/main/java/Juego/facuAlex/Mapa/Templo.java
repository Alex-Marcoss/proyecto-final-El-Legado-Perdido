package Juego.facuAlex.Mapa;   

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * El templo que se ve en el mapa del mundo (sprites/templo.png, 470 x 256).
 *
 * - Es SOLIDO: el jugador no puede atravesar las paredes.
 * - Tiene una zona de interaccion frente a la puerta: si el jugador esta ahi
 *   se muestra el cartel y con la tecla E se entra (eso lo hace Principal).
 *
 * (x, y) es la esquina inferior izquierda del PNG en el mundo.
 */
public class Templo {

    // =====================================================
    // MEDIDAS DEL PNG (en pixeles del PNG, contados DESDE ARRIBA)
    // =====================================================

    // Pared de piedra (zona solida): desde el techo hasta donde toca el pasto
    private static final float PARED_IZQUIERDA = 68f;
    private static final float PARED_DERECHA   = 412f;
    private static final float PARED_ARRIBA    = 22f;
    private static final float PARED_ABAJO     = 195f;

    // Zona frente a la puerta donde se puede interactuar
    private static final float PUERTA_IZQUIERDA = 186f;
    private static final float PUERTA_DERECHA   = 286f;
    private static final float PUERTA_ARRIBA    = 185f;
    private static final float PUERTA_ABAJO     = 245f;

    // Centro de la puerta (para el cartel y para aparecer al salir)
    private static final float PUERTA_CENTRO_X = 238f;
    private static final float CARTEL_Y        = 140f;   // un poco arriba de la puerta
    private static final float APARECER_Y      = 225f;   // sobre el caminito de piedras

    private final Texture textura;
    private final float x;
    private final float y;
    private final float escala;

    private final Rectangle pared;
    private final Rectangle zonaPuerta;

    private final GlyphLayout layout = new GlyphLayout();

    public Templo(String rutaPng, float x, float y, float escala) {

        this.textura = new Texture(Gdx.files.internal(rutaPng));
        this.textura.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        this.x = x;
        this.y = y;
        this.escala = escala;

        this.pared = rectanguloDesdePng(
            PARED_IZQUIERDA, PARED_ARRIBA, PARED_DERECHA, PARED_ABAJO);

        this.zonaPuerta = rectanguloDesdePng(
            PUERTA_IZQUIERDA, PUERTA_ARRIBA, PUERTA_DERECHA, PUERTA_ABAJO);
    }

    // Convierte una zona medida en el PNG (desde arriba) a un Rectangle del mundo
    private Rectangle rectanguloDesdePng(float izq, float arriba, float der, float abajo) {

        float altoPng = textura.getHeight();

        return new Rectangle(
            x + izq * escala,
            y + (altoPng - abajo) * escala,
            (der - izq) * escala,
            (abajo - arriba) * escala
        );
    }

    // =====================================================
    // DIBUJO
    // =====================================================

    public void dibujar(SpriteBatch batch) {

        batch.draw(
            textura,
            x, y,
            textura.getWidth() * escala,
            textura.getHeight() * escala
        );
    }

    // Llamar SOLO cuando el jugador esta en la puerta
    public void dibujarCartel(SpriteBatch batch, BitmapFont font) {

        String texto = "Presiona E para entrar";

        layout.setText(font, texto);

        font.draw(
            batch,
            texto,
            x + PUERTA_CENTRO_X * escala - layout.width / 2f,
            y + (textura.getHeight() - CARTEL_Y) * escala
        );
    }

    // =====================================================
    // COLISION E INTERACCION
    // "pies" = hitbox de los pies del jugador
    // =====================================================

    public boolean colisiona(Rectangle pies) {
        return pared.overlaps(pies);
    }

    public boolean jugadorEnPuerta(Rectangle pies) {
        return zonaPuerta.overlaps(pies);
    }

    // Donde aparece el jugador al salir del templo
    public float getSalidaX() {
        return x + PUERTA_CENTRO_X * escala;
    }

    public float getSalidaY() {
        return y + (textura.getHeight() - APARECER_Y) * escala;
    }

    public float getX() { return x; }

    public float getY() { return y; }

    public float getAncho() { return textura.getWidth() * escala; }

    public float getAlto() { return textura.getHeight() * escala; }

    public void dispose() {
        textura.dispose();
    }
}