package Juego.facuAlex.enemigos;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import Juego.facuAlex.enemigos.Criatura.Estado;


public class CriaturaAnimacion {

    public enum Vista { LADO, FRENTE, ESPALDA }

    // Cada celda de los PNG tiene 2 px transparentes alrededor
    private static final int MARGEN = 2;

    // Una sola textura por PNG, aunque haya muchas criaturas iguales
    private static final Map<String, Texture> texturas =
            new HashMap<String, Texture>();

    private final TextureRegion[][] celdas;

    private final Map<Estado, Map<Vista, Animation<TextureRegion>>> animaciones =
            new EnumMap<Estado, Map<Vista, Animation<TextureRegion>>>(Estado.class);

    public CriaturaAnimacion(String ruta, int columnas, int filas) {

        Texture textura = texturas.get(ruta);

        if (textura == null) {

            textura = new Texture(ruta);

            textura.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
            );

            texturas.put(ruta, textura);
        }

        celdas = TextureRegion.split(
            textura,
            textura.getWidth() / columnas,
            textura.getHeight() / filas
        );
    }

    // =====================================================
    // DEFINIR ANIMACIONES (fila del PNG, cantidad de frames, segundos por frame)
    // =====================================================

    public CriaturaAnimacion quieto(Vista vista, int fila, int frames, float duracion) {

        return agregar(Estado.QUIETO, vista, fila, frames, duracion, Animation.PlayMode.LOOP);
    }

    public CriaturaAnimacion caminar(Vista vista, int fila, int frames, float duracion) {

        return agregar(Estado.CAMINAR, vista, fila, frames, duracion, Animation.PlayMode.LOOP);
    }

    public CriaturaAnimacion correr(Vista vista, int fila, int frames, float duracion) {

        return agregar(Estado.CORRER, vista, fila, frames, duracion, Animation.PlayMode.LOOP);
    }

    public CriaturaAnimacion atacar(Vista vista, int fila, int frames, float duracion) {

        return agregar(Estado.ATACAR, vista, fila, frames, duracion, Animation.PlayMode.NORMAL);
    }

    // La muerte se reproduce una vez y se queda en el ultimo frame
    public CriaturaAnimacion muerte(Vista vista, int fila, int frames, float duracion) {

        return agregar(Estado.MUERTO, vista, fila, frames, duracion, Animation.PlayMode.NORMAL);
    }

    private CriaturaAnimacion agregar(Estado estado, Vista vista, int fila,
                                      int frames, float duracion,
                                      Animation.PlayMode modo) {

        TextureRegion[] lista = new TextureRegion[frames];

        for (int i = 0; i < frames; i++) {

            lista[i] = celdas[fila][i];
        }

        Animation<TextureRegion> anim =
                new Animation<TextureRegion>(duracion, lista);

        anim.setPlayMode(modo);

        Map<Vista, Animation<TextureRegion>> porVista = animaciones.get(estado);

        if (porVista == null) {

            porVista = new EnumMap<Vista, Animation<TextureRegion>>(Vista.class);

            animaciones.put(estado, porVista);
        }

        porVista.put(vista, anim);

        return this;
    }

    // =====================================================
    // ELEGIR ANIMACION
    // =====================================================

    // Devuelve la vista pedida si existe; si no, la primera que haya
    private Vista elegirVista(Estado estado, Vista pedida) {

        Map<Vista, Animation<TextureRegion>> porVista = animaciones.get(estado);

        if (porVista == null) {

            return null;
        }

        if (porVista.containsKey(pedida)) {

            return pedida;
        }

        for (Vista v : Vista.values()) {

            if (porVista.containsKey(v)) {

                return v;
            }
        }

        return null;
    }

    // =====================================================
    // DIBUJAR
    // =====================================================

    // (x, y) = los PIES de la criatura, centrados en x
    public void dibujar(SpriteBatch batch, Estado estado, Vista vista,
                        boolean mirandoDerecha, float tiempo,
                        float x, float y, float escala) {

        Estado usado = estado;

        Vista vistaUsada = elegirVista(usado, vista);

        // Si no tiene correr, camina
        if (vistaUsada == null && estado == Estado.CORRER) {

            usado = Estado.CAMINAR;

            vistaUsada = elegirVista(usado, vista);
        }

        // Si no tiene esa animacion, se queda quieta
        if (vistaUsada == null) {

            usado = Estado.QUIETO;

            vistaUsada = elegirVista(usado, vista);
        }

        TextureRegion frame =
                animaciones.get(usado).get(vistaUsada).getKeyFrame(tiempo);

        float ancho = frame.getRegionWidth() * escala;

        float alto = frame.getRegionHeight() * escala;

        float izquierda = x - ancho / 2f;

        float abajo = y - MARGEN * escala;

        // Los sprites de LADO miran a la izquierda: para la derecha se espejan
        if (vistaUsada == Vista.LADO && mirandoDerecha) {

            batch.draw(frame, izquierda + ancho, abajo, -ancho, alto);

        } else {

            batch.draw(frame, izquierda, abajo, ancho, alto);
        }
    }

    // Llamar una sola vez al cerrar / reiniciar la partida
    public static void liberarTexturas() {

        for (Texture t : texturas.values()) {

            t.dispose();
        }

        texturas.clear();
    }
}