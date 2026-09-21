package Juego.facuAlex.Mapa;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class Mapa {

    private float ancho;
    private float alto;

    private Zona[] zonas;
    private LugarEspecial templo;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    public Mapa(float anchoDefecto, float altoDefecto) {

        // Cargar el archivo .tmx
        tiledMap = new TmxMapLoader().load("mapa pasto.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // Obtener dimensiones reales del mapa en píxeles desde el .tmx
        int tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
        int tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);
        int mapWidthTiles = tiledMap.getProperties().get("width", Integer.class);
        int mapHeightTiles = tiledMap.getProperties().get("height", Integer.class);

        // Calcular ancho y alto totales en píxeles
        this.ancho = mapWidthTiles * tileWidth;
        this.alto = mapHeightTiles * tileHeight;

        // Configurar zonas proporcionales al tamaño real
        zonas = new Zona[3];
        zonas[0] = new Zona("Zona 1", 0, 0, ancho / 3, alto);
        zonas[1] = new Zona("Zona 2", ancho / 3, 0, ancho / 3, alto);
        zonas[2] = new Zona("Zona 3", (ancho / 3) * 2, 0, ancho / 3, alto);

        templo = new LugarEspecial("Templo", ancho * 0.75f, alto * 0.65f, 100, 100);
    }

    public void dibujar(OrthographicCamera camara) {
        if (mapRenderer != null) {
            mapRenderer.setView(camara);
            mapRenderer.render();
        }
    }

    public boolean estaDentro(float x, float y) {
        return x >= 0 && x <= ancho && y >= 0 && y <= alto;
    }

    public Zona obtenerZona(float x, float y) {
        for (int i = 0; i < zonas.length; i++) {
            if (zonas[i].contiene(x, y)) {
                return zonas[i];
            }
        }
        return null;
    }

    public boolean estaEnTemplo(float x, float y) {
        return templo.contiene(x, y);
    }

    public LugarEspecial getTemplo() {
        return templo;
    }

    public float getAncho() {
        return ancho;
    }

    public float getAlto() {
        return alto;
    }

    public void dispose() {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
    }
}
