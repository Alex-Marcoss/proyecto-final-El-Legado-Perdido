package Juego.facuAlex.enemigos.guardian;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Rayo del Guardian (sprites/rayo.png = 8 frames de 64 x 128).
 *
 * Fases (iguales para los dos tipos):
 *  1) AVISO:   frames 0-1, dura "retardo" segundos.
 *  2) IMPACTO: frames 2-7.
 *
 * Tipos:
 *  - VERTICAL:   el rayo cae desde arriba sobre el punto (x, y).
 *                Aviso = un circulo en el suelo. Es el ataque original.
 *  - HORIZONTAL: es el MISMO sprite girado 90 grados. El rayo sale del
 *                punto (x, y) (el lado del Guardian) y viaja en linea recta
 *                hacia la derecha o hacia la izquierda.
 *                Aviso = una fila de circulos en el suelo que marca por
 *                donde va a pasar, para que el jugador pueda esquivarlo.
 *
 * Ejemplos:
 *   new RayoEfecto(x, y, 0.74f, 2f, TipoRayo.VERTICAL);
 *   new RayoEfecto(x, y, 0.74f, 2f, TipoRayo.HORIZONTAL, true); // hacia la derecha
 */
public class RayoEfecto {

    public enum TipoRayo {
        VERTICAL,
        HORIZONTAL
    }

    private static final int FRAME_W = 64;
    private static final int FRAME_H = 128;

    // El punto de impacto dentro del frame esta a 12 px del borde inferior
    private static final float OFFSET_IMPACTO_Y = 12f;

    // Largo del rayo dentro del sprite: de la punta de arriba al punto de impacto
    private static final float LARGO_RAYO = FRAME_H - OFFSET_IMPACTO_Y;

    private static final float DURACION_FRAME_IMPACTO = 0.06f;

    // Cantidad de circulos de aviso a lo largo del rayo horizontal
    private static final int CIRCULOS_AVISO = 4;

    // Se cargan una sola vez y se comparten entre todos los rayos
    private static Texture textura;
    private static TextureRegion[] frames;

    private final float x;            // vertical: punto de impacto | horizontal: inicio del rayo
    private final float y;
    private final float impactoX;     // punto donde cae / termina el rayo
    private final float impactoY;
    private final float escala;
    private final float retardo;      // segundos de aviso antes del impacto
    private final TipoRayo tipo;
    private final boolean haciaDerecha;   // solo se usa en HORIZONTAL
    private final Animation<TextureRegion> impacto;

    private float tiempo = 0f;

    // =====================================================
    // CONSTRUCTORES
    // =====================================================

    // Rayo vertical con escala 2 (como antes)
    public RayoEfecto(float x, float y, float retardo) {
        this(x, y, retardo, 2f);
    }

    // Rayo vertical (como antes)
    public RayoEfecto(float x, float y, float retardo, float escala) {
        this(x, y, retardo, escala, TipoRayo.VERTICAL);
    }

    // Si es horizontal, por defecto va hacia la derecha
    public RayoEfecto(float x, float y, float retardo, float escala, TipoRayo tipo) {
        this(x, y, retardo, escala, tipo, true);
    }

    public RayoEfecto(float x, float y, float retardo, float escala,
                      TipoRayo tipo, boolean haciaDerecha) {

        cargarRecursos();

        this.x = x;
        this.y = y;
        this.retardo = retardo;
        this.escala = escala;
        this.tipo = tipo;
        this.haciaDerecha = haciaDerecha;

        if (tipo == TipoRayo.HORIZONTAL) {
            float largo = LARGO_RAYO * escala;
            this.impactoX = haciaDerecha ? x + largo : x - largo;
        } else {
            this.impactoX = x;
        }
        this.impactoY = y;

        this.impacto = new Animation<TextureRegion>(
            DURACION_FRAME_IMPACTO,
            frames[2], frames[3], frames[4], frames[5], frames[6], frames[7]
        );
        this.impacto.setPlayMode(PlayMode.NORMAL);
    }

