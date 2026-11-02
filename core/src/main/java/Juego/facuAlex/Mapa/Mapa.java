
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

import Juego.facuAlex.recursos.Mena;
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
   

    private static final int CANTIDAD_ROCAS = 30;
    

    private static final int CANTIDAD_PLANTAS = 40;
   

    private static final int CANTIDAD_ARBUSTOS_BAYAS = 20;
    
 // Punto de aparición del jugador y ubicación del templo (Principal usa estas mismas)
    public static final float SPAWN_X = 3072f;
    public static final float SPAWN_Y = 3072f;
    public static final float TEMPLO_DX = 300f;
    public static final float TEMPLO_DY = 100f;

    // Espacio libre mínimo entre los bordes de dos objetos
    private static final float MARGEN_OBJETOS = 90f;

    // "Tamaño" de cada cosa a efectos de separación
    private static final float RADIO_ARBOL = 50f;
    private static final float RADIO_ROCA = 40f;
    private static final float RADIO_MENA = 40f;
    private static final float RADIO_PLANTA = 20f;
    private static final float RADIO_ARBUSTO = 30f;

    // Zonas reservadas (radio físico de lo que ya está ahí)
    private static final float RADIO_SPAWN = 60f;
    private static final float RADIO_RESCATE = 90f;
    private static final float RADIO_TEMPLO = 260f;     // ajustalo al tamaño real del templo
    private static final float TEMPLO_CENTRO_DX = 450f; // centro aproximado del templo
    private static final float TEMPLO_CENTRO_DY = 250f; // respecto a SPAWN

    private static final int CANTIDAD_MENAS_HIERRO = 10;
    private static final int CANTIDAD_MENAS_ORO = 5;
    private static final int CANTIDAD_MENAS_CARBON = 8;
    
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

        reservarZonas();

        crearArboles();
        crearRocas();
        crearMenas();
        crearPlantas();
        crearArbustosBayas();
    }

    public EstructuraRescateMundo getEstructuraRescate() {
        return estructuraRescate;
    }

    // ==========================================================
    // CREACIÓN DE RECURSOS
    // ==========================================================

    

    private static class Ocupado {

        final float x, y, radio;

        Ocupado(float x, float y, float radio) {
            this.x = x;
            this.y = y;
            this.radio = radio;
        }
    }

    private final List<Ocupado> ocupados = new ArrayList<>();

 // Lugares que nada puede ocupar ni tener pegados
    private void reservarZonas() {

        // Estructura de rescate
        ocupados.add(new Ocupado(
                rescateX + ANCHO_RESCATE / 2f,
                rescateY + ALTO_RESCATE / 2f,
                RADIO_RESCATE));

        // Donde aparece el jugador
        ocupados.add(new Ocupado(SPAWN_X, SPAWN_Y, RADIO_SPAWN));

        // Templo que dibuja Principal
        ocupados.add(new Ocupado(
                SPAWN_X + TEMPLO_CENTRO_DX,
                SPAWN_Y + TEMPLO_CENTRO_DY,
                RADIO_TEMPLO));

        // Zona de templo propia del mapa (LugarEspecial)
        ocupados.add(new Ocupado(
                ancho * 0.75f + 50f,
                alto * 0.65f + 50f,
                70f));
    }

    private boolean hayEspacio(float x, float y, float radio) {

        for (Ocupado o : ocupados) {

            float dx = x - o.x;
            float dy = y - o.y;
            float minima = radio + o.radio + MARGEN_OBJETOS;

            if (dx * dx + dy * dy < minima * minima) {
                return false;
            }
        }

        return true;
    }

    // Coloca "cantidad" objetos en tierra y con espacio libre alrededor
    private int generar(int cantidad, float radio,
                        java.util.function.BiConsumer<Float, Float> fabrica) {

        int creados = 0;
        int intentos = 0;

        while (creados < cantidad && intentos < 10000) {

            intentos++;

            float x = 100 + random.nextFloat() * (ancho - 200);
            float y = 100 + random.nextFloat() * (alto - 200);

            if (!esTierra(x, y) || !hayEspacio(x, y, radio)) {
                continue;
            }

            fabrica.accept(x, y);
            ocupados.add(new Ocupado(x, y, radio));
            creados++;
        }

        return creados;
    }

    private void crearArboles() {

        int n = generar(CANTIDAD_ARBOLES, RADIO_ARBOL,
                (x, y) -> arboles.add(new arbol(x, y)));

        System.out.println("Total de árboles creados: " + n);
    }

    private void crearRocas() {

        int n = generar(CANTIDAD_ROCAS, RADIO_ROCA,
                (x, y) -> rocas.add(new roca(x, y)));

        System.out.println("Total de rocas creadas: " + n);
    }

    // Las menas viven en la misma lista que las rocas (son rocas especiales)
    private void crearMenas() {

        int hierro = generar(CANTIDAD_MENAS_HIERRO, RADIO_MENA,
                (x, y) -> rocas.add(new Mena(Mena.Tipo.HIERRO, x, y)));

        int oro = generar(CANTIDAD_MENAS_ORO, RADIO_MENA,
                (x, y) -> rocas.add(new Mena(Mena.Tipo.ORO, x, y)));

        int carbon = generar(CANTIDAD_MENAS_CARBON, RADIO_MENA,
                (x, y) -> rocas.add(new Mena(Mena.Tipo.CARBON, x, y)));

        System.out.println("Menas creadas: " + hierro + " de hierro, "
                + oro + " de oro, " + carbon + " de carbon");
    }

    private void crearPlantas() {

        int n = generar(CANTIDAD_PLANTAS, RADIO_PLANTA,
                (x, y) -> plantas.add(new planta(x, y)));

        System.out.println("Total de plantas creadas: " + n);
    }

    private void crearArbustosBayas() {

        int n = generar(CANTIDAD_ARBUSTOS_BAYAS, RADIO_ARBUSTO,
                (x, y) -> arbustosBayas.add(new arbustoBayas(x, y)));

        System.out.println("Total de arbustos de bayas creados: " + n);
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
