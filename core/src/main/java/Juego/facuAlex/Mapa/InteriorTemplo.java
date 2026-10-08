package Juego.facuAlex.Mapa;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Interior del templo: el mapa hecho en Tiled (.tmx).
 *
 *  - Capas de tiles: todas se dibujan (menos "colision", que se oculta).
 *  - Capa de OBJETOS "colisiones": rectangulos o poligonos solidos.
 *  - Capa de TILES "colision" (opcional): cada tile dibujado es solido.
 *  - Capa de OBJETOS "objetos": spawn_jugador, spawn_guardian, salida.
 */
public class InteriorTemplo {

    private static final String CAPA_COLISIONES      = "colisiones";
    private static final String CAPA_COLISION_TILES  = "colision";
    private static final String CAPA_OBJETOS         = "objetos";

    private static final String OBJ_SPAWN_JUGADOR  = "spawn_jugador";
    private static final String OBJ_SPAWN_GUARDIAN = "spawn_guardian";
    private static final String OBJ_SALIDA         = "salida";

	    // true  = el jugador SIEMPRE entra por el centro del mapa
	    // false = usa "spawn_jugador" de Tiled y, si falta, el centro
	    private static final boolean FORZAR_SPAWN_CENTRO = true;
    
    private final TiledMap mapa;
    private final OrthogonalTiledMapRenderer renderer;
    private final float escala;

    private final Array<Rectangle> colisiones = new Array<Rectangle>();
    private final Rectangle salida = new Rectangle();

    private float spawnJugadorX, spawnJugadorY;
    private float spawnGuardianX, spawnGuardianY;

    private boolean hayCapaObjetos;
    private boolean hayCapaColisiones;
    private boolean encontroSpawnJugador;
    private boolean encontroSpawnGuardian;
    private boolean encontroSalida;

    private final float anchoMundo;
    private final float altoMundo;
    private final float tileAncho;
    private final float tileAlto;

    private final GlyphLayout layout = new GlyphLayout();

    public InteriorTemplo(String rutaTmx, float escala) {

        this.escala = escala;

        mapa = new TmxMapLoader().load(rutaTmx);
        renderer = new OrthogonalTiledMapRenderer(mapa, escala);

        int tilesAncho = mapa.getProperties().get("width", Integer.class);
        int tilesAlto  = mapa.getProperties().get("height", Integer.class);
        tileAncho = mapa.getProperties().get("tilewidth", Integer.class);
        tileAlto  = mapa.getProperties().get("tileheight", Integer.class);

        anchoMundo = tilesAncho * tileAncho * escala;
        altoMundo  = tilesAlto * tileAlto * escala;

        cargarColisionesObjetos();
        cargarColisionesTiles();
        cargarObjetos();
        aplicarRespaldos();
        
    }

    // =====================================================
    // CARGA DEL MAPA
    // =====================================================

    private void cargarColisionesObjetos() {

        MapLayer capa = mapa.getLayers().get(CAPA_COLISIONES);

        if (capa == null) {
            return;
        }

        hayCapaColisiones = true;

        for (MapObject objeto : capa.getObjects()) {

            if (objeto instanceof RectangleMapObject) {

                Rectangle r = ((RectangleMapObject) objeto).getRectangle();
                colisiones.add(escalar(r));

            } else if (objeto instanceof PolygonMapObject) {

                // Se usa la caja envolvente del poligono
                Rectangle r = ((PolygonMapObject) objeto).getPolygon()
                        .getBoundingRectangle();
                colisiones.add(escalar(r));

            } else {

              
            }
        }
    }

    private void cargarColisionesTiles() {

        MapLayer capa = mapa.getLayers().get(CAPA_COLISION_TILES);

        if (!(capa instanceof TiledMapTileLayer)) {
            return;
        }

        hayCapaColisiones = true;

        // Es una capa tecnica: se usa para colisionar, no se dibuja
        capa.setVisible(false);

        TiledMapTileLayer tiles = (TiledMapTileLayer) capa;

        float tw = tiles.getTileWidth() * escala;
        float th = tiles.getTileHeight() * escala;

        for (int cx = 0; cx < tiles.getWidth(); cx++) {

            for (int cy = 0; cy < tiles.getHeight(); cy++) {

                if (tiles.getCell(cx, cy) != null) {

                    colisiones.add(new Rectangle(cx * tw, cy * th, tw, th));
                }
            }
        }
    }

    private void cargarObjetos() {

        MapLayer capa = mapa.getLayers().get(CAPA_OBJETOS);

        if (capa == null) {
            return;
        }

        hayCapaObjetos = true;

        for (MapObject objeto : capa.getObjects()) {

            if (objeto.getName() == null) {
                continue;
            }

            String nombre = objeto.getName().trim().toLowerCase();

            if (nombre.equals(OBJ_SALIDA)) {

                if (objeto instanceof RectangleMapObject) {

                    salida.set(escalar(((RectangleMapObject) objeto).getRectangle()));
                    encontroSalida = true;
                }

            } else if (nombre.equals(OBJ_SPAWN_JUGADOR)) {

                float[] p = posicionDe(objeto);
                spawnJugadorX = p[0];
                spawnJugadorY = p[1];
                encontroSpawnJugador = true;

            } else if (nombre.equals(OBJ_SPAWN_GUARDIAN)) {

                float[] p = posicionDe(objeto);
                spawnGuardianX = p[0];
                spawnGuardianY = p[1];
                encontroSpawnGuardian = true;
            }
        }
    }