    private static void cargarRecursos() {
        if (textura != null) return;
        textura = new Texture(Gdx.files.internal("sprites/rayo.png"));
        textura.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        TextureRegion[][] split = TextureRegion.split(textura, FRAME_W, FRAME_H);
        frames = split[0];
    }

    /** Llamar una vez al cerrar el juego (en dispose de Principal). */
    public static void disposeRecursos() {
        if (textura != null) {
            textura.dispose();
            textura = null;
            frames = null;
        }
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================

    public void actualizar(float delta) {
        tiempo += delta;
    }

    // =====================================================
    // DIBUJAR
    // =====================================================

    public void dibujar(SpriteBatch batch) {

        // Mezcla aditiva: el brillo azul se suma al fondo y se ve como luz
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        if (tiempo < retardo) {

            // AVISO: alterna frame 0 y 1 para que "lata"
            TextureRegion aviso = frames[((int) (tiempo / 0.12f)) % 2];

            if (tipo == TipoRayo.VERTICAL) {

                dibujarFrame(batch, aviso, impactoX, impactoY, 0f);

            } else {

                // Fila de circulos desde el Guardian hasta el final del rayo
                for (int i = 1; i <= CIRCULOS_AVISO; i++) {

                    float px = x + (impactoX - x) * i / CIRCULOS_AVISO;

                    dibujarFrame(batch, aviso, px, impactoY, 0f);
                }
            }

        } else {

            TextureRegion frame = impacto.getKeyFrame(tiempo - retardo);

            // Vertical = sin rotar. Horizontal = girado 90 grados.
            // Con +90 la punta del rayo queda a la izquierda (viaja hacia la derecha),
            // con -90 la punta queda a la derecha (viaja hacia la izquierda).
            float rotacion = 0f;

            if (tipo == TipoRayo.HORIZONTAL) {
                rotacion = haciaDerecha ? 90f : -90f;
            }

            dibujarFrame(batch, frame, impactoX, impactoY, rotacion);
        }

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    /*
     * Dibuja un frame de manera que su punto de impacto quede en (px, py).
     * La rotacion se hace alrededor de ese mismo punto, asi que el punto
     * de impacto no se mueve al girar el sprite.
     */
    private void dibujarFrame(SpriteBatch batch, TextureRegion frame,
                              float px, float py, float rotacion) {

        float w = FRAME_W * escala;
        float h = FRAME_H * escala;

        float origenX = w / 2f;
        float origenY = OFFSET_IMPACTO_Y * escala;

        batch.draw(
            frame,
            px - origenX, py - origenY,   // posicion
            origenX, origenY,             // punto de giro
            w, h,                         // tamano
            1f, 1f,                       // escala extra
            rotacion
        );
    }

    // =====================================================
    // DAÑO
    // =====================================================

    /** true solo mientras el rayo esta golpeando (frames fuertes). Para hacer dano. */
    public boolean isImpacto() {
        float t = tiempo - retardo;
        return t >= DURACION_FRAME_IMPACTO && t < DURACION_FRAME_IMPACTO * 4f;
    }

    /** Zona donde hace dano. Ajustar los multiplicadores a gusto. */
    public Rectangle getAreaImpacto() {

        if (tipo == TipoRayo.HORIZONTAL) {

            // Franja alargada: desde el inicio del rayo hasta su final
            float largo = LARGO_RAYO * escala;
            float alto = 28f * escala;
            float izquierda = Math.min(x, impactoX);

            return new Rectangle(izquierda, impactoY - alto / 2f, largo, alto);
        }

        // Vertical (igual que antes)
        float w = 44f * escala;
        float h = 24f * escala;
        return new Rectangle(x - w / 2f, y - h / 2f, w, h);
    }

    public boolean isTerminado() {
        return tiempo >= retardo + DURACION_FRAME_IMPACTO * 6f;
    }

    public TipoRayo getTipo() {
        return tipo;
    }
}