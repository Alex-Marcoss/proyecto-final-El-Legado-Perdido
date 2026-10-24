
package Juego.facuAlex.Mapa;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import Juego.facuAlex.estructuras.*;

public class Mapa {

    private float ancho;
    private float alto;

    private static final int CANTIDAD_ARBOLES = 50;
    private static final float DISTANCIA_MINIMA_ARBOLES = 180f;

    private static final int CANTIDAD_ROCAS = 30;
    private static final float DISTANCIA_MINIMA_ROCAS = 150f;

    private static final int CANTIDAD_PLANTAS = 40;
    private static final float DISTANCIA_MINIMA_PLANTAS = 100f;

    private static final int CANTIDAD_ARBUSTOS_BAYAS = 20;

    /*
     * Tamaños aproximados de las áreas de colisión.
     * Ajustarlos si los sprites tienen otras dimensiones.
     */
    private static final float ANCHO_PLANTA = 32f;
    private static final float ALTO_PLANTA = 32f;

    private static final float ANCHO_ARBUSTO = 48f;
    private static final float ALTO_ARBUSTO = 48f;

    private static final float ANCHO_RESCATE = 128f;
    private static final float ALTO_RESCATE = 128f;

    private Zona[] zonas;
    private LugarEspecial templo;
    private EstructuraRescateMundo estructuraRescate;

    private float rescateX;
    private float rescateY;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    private List<arbol> arboles;
    private List<roca> rocas;
    private List<planta> plantas;
    private List<arbustoBayas> arbustosBayas;

    private Random random;

    private List<Estructura> estructurasConstruidas =
            new ArrayList<>();