    private Rectangle escalar(Rectangle r) {
        return new Rectangle(r.x * escala, r.y * escala,
                             r.width * escala, r.height * escala);
    }

    // Posicion de un objeto (punto o rectangulo) ya escalada.
    // En un rectangulo se usa el centro de su borde inferior (los "pies").
    private float[] posicionDe(MapObject objeto) {

        if (objeto instanceof RectangleMapObject) {

            Rectangle r = ((RectangleMapObject) objeto).getRectangle();

            return new float[] {
                (r.x + r.width / 2f) * escala,
                r.y * escala
            };
        }

        Float px = objeto.getProperties().get("x", Float.class);
        Float py = objeto.getProperties().get("y", Float.class);

        if (px == null || py == null) {
            return new float[] { 0f, 0f };
        }

        return new float[] { px * escala, py * escala };
    }

    private void aplicarRespaldos() {

        // El jugador entra por el centro del mapa
        if (FORZAR_SPAWN_CENTRO || !encontroSpawnJugador) {

            

            spawnJugadorX = anchoMundo / 2f;
            spawnJugadorY = altoMundo / 2f;
        }

        if (!encontroSpawnGuardian) {

            spawnGuardianX = anchoMundo / 2f;
            spawnGuardianY = altoMundo * 0.75f;

            
        }

        if (!encontroSalida) {

            float ancho = tileAncho * escala * 3f;

            salida.set(anchoMundo / 2f - ancho / 2f, 0f, ancho, altoMundo * 0.1f);

            
        }
    }

   

    // =====================================================
    // SPAWN SEGURO
    // =====================================================

    /**
     * Llamar UNA vez despues de crear el templo, pasando el tamano de la
     * hitbox de los pies del jugador. Si el spawn cae dentro de una pared,
     * lo mueve al punto libre mas cercano (busqueda en espiral).
     */
    public void ubicarSpawnLibre(float anchoPies, float altoPies) {

        Rectangle prueba = new Rectangle(0, 0, anchoPies, altoPies);

        float paso = Math.max(2f, tileAncho * escala / 4f);
        float maxRadio = Math.max(anchoMundo, altoMundo);

        for (float radio = 0; radio <= maxRadio; radio += paso) {

            for (float ang = 0; ang < 360f; ang += 15f) {

                float x = spawnJugadorX + MathUtils.cosDeg(ang) * radio;
                float y = spawnJugadorY + MathUtils.sinDeg(ang) * radio;

                prueba.setPosition(x - anchoPies / 2f, y);

                if (!colisiona(prueba)) {

                    

                    spawnJugadorX = x;
                    spawnJugadorY = y;
                    return;
                }
            }
        }

        
    }

    // =====================================================
    // DIBUJO
    // =====================================================

    // Llamar ANTES de batch.begin() (el mapa usa su propio batch)
    public void dibujar(OrthographicCamera camara) {

        renderer.setView(camara);
        renderer.render();
    }

    // Llamar SOLO cuando el jugador esta en la salida (dentro de batch.begin/end)
    public void dibujarCartelSalida(SpriteBatch batch, BitmapFont font) {

        String texto = "Presiona E para salir";

        layout.setText(font, texto);

        font.draw(
            batch,
            texto,
            salida.x + salida.width / 2f - layout.width / 2f,
            salida.y + salida.height + 30f
        );
    }

    // Centra la camara en el jugador sin mostrar nada fuera del mapa
    public void ajustarCamara(OrthographicCamera camara, float objetivoX, float objetivoY) {

        float mitadAncho = camara.viewportWidth * camara.zoom / 2f;
        float mitadAlto  = camara.viewportHeight * camara.zoom / 2f;

        float cx = (anchoMundo <= mitadAncho * 2f)
                ? anchoMundo / 2f
                : MathUtils.clamp(objetivoX, mitadAncho, anchoMundo - mitadAncho);

        float cy = (altoMundo <= mitadAlto * 2f)
                ? altoMundo / 2f
                : MathUtils.clamp(objetivoY, mitadAlto, altoMundo - mitadAlto);

        camara.position.set(cx, cy, 0f);
        camara.update();
    }

    // =====================================================
    // COLISION Y SALIDA
    // =====================================================

    public boolean colisiona(Rectangle pies) {

        // Fuera del mapa cuenta como pared
        if (pies.x < 0 || pies.y < 0
                || pies.x + pies.width > anchoMundo
                || pies.y + pies.height > altoMundo) {

            return true;
        }

        for (Rectangle pared : colisiones) {

            if (pared.overlaps(pies)) {
                return true;
            }
        }

        return false;
    }

    public boolean jugadorEnSalida(Rectangle pies) {
        return salida.overlaps(pies);
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public float getSpawnJugadorX()  { return spawnJugadorX; }

    public float getSpawnJugadorY()  { return spawnJugadorY; }

    public float getSpawnGuardianX() { return spawnGuardianX; }

    public float getSpawnGuardianY() { return spawnGuardianY; }

    public float getAnchoMundo()     { return anchoMundo; }

    public float getAltoMundo()      { return altoMundo; }

    public void dispose() {
        renderer.dispose();
        mapa.dispose();
    }
}