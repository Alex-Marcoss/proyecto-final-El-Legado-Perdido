package Juego.facuAlex.Mapa;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

import Juego.facuAlex.recursos.arbol;
import Juego.facuAlex.recursos.arbustoBayas;
import Juego.facuAlex.recursos.roca;
import Juego.facuAlex.recursos.planta;
import Juego.facuAlex.sistemas.EstructuraRescateMundo;

public class Mapa {

private float ancho;
private float alto;

// ==========================================================
// CANTIDAD DE RECURSOS
// ==========================================================

private static final int CANTIDAD_ARBOLES = 50;
private static final float DISTANCIA_MINIMA_ARBOLES = 180f;

private static final int CANTIDAD_ROCAS = 30;
private static final float DISTANCIA_MINIMA_ROCAS = 150f;

private static final int CANTIDAD_PLANTAS = 40;
private static final float DISTANCIA_MINIMA_PLANTAS = 100f;

private static final int CANTIDAD_ARBUSTOS_BAYAS = 20;

// ==========================================================
// ELEMENTOS DEL MAPA
// ==========================================================

private Zona[] zonas;
private LugarEspecial templo;
private EstructuraRescateMundo estructuraRescate;

private TiledMap tiledMap;
private OrthogonalTiledMapRenderer mapRenderer;

private List<arbol> arboles;
private List<roca> rocas;
private List<planta> plantas;
private List<arbustoBayas> arbustosBayas;

private Random random;

// ==========================================================
// CONSTRUCTOR
// ==========================================================

public Mapa(float anchoDefecto, float altoDefecto) {

    tiledMap = new TmxMapLoader().load("mapa pasto.tmx");

    mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

    // ======================================================
    // DIMENSIONES DEL MAPA
    // ======================================================

    int tileWidth =
        tiledMap.getProperties().get(
            "tilewidth",
            Integer.class
        );

    int tileHeight =
        tiledMap.getProperties().get(
            "tileheight",
            Integer.class
        );

    int mapWidthTiles =
        tiledMap.getProperties().get(
            "width",
            Integer.class
        );

    int mapHeightTiles =
        tiledMap.getProperties().get(
            "height",
            Integer.class
        );

    this.ancho = mapWidthTiles * tileWidth;
    this.alto = mapHeightTiles * tileHeight;

    // ======================================================
    // ZONAS
    // ======================================================

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

    // ======================================================
    // TEMPLO
    // ======================================================

    templo = new LugarEspecial(
        "Templo",
        ancho * 0.75f,
        alto * 0.65f,
        100,
        100
    );

    // ======================================================
    // ESTRUCTURA DE RESCATE
    // ======================================================

    estructuraRescate = new EstructuraRescateMundo(
        ancho * 0.50f,
        alto * 0.50f
    );

    // ======================================================
    // LISTAS
    // ======================================================

    arboles = new ArrayList<>();
    rocas = new ArrayList<>();
    plantas = new ArrayList<>();
    arbustosBayas = new ArrayList<>();

    random = new Random();

    // ======================================================
    // CREAR RECURSOS
    // ======================================================

    crearArboles();
    crearRocas();
    crearPlantas();
    crearArbustosBayas();
}

// ==========================================================
// ESTRUCTURA DE RESCATE
// ==========================================================

public EstructuraRescateMundo getEstructuraRescate() {
    return estructuraRescate;
}

// ==========================================================
// CREAR ÁRBOLES
// ==========================================================

private void crearArboles() {

    int arbolesCreados = 0;
    int intentos = 0;

    while (
        arbolesCreados < CANTIDAD_ARBOLES &&
        intentos < 10000
    ) {

        intentos++;

        float x =
            100 + random.nextFloat() * (ancho - 200);

        float y =
            100 + random.nextFloat() * (alto - 200);

        if (!esTierra(x, y)) {
            continue;
        }

        if (estaMuyCercaDeOtroArbol(x, y)) {
            continue;
        }

        arboles.add(new arbol(x, y));
        arbolesCreados++;

        System.out.println(
            "Arbol creado: X=" +
            x +
            " Y=" +
            y
        );
    }

    System.out.println(
        "Total de arboles creados: " +
        arbolesCreados
    );
}

private boolean estaMuyCercaDeOtroArbol(
    float x,
    float y
) {

    for (arbol arbolExistente : arboles) {

        float dx =
            x - arbolExistente.getPosicionX();

        float dy =
            y - arbolExistente.getPosicionY();

        float distancia =
            (float) Math.sqrt(
                dx * dx +
                dy * dy
            );

        if (
            distancia <
            DISTANCIA_MINIMA_ARBOLES
        ) {
            return true;
        }
    }

    return false;
}

// ==========================================================
// CREAR ROCAS
// ==========================================================

private void crearRocas() {

    int rocasCreadas = 0;
    int intentos = 0;

    while (
        rocasCreadas < CANTIDAD_ROCAS &&
        intentos < 10000
    ) {

        intentos++;

        float x =
            100 + random.nextFloat() * (ancho - 200);

        float y =
            100 + random.nextFloat() * (alto - 200);

        if (!esTierra(x, y)) {
            continue;
        }

        if (estaMuyCercaDeOtraRoca(x, y)) {
            continue;
        }

        roca nuevaRoca =
            new roca(x, y);

        rocas.add(nuevaRoca);
        rocasCreadas++;

        System.out.println(
            "Roca creada: X=" +
            x +
            " Y=" +
            y
        );
    }

    System.out.println(
        "Total de rocas creadas: " +
        rocasCreadas
    );
}

private boolean estaMuyCercaDeOtraRoca(
    float x,
    float y
) {

    for (roca rocaExistente : rocas) {

        float dx =
            x - rocaExistente.getPosicionX();

        float dy =
            y - rocaExistente.getPosicionY();

        float distancia =
            (float) Math.sqrt(
                dx * dx +
                dy * dy
            );

        if (
            distancia <
            DISTANCIA_MINIMA_ROCAS
        ) {
            return true;
        }
    }

    return false;
}

// ==========================================================
// CREAR PLANTAS
// ==========================================================

private void crearPlantas() {

    int plantasCreadas = 0;
    int intentos = 0;

    while (
        plantasCreadas < CANTIDAD_PLANTAS &&
        intentos < 10000
    ) {

        intentos++;

        float x =
            100 + random.nextFloat() * (ancho - 200);

        float y =
            100 + random.nextFloat() * (alto - 200);

        if (!esTierra(x, y)) {
            continue;
        }

        if (estaMuyCercaDeOtraPlanta(x, y)) {
            continue;
        }

        planta nuevaPlanta =
            new planta(x, y);

        plantas.add(nuevaPlanta);
        plantasCreadas++;

        System.out.println(
            "Planta creada: X=" +
            x +
            " Y=" +
            y
        );
    }

    System.out.println(
        "Total de plantas creadas: " +
        plantasCreadas
    );
}

private boolean estaMuyCercaDeOtraPlanta(
    float x,
    float y
) {

    for (planta plantaExistente : plantas) {

        float dx =
            x - plantaExistente.getPosicionX();

        float dy =
            y - plantaExistente.getPosicionY();

        float distancia =
            (float) Math.sqrt(
                dx * dx +
                dy * dy
            );

        if (
            distancia <
            DISTANCIA_MINIMA_PLANTAS
        ) {
            return true;
        }
    }

    return false;
}

// ==========================================================
// CREAR ARBUSTOS DE BAYAS
// ==========================================================

private void crearArbustosBayas() {

    int arbustosCreados = 0;
    int intentos = 0;

    while (
        arbustosCreados < CANTIDAD_ARBUSTOS_BAYAS &&
        intentos < 10000
    ) {

        intentos++;

        float x =
            100 + random.nextFloat() * (ancho - 200);

        float y =
            100 + random.nextFloat() * (alto - 200);

        if (!esTierra(x, y)) {
            continue;
        }

        arbustoBayas nuevoArbusto =
            new arbustoBayas(x, y);

        arbustosBayas.add(nuevoArbusto);
        arbustosCreados++;
    }

    System.out.println(
        "Total de arbustos de bayas creados: " +
        arbustosCreados
    );
}

// ==========================================================
// COMPROBAR TIERRA
// ==========================================================

private boolean esTierra(
    float x,
    float y
) {

    TiledMapTileLayer capaPasto =
        (TiledMapTileLayer)
        tiledMap.getLayers().get("pasto");

    TiledMapTileLayer capaPasto2 =
        (TiledMapTileLayer)
        tiledMap.getLayers().get("pasto 2");

    if (
        capaPasto == null &&
        capaPasto2 == null
    ) {

        System.out.println(
            "ERROR: No se encontraron las capas de pasto."
        );

        return false;
    }

    int tileWidth =
        tiledMap.getProperties()
            .get("tilewidth", Integer.class);

    int tileHeight =
        tiledMap.getProperties()
            .get("tileheight", Integer.class);

    int tileX =
        (int) (x / tileWidth);

    int tileY =
        (int) (y / tileHeight);

    boolean hayPasto = false;

    if (capaPasto != null) {

        TiledMapTileLayer.Cell celda =
            capaPasto.getCell(
                tileX,
                tileY
            );

        if (
            celda != null &&
            celda.getTile() != null
        ) {
            hayPasto = true;
        }
    }

    if (capaPasto2 != null) {

        TiledMapTileLayer.Cell celda =
            capaPasto2.getCell(
                tileX,
                tileY
            );

        if (
            celda != null &&
            celda.getTile() != null
        ) {
            hayPasto = true;
        }
    }

    return hayPasto;
}

// ==========================================================
// COLISIONES
// ==========================================================

public boolean puedeCaminar(
    Rectangle hitbox
) {

    if (
        hitbox.x < 0 ||
        hitbox.y < 0 ||
        hitbox.x + hitbox.width > ancho ||
        hitbox.y + hitbox.height > alto
    ) {
        return false;
    }

    if (
        !esTierra(
            hitbox.x,
            hitbox.y
        )
        ||
        !esTierra(
            hitbox.x + hitbox.width,
            hitbox.y
        )
        ||
        !esTierra(
            hitbox.x,
            hitbox.y + hitbox.height
        )
        ||
        !esTierra(
            hitbox.x + hitbox.width,
            hitbox.y + hitbox.height
        )
    ) {

        return false;
    }

    // Árboles

    for (arbol a : arboles) {

        if (
            !a.estaTalado() &&
            hitbox.overlaps(
                a.getHitbox()
            )
        ) {
            return false;
        }
    }

    // Rocas

    for (roca r : rocas) {

        if (
            hitbox.overlaps(
                r.getHitbox()
            )
        ) {
            return false;
        }
    }

    // Las plantas y arbustos no bloquean al jugador

    return true;
}

// ==========================================================
// BUSCAR POSICIÓN LIBRE
// ==========================================================

public float[] buscarPosicionLibre(
    float x,
    float y
) {

    final float paso = 16f;

    for (
        int radio = 0;
        radio <= 40;
        radio++
    ) {

        for (
            int dx = -radio;
            dx <= radio;
            dx++
        ) {

            for (
                int dy = -radio;
                dy <= radio;
                dy++
            ) {

                if (
                    Math.max(
                        Math.abs(dx),
                        Math.abs(dy)
                    ) != radio
                ) {
                    continue;
                }

                float px =
                    x + dx * paso;

                float py =
                    y + dy * paso;

                Rectangle pies =
                    new Rectangle(
                        px + 20f,
                        py,
                        24f,
                        12f
                    );

                if (puedeCaminar(pies)) {

                    return new float[] {
                        px,
                        py
                    };
                }
            }
        }
    }

    return new float[] {
        x,
        y
    };
}

// ==========================================================
// DIBUJAR MAPA
// ==========================================================

public void dibujar(
    OrthographicCamera camara
) {

    if (mapRenderer != null) {

        mapRenderer.setView(camara);
        mapRenderer.render();
    }
}

// ==========================================================
// ACTUALIZAR ARBUSTOS
// ==========================================================

public void actualizarArbustosBayas(
    float delta
) {

    for (
        arbustoBayas arbusto :
        arbustosBayas
    ) {

        arbusto.actualizar(delta);
    }
}

// ==========================================================
// COMPROBAR DENTRO DEL MAPA
// ==========================================================

public boolean estaDentro(
    float x,
    float y
) {

    return x >= 0 &&
           x <= ancho &&
           y >= 0 &&
           y <= alto;
}

// ==========================================================
// ZONA
// ==========================================================

public Zona obtenerZona(
    float x,
    float y
) {

    for (
        int i = 0;
        i < zonas.length;
        i++
    ) {

        if (
            zonas[i].contiene(
                x,
                y
            )
        ) {

            return zonas[i];
        }
    }

    return null;
}

// ==========================================================
// TEMPLO
// ==========================================================

public boolean estaEnTemplo(
    float x,
    float y
) {

    return templo.contiene(
        x,
        y
    );
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

public void eliminarArbol(
    arbol arbol
) {

    if (arbol != null) {

        arbol.dispose();
        arboles.remove(arbol);
    }
}

public arbol obtenerArbolCercano(
    float jugadorX,
    float jugadorY,
    float distanciaMaxima
) {

    arbol arbolCercano = null;

    float distanciaMenor =
        distanciaMaxima;

    for (
        arbol arbolActual :
        arboles
    ) {

        float dx =
            jugadorX -
            arbolActual.getPosicionX();

        float dy =
            jugadorY -
            arbolActual.getPosicionY();

        float distancia =
            (float) Math.sqrt(
                dx * dx +
                dy * dy
            );

        if (
            distancia <=
            distanciaMenor
        ) {

            distanciaMenor =
                distancia;

            arbolCercano =
                arbolActual;
        }
    }

    return arbolCercano;
}

// ==========================================================
// ROCAS
// ==========================================================

public List<roca> getRocas() {
    return rocas;
}

public void eliminarRoca(
    roca roca
) {

    if (roca != null) {

        roca.dispose();
        rocas.remove(roca);
    }
}

public roca obtenerRocaCercana(
    float jugadorX,
    float jugadorY,
    float distanciaMaxima
) {

    roca rocaCercana = null;

    float distanciaMenor =
        distanciaMaxima;

    for (
        roca rocaActual :
        rocas
    ) {

        float dx =
            jugadorX -
            rocaActual.getPosicionX();

        float dy =
            jugadorY -
            rocaActual.getPosicionY();

        float distancia =
            (float) Math.sqrt(
                dx * dx +
                dy * dy
            );

        if (
            distancia <=
            distanciaMenor
        ) {

            distanciaMenor =
                distancia;

            rocaCercana =
                rocaActual;
        }
    }

    return rocaCercana;
}

// ==========================================================
// PLANTAS
// ==========================================================

public List<planta> getPlantas() {
    return plantas;
}

public void eliminarPlanta(
    planta planta
) {

    if (planta != null) {

        planta.dispose();
        plantas.remove(planta);
    }
}

public planta obtenerPlantaCercana(
    float jugadorX,
    float jugadorY,
    float distanciaMaxima
) {

    planta plantaCercana = null;

    float distanciaMenor =
        distanciaMaxima;

    for (
        planta plantaActual :
        plantas
    ) {

        if (
            plantaActual.estaAgotada()
        ) {
            continue;
        }

        float dx =
            jugadorX -
            plantaActual.getPosicionX();

        float dy =
            jugadorY -
            plantaActual.getPosicionY();

        float distancia =
            (float) Math.sqrt(
                dx * dx +
                dy * dy
            );

        if (
            distancia <=
            distanciaMenor
        ) {

            distanciaMenor =
                distancia;

            plantaCercana =
                plantaActual;
        }
    }

    return plantaCercana;
}

// ==========================================================
// ARBUSTOS DE BAYAS
// ==========================================================

public List<arbustoBayas> getArbustosBayas() {
    return arbustosBayas;
}

public arbustoBayas obtenerArbustoBayasCercano(
    float jugadorX,
    float jugadorY,
    float distanciaMaxima
) {

    arbustoBayas arbustoCercano = null;

    float distanciaMenor =
        distanciaMaxima;

    for (
        arbustoBayas arbusto :
        arbustosBayas
    ) {

        if (!arbusto.tieneBayas()) {
            continue;
        }

        float dx =
            jugadorX -
            arbusto.getPosicionX();

        float dy =
            jugadorY -
            arbusto.getPosicionY();

        float distancia =
            (float) Math.sqrt(
                dx * dx +
                dy * dy
            );

        if (
            distancia <=
            distanciaMenor
        ) {

            distanciaMenor =
                distancia;

            arbustoCercano =
                arbusto;
        }
    }

    return arbustoCercano;
}

// ==========================================================
// DISPOSE
// ==========================================================

public void dispose() {

    if (arboles != null) {

        for (
            arbol arbol :
            arboles
        ) {

            arbol.dispose();
        }
    }

    if (rocas != null) {

        for (
            roca roca :
            rocas
        ) {

            roca.dispose();
        }
    }

    if (plantas != null) {

        for (
            planta planta :
            plantas
        ) {

            planta.dispose();
        }
    }

    if (arbustosBayas != null) {

        for (
            arbustoBayas arbusto :
            arbustosBayas
        ) {

            arbusto.dispose();
        }
    }

    if (tiledMap != null) {
        tiledMap.dispose();
    }

    if (mapRenderer != null) {
        mapRenderer.dispose();
    }

    if (estructuraRescate != null) {
        estructuraRescate.dispose();
    }
}

}