    public Mapa(float anchoDefecto, float altoDefecto) {

        tiledMap = new TmxMapLoader().load("mapa pasto.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        int tileWidth = tiledMap.getProperties()
                .get("tilewidth", Integer.class);

        int tileHeight = tiledMap.getProperties()
                .get("tileheight", Integer.class);

        int mapWidthTiles = tiledMap.getProperties()
                .get("width", Integer.class);

        int mapHeightTiles = tiledMap.getProperties()
                .get("height", Integer.class);

        ancho = mapWidthTiles * tileWidth;
        alto = mapHeightTiles * tileHeight;

        zonas = new Zona[3];

        zonas[0] = new Zona("Zona 1", 0, 0, ancho / 3, alto);
        zonas[1] = new Zona("Zona 2", ancho / 3, 0, ancho / 3, alto);
        zonas[2] = new Zona(
                "Zona 3", (ancho / 3) * 2, 0, ancho / 3, alto);

        templo = new LugarEspecial(
                "Templo", ancho * 0.75f, alto * 0.65f, 100, 100);

        rescateX = ancho * 0.50f;
        rescateY = alto * 0.50f;

        estructuraRescate = new EstructuraRescateMundo(
                rescateX, rescateY);

        arboles = new ArrayList<>();
        rocas = new ArrayList<>();
        plantas = new ArrayList<>();
        arbustosBayas = new ArrayList<>();

        random = new Random();

        crearArboles();
        crearRocas();
        crearPlantas();
        crearArbustosBayas();
    }

    public EstructuraRescateMundo getEstructuraRescate() {
        return estructuraRescate;
    }

    // ==========================================================
    // CREACIÓN DE RECURSOS
    // ==========================================================

    private void crearArboles() {

        int creados = 0;
        int intentos = 0;

        while (creados < CANTIDAD_ARBOLES && intentos < 10000) {

            intentos++;

            float x = 100 + random.nextFloat() * (ancho - 200);
            float y = 100 + random.nextFloat() * (alto - 200);

            if (!esTierra(x, y) || estaMuyCercaDeOtroArbol(x, y)) {
                continue;
            }

            arboles.add(new arbol(x, y));
            creados++;
        }

        System.out.println("Total de árboles creados: " + creados);
    }

    private boolean estaMuyCercaDeOtroArbol(float x, float y) {

        for (arbol existente : arboles) {
            float dx = x - existente.getPosicionX();
            float dy = y - existente.getPosicionY();

            if (Math.sqrt(dx * dx + dy * dy)
                    < DISTANCIA_MINIMA_ARBOLES) {
                return true;
            }
        }

        return false;
    }

    private void crearRocas() {

        int creadas = 0;
        int intentos = 0;

        while (creadas < CANTIDAD_ROCAS && intentos < 10000) {

            intentos++;

            float x = 100 + random.nextFloat() * (ancho - 200);
            float y = 100 + random.nextFloat() * (alto - 200);

            if (!esTierra(x, y) || estaMuyCercaDeOtraRoca(x, y)) {
                continue;
            }

            rocas.add(new roca(x, y));
            creadas++;
        }

        System.out.println("Total de rocas creadas: " + creadas);
    }

    private boolean estaMuyCercaDeOtraRoca(float x, float y) {

        for (roca existente : rocas) {
            float dx = x - existente.getPosicionX();
            float dy = y - existente.getPosicionY();

            if (Math.sqrt(dx * dx + dy * dy)
                    < DISTANCIA_MINIMA_ROCAS) {
                return true;
            }
        }

        return false;
    }

    private void crearPlantas() {

        int creadas = 0;
        int intentos = 0;

        while (creadas < CANTIDAD_PLANTAS && intentos < 10000) {

            intentos++;

            float x = 100 + random.nextFloat() * (ancho - 200);
            float y = 100 + random.nextFloat() * (alto - 200);

            if (!esTierra(x, y) || estaMuyCercaDeOtraPlanta(x, y)) {
                continue;
            }

            plantas.add(new planta(x, y));
            creadas++;
        }

        System.out.println("Total de plantas creadas: " + creadas);
    }

    private boolean estaMuyCercaDeOtraPlanta(float x, float y) {

        for (planta existente : plantas) {
            float dx = x - existente.getPosicionX();
            float dy = y - existente.getPosicionY();

            if (Math.sqrt(dx * dx + dy * dy)
                    < DISTANCIA_MINIMA_PLANTAS) {
                return true;
            }
        }

        return false;
    }

    private void crearArbustosBayas() {

        int creados = 0;
        int intentos = 0;

        while (creados < CANTIDAD_ARBUSTOS_BAYAS
                && intentos < 10000) {

            intentos++;

            float x = 100 + random.nextFloat() * (ancho - 200);
            float y = 100 + random.nextFloat() * (alto - 200);

            if (!esTierra(x, y)) {
                continue;
            }

            arbustosBayas.add(new arbustoBayas(x, y));
            creados++;
        }

        System.out.println("Total de arbustos de bayas creados: " + creados);
    }

    // ==========================================================
    // HITBOXES DE RECURSOS Y ESTRUCTURAS
    // ==========================================================

    /*
     * Se construyen las hitboxes a partir de las posiciones
     * disponibles en las clases de recursos.
     */
    private Rectangle hitboxPlanta(planta p) {
        return new Rectangle(
                p.getPosicionX(),
                p.getPosicionY(),
                ANCHO_PLANTA,
                ALTO_PLANTA);
    }

    private Rectangle hitboxArbusto(arbustoBayas arbusto) {
        return new Rectangle(
                arbusto.getPosicionX(),
                arbusto.getPosicionY(),
                ANCHO_ARBUSTO,
                ALTO_ARBUSTO);
    }

    private Rectangle hitboxRescate() {
        return new Rectangle(
                rescateX,
                rescateY,
                ANCHO_RESCATE,
                ALTO_RESCATE);
    }

    /*
     * Comprueba varios puntos de la hitbox contra la zona del
     * templo. Esto evita depender de métodos de colisión que
     * LugarEspecial no expone.
     */
    private boolean colisionaConTemplo(Rectangle hitbox) {

        float izquierda = hitbox.x;
        float derecha = hitbox.x + hitbox.width;
        float abajo = hitbox.y;
        float arriba = hitbox.y + hitbox.height;
        float centroX = hitbox.x + hitbox.width / 2f;
        float centroY = hitbox.y + hitbox.height / 2f;

        return estaEnTemplo(izquierda, abajo)
                || estaEnTemplo(derecha, abajo)
                || estaEnTemplo(izquierda, arriba)
                || estaEnTemplo(derecha, arriba)
                || estaEnTemplo(centroX, centroY)
                || estaEnTemplo(centroX, abajo)
                || estaEnTemplo(centroX, arriba)
                || estaEnTemplo(izquierda, centroY)
                || estaEnTemplo(derecha, centroY);
    }

    // ==========================================================
    // CONSTRUCCIÓN
    // ==========================================================

    public boolean puedeColocarEstructura(Estructura nueva) {

        if (nueva == null) {
            return false;
        }

        Rectangle hitbox = nueva.getHitbox();

        if (hitbox == null) {
            return false;
        }

        // Debe estar dentro de los límites del mapa.
        if (hitbox.x < 0 || hitbox.y < 0
                || hitbox.x + hitbox.width > ancho
                || hitbox.y + hitbox.height > alto) {
            return false;
        }

        // Todos los puntos de la estructura deben estar sobre pasto.
        if (!esTierra(hitbox.x, hitbox.y)
                || !esTierra(hitbox.x + hitbox.width, hitbox.y)
                || !esTierra(hitbox.x, hitbox.y + hitbox.height)
                || !esTierra(
                        hitbox.x + hitbox.width,
                        hitbox.y + hitbox.height)) {
            return false;
        }

        // Árboles.
        for (arbol a : arboles) {
            if (!a.estaTalado() && hitbox.overlaps(a.getHitbox())) {
                return false;
            }
        }

        // Rocas.
        for (roca r : rocas) {
            if (hitbox.overlaps(r.getHitbox())) {
                return false;
            }
        }

        // Plantas de fibra que todavía existen.
        for (planta p : plantas) {
            if (!p.estaAgotada() && hitbox.overlaps(hitboxPlanta(p))) {
                return false;
            }
        }

        // Los arbustos siguen ocupando espacio aunque no tengan bayas.
        for (arbustoBayas arbusto : arbustosBayas) {
            if (hitbox.overlaps(hitboxArbusto(arbusto))) {
                return false;
            }
        }

        // Estructura de rescate.
        if (hitbox.overlaps(hitboxRescate())) {
            return false;
        }

        // Templo.
        if (colisionaConTemplo(hitbox)) {
            return false;
        }

        // Otras estructuras construidas.
        for (Estructura existente : estructurasConstruidas) {
            if (hitbox.overlaps(existente.getHitbox())) {
                return false;
            }
        }

        return true;
    }

    public boolean agregarEstructura(Estructura nueva) {

        if (!puedeColocarEstructura(nueva)) {
            return false;
        }

        estructurasConstruidas.add(nueva);
        return true;
    }

    public List<Estructura> getEstructurasConstruidas() {
        return estructurasConstruidas;
    }

    public Estructura obtenerEstructuraCercana(
            float jugadorX, float jugadorY) {

        Estructura cercana = null;
        float distanciaMenor = Float.MAX_VALUE;

        for (Estructura estructura : estructurasConstruidas) {

            if (!estructura.estaCerca(jugadorX, jugadorY)) {
                continue;
            }

            float dx = jugadorX + 32f
                    - (estructura.getX() + estructura.getAncho() / 2f);

            float dy = jugadorY + 32f
                    - (estructura.getY() + estructura.getAlto() / 2f);

            float distanciaCuadrada = dx * dx + dy * dy;

            if (distanciaCuadrada < distanciaMenor) {
                distanciaMenor = distanciaCuadrada;
                cercana = estructura;
            }
        }

        return cercana;
    }

    public void dibujarEstructuras(SpriteBatch batch) {

        if (batch == null) {
            return;
        }

        for (Estructura estructura : estructurasConstruidas) {
            estructura.dibujar(batch);
        }
    }

    // ==========================================================
    // COMPROBAR PASTO
    // ==========================================================

    private boolean esTierra(float x, float y) {

        TiledMapTileLayer capaPasto =
                (TiledMapTileLayer) tiledMap.getLayers().get("pasto");

        TiledMapTileLayer capaPasto2 =
                (TiledMapTileLayer) tiledMap.getLayers().get("pasto 2");

        if (capaPasto == null && capaPasto2 == null) {
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
    // COLISIONES DEL JUGADOR
    // ==========================================================

    public boolean puedeCaminar(Rectangle hitbox) {

        if (hitbox == null) {
            return false;
        }

        if (hitbox.x < 0 || hitbox.y < 0
                || hitbox.x + hitbox.width > ancho
                || hitbox.y + hitbox.height > alto) {
            return false;
        }

        if (!esTierra(hitbox.x, hitbox.y)
                || !esTierra(hitbox.x + hitbox.width, hitbox.y)
                || !esTierra(hitbox.x, hitbox.y + hitbox.height)
                || !esTierra(
                        hitbox.x + hitbox.width,
                        hitbox.y + hitbox.height)) {
            return false;
        }

        // Árboles.
        for (arbol a : arboles) {
            if (!a.estaTalado() && hitbox.overlaps(a.getHitbox())) {
                return false;
            }
        }

        // Rocas.
        for (roca r : rocas) {
            if (hitbox.overlaps(r.getHitbox())) {
                return false;
            }
        }

        // Plantas de fibra.
        for (planta p : plantas) {
            if (!p.estaAgotada() && hitbox.overlaps(hitboxPlanta(p))) {
                return false;
            }
        }

        // Arbustos de bayas, tengan bayas o no.
        for (arbustoBayas arbusto : arbustosBayas) {
            if (hitbox.overlaps(hitboxArbusto(arbusto))) {
                return false;
            }
        }

        // Estructura de rescate.
        if (hitbox.overlaps(hitboxRescate())) {
            return false;
        }

        // Templo.
        if (colisionaConTemplo(hitbox)) {
            return false;
        }

        // Estructuras construidas.
        for (Estructura estructura : estructurasConstruidas) {
            if (hitbox.overlaps(estructura.getHitbox())) {
                return false;
            }
        }

        return true;
    }

    // ==========================================================
    // BUSCAR POSICIÓN LIBRE
    // ==========================================================

    public float[] buscarPosicionLibre(float x, float y) {

        final float paso = 16f;

        for (int radio = 0; radio <= 40; radio++) {
            for (int dx = -radio; dx <= radio; dx++) {
                for (int dy = -radio; dy <= radio; dy++) {

                    if (Math.max(Math.abs(dx), Math.abs(dy)) != radio) {
                        continue;
                    }

                    float px = x + dx * paso;
                    float py = y + dy * paso;

                    Rectangle pies = new Rectangle(
                            px + 20f, py, 24f, 12f);

                    if (puedeCaminar(pies)) {
                        return new float[] { px, py };
                    }
                }
            }
        }

        return new float[] { x, y };
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
    // ACTUALIZAR ARBUSTOS
    // ==========================================================

    public void actualizarArbustosBayas(float delta) {

        for (arbustoBayas arbusto : arbustosBayas) {
            arbusto.actualizar(delta);
        }
    }

    // ==========================================================
    // DIMENSIONES Y ZONAS
    // ==========================================================

    public boolean estaDentro(float x, float y) {
        return x >= 0 && x <= ancho && y >= 0 && y <= alto;
    }

    public Zona obtenerZona(float x, float y) {

        for (Zona zona : zonas) {
            if (zona.contiene(x, y)) {
                return zona;
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

    // ==========================================================
    // ÁRBOLES
    // ==========================================================

    public List<arbol> getArboles() {
        return arboles;
    }

    public void eliminarArbol(arbol arbol) {

        if (arbol != null) {
            arbol.dispose();
            arboles.remove(arbol);
        }
    }

    public arbol obtenerArbolCercano(
            float jugadorX, float jugadorY, float distanciaMaxima) {

        arbol cercano = null;
        float distanciaMenor = distanciaMaxima;

        for (arbol actual : arboles) {
            if (actual.estaTalado()) {
                continue;
            }

            float dx = jugadorX - actual.getPosicionX();
            float dy = jugadorY - actual.getPosicionY();
            float distancia = (float) Math.sqrt(dx * dx + dy * dy);

            if (distancia <= distanciaMenor) {
                distanciaMenor = distancia;
                cercano = actual;
            }
        }

        return cercano;
    }
    
    public Fogata obtenerFogataEncendidaCercana(
            float x, float y, float distanciaMaxima) {

        Fogata cercana = null;
        float distanciaMenor = distanciaMaxima;

        for (Estructura estructura : estructurasConstruidas) {

            if (!(estructura instanceof Fogata)) {
                continue;
            }

            Fogata fogata = (Fogata) estructura;

            if (!fogata.estaEncendida()) {
                continue;
            }

            float centroX = fogata.getX() + fogata.getAncho() / 2f;
            float centroY = fogata.getY() + fogata.getAlto() / 2f;

            float dx = x - centroX;
            float dy = y - centroY;

            float distancia = (float) Math.sqrt(dx * dx + dy * dy);

            if (distancia < distanciaMenor) {
                distanciaMenor = distancia;
                cercana = fogata;
            }
        }

        return cercana;
    }

    public boolean hayFogataEncendidaCerca(
            float x, float y, float distanciaMaxima) {

        return obtenerFogataEncendidaCercana(
                x, y, distanciaMaxima) != null;
    }

    // ==========================================================
    // ROCAS
    // ==========================================================

    public List<roca> getRocas() {
        return rocas;
    }

    public void eliminarRoca(roca roca) {

        if (roca != null) {
            roca.dispose();
            rocas.remove(roca);
        }
    }

    public roca obtenerRocaCercana(
            float jugadorX, float jugadorY, float distanciaMaxima) {

        roca cercana = null;
        float distanciaMenor = distanciaMaxima;

        for (roca actual : rocas) {
            float dx = jugadorX - actual.getPosicionX();
            float dy = jugadorY - actual.getPosicionY();
            float distancia = (float) Math.sqrt(dx * dx + dy * dy);

            if (distancia <= distanciaMenor) {
                distanciaMenor = distancia;
                cercana = actual;
            }
        }

        return cercana;
    }

    // ==========================================================
    // PLANTAS
    // ==========================================================

    public List<planta> getPlantas() {
        return plantas;
    }

    public void eliminarPlanta(planta planta) {

        if (planta != null) {
            planta.dispose();
            plantas.remove(planta);
        }
    }

    public planta obtenerPlantaCercana(
            float jugadorX, float jugadorY, float distanciaMaxima) {

        planta cercana = null;
        float distanciaMenor = distanciaMaxima;

        for (planta actual : plantas) {
            if (actual.estaAgotada()) {
                continue;
            }

            float dx = jugadorX - actual.getPosicionX();
            float dy = jugadorY - actual.getPosicionY();
            float distancia = (float) Math.sqrt(dx * dx + dy * dy);

            if (distancia <= distanciaMenor) {
                distanciaMenor = distancia;
                cercana = actual;
            }
        }

        return cercana;
    }

    // ==========================================================
    // ARBUSTOS DE BAYAS
    // ==========================================================

    public List<arbustoBayas> getArbustosBayas() {
        return arbustosBayas;
    }

    public arbustoBayas obtenerArbustoBayasCercano(
            float jugadorX, float jugadorY, float distanciaMaxima) {

        arbustoBayas cercano = null;
        float distanciaMenor = distanciaMaxima;

        for (arbustoBayas arbusto : arbustosBayas) {
            if (!arbusto.tieneBayas()) {
                continue;
            }

            float dx = jugadorX - arbusto.getPosicionX();
            float dy = jugadorY - arbusto.getPosicionY();
            float distancia = (float) Math.sqrt(dx * dx + dy * dy);

            if (distancia <= distanciaMenor) {
                distanciaMenor = distancia;
                cercano = arbusto;
            }
        }

        return cercano;
    }

    // ==========================================================
    // LIBERAR RECURSOS
    // ==========================================================

    public void dispose() {

        if (arboles != null) {
            for (arbol a : arboles) {
                a.dispose();
            }
        }

        if (rocas != null) {
            for (roca r : rocas) {
                r.dispose();
            }
        }

        if (plantas != null) {
            for (planta p : plantas) {
                p.dispose();
            }
        }

        if (arbustosBayas != null) {
            for (arbustoBayas arbusto : arbustosBayas) {
                arbusto.dispose();
            }
        }

        if (estructuraRescate != null) {
            estructuraRescate.dispose();
        }

        if (estructurasConstruidas != null) {
            for (Estructura estructura : estructurasConstruidas) {
                estructura.dispose();
            }

            estructurasConstruidas.clear();
        }

        if (mapRenderer != null) {
            mapRenderer.dispose();
        }

        if (tiledMap != null) {
            tiledMap.dispose();
        }
    }
}
