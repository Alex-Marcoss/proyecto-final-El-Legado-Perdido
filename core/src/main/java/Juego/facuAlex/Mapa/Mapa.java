package Juego.facuAlex.Mapa;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import Juego.facuAlex.recursos.arbol;

public class Mapa {

    private float ancho;
    private float alto;
    
    private static final int CANTIDAD_ARBOLES = 50;
    private static final float DISTANCIA_MINIMA_ARBOLES = 180f;

    private Zona[] zonas;

    private LugarEspecial templo;

    private TiledMap tiledMap;

    private OrthogonalTiledMapRenderer mapRenderer;

    private List<arbol> arboles;

    private Random random;

    public Mapa(float anchoDefecto, float altoDefecto) {

        // ==============================
        // CARGAR MAPA
        // ==============================

        tiledMap = new TmxMapLoader().load("mapa pasto.tmx");

        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // ==============================
        // DIMENSIONES DEL MAPA
        // ==============================

        int tileWidth = tiledMap.getProperties()
                .get("tilewidth", Integer.class);

        int tileHeight = tiledMap.getProperties()
                .get("tileheight", Integer.class);

        int mapWidthTiles = tiledMap.getProperties()
                .get("width", Integer.class);

        int mapHeightTiles = tiledMap.getProperties()
                .get("height", Integer.class);

        this.ancho = mapWidthTiles * tileWidth;
        this.alto = mapHeightTiles * tileHeight;

        // ==============================
        // ZONAS
        // ==============================

        zonas = new Zona[3];

        zonas[0] = new Zona(
                "Zona 1",
                0,
                0,
                ancho / 3,
                alto
        );

        zonas[1] = new Zona(
                "Zona 2",
                ancho / 3,
                0,
                ancho / 3,
                alto
        );

        zonas[2] = new Zona(
                "Zona 3",
                (ancho / 3) * 2,
                0,
                ancho / 3,
                alto
        );

        // ==============================
        // TEMPLO
        // ==============================

        templo = new LugarEspecial(
                "Templo",
                ancho * 0.75f,
                alto * 0.65f,
                100,
                100
        );

        // ==============================
        // ÁRBOLES
        // ==============================

        arboles = new ArrayList<>();

        random = new Random();

        crearArboles();
    }

    // ==========================================================
    // CREAR ÁRBOLES
    // ==========================================================

    private void crearArboles() {

        int arbolesCreados = 0;
        int intentos = 0;

        while (arbolesCreados < CANTIDAD_ARBOLES && intentos < 10000) {

            intentos++;

            float x = 100 + random.nextFloat() * (ancho - 200);
            float y = 100 + random.nextFloat() * (alto - 200);

            // Primero comprobamos que sea tierra
            if (!esTierra(x, y)) {
                continue;
            }

            // Comprobamos que no esté demasiado cerca de otro árbol
            if (estaMuyCercaDeOtroArbol(x, y)) {
                continue;
            }

            // Creamos el árbol
            arboles.add(new arbol(x, y));

            arbolesCreados++;

            System.out.println(
                "Árbol creado: X=" + x + " Y=" + y
            );
        }

        System.out.println(
            "Total de árboles creados: " + arbolesCreados
        );
    }
    
    private boolean estaMuyCercaDeOtroArbol(float x, float y) {

        for (arbol arbolExistente : arboles) {

            float dx = x - arbolExistente.getPosicionX();
            float dy = y - arbolExistente.getPosicionY();

            float distancia = (float) Math.sqrt(
                dx * dx + dy * dy
            );

            if (distancia < DISTANCIA_MINIMA_ARBOLES) {
                return true;
            }
        }

        return false;
    }w
    
    // ==========================================================
    // COMPROBAR AGUA
    // ==========================================================

    private boolean esTierra(float x, float y) {

        TiledMapTileLayer capaPasto =
                (TiledMapTileLayer) tiledMap.getLayers().get("pasto");

        TiledMapTileLayer capaPasto2 =
                (TiledMapTileLayer) tiledMap.getLayers().get("pasto 2");

        if (capaPasto == null && capaPasto2 == null) {
            System.out.println("ERROR: No se encontraron las capas de pasto.");
            return false;
        }

        int tileWidth = tiledMap.getProperties()
                .get("tilewidth", Integer.class);

        int tileHeight = tiledMap.getProperties()
                .get("tileheight", Integer.class);

        int tileX = (int) (x / tileWidth);
        int tileY = (int) (y / tileHeight);

        boolean hayPasto = false;

        if (capaPasto != null) {

            TiledMapTileLayer.Cell celda =
                    capaPasto.getCell(tileX, tileY);

            if (celda != null && celda.getTile() != null) {
                hayPasto = true;
            }
        }

        if (capaPasto2 != null) {

            TiledMapTileLayer.Cell celda =
                    capaPasto2.getCell(tileX, tileY);

            if (celda != null && celda.getTile() != null) {
                hayPasto = true;
            }
        }

        return hayPasto;
    }

    // ==========================================================
    // DIBUJAR MAPA
    // ==========================================================

    public void dibujar(OrthographicCamera camara) {

        if (mapRenderer != null) {

            mapRenderer.setView(camara);

            mapRenderer.render();
        }
    }

    // ==========================================================
    // COMPROBAR DENTRO DEL MAPA
    // ==========================================================

    public boolean estaDentro(float x, float y) {

        return x >= 0 &&
               x <= ancho &&
               y >= 0 &&
               y <= alto;
    }

    // ==========================================================
    // OBTENER ZONA
    // ==========================================================

    public Zona obtenerZona(float x, float y) {

        for (int i = 0; i < zonas.length; i++) {

            if (zonas[i].contiene(x, y)) {

                return zonas[i];
            }
        }

        return null;
    }

    // ==========================================================
    // TEMPLO
    // ==========================================================

    public boolean estaEnTemplo(float x, float y) {

        return templo.contiene(x, y);
    }

    public LugarEspecial getTemplo() {

        return templo;
    }

    // ==========================================================
    // DIMENSIONES
    // ==========================================================

    public float getAncho() {

        return ancho;
    }

    public float getAlto() {

        return alto;
    }

    // ==========================================================
    // ÁRBOLES
    // ==========================================================

    public List<arbol> getArboles() {

        return arboles;
    }

    // ==========================================================
    // DISPOSE
    // ==========================================================

    public void dispose() {

        if (arboles != null) {

            for (arbol arbol : arboles) {

                arbol.dispose();
            }
        }

        if (tiledMap != null) {

            tiledMap.dispose();
        }

        if (mapRenderer != null) {

            mapRenderer.dispose();
        }
    }
}