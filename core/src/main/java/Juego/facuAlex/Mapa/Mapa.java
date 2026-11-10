
package Juego.facuAlex.Mapa;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
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
   

    private static final int CANTIDAD_ROCAS = 40;
    

    private static final int CANTIDAD_PLANTAS = 40;
   

    private static final int CANTIDAD_ARBUSTOS_BAYAS = 15;
    
    private static final int CANTIDAD_MENAS_HIERRO = 15;
    
    private static final int CANTIDAD_MENAS_ORO = 5;
    
    private static final int CANTIDAD_MENAS_CARBON = 10;
    
 // Punto de aparición del jugador y ubicación del templo (Principal usa estas mismas)
    public static final float SPAWN_X = 3072f;
    public static final float SPAWN_Y = 3072f;
    
    private float temploX;
    private float temploY;
    


    // Ubicación aleatoria del templo y de la estructura de rescate
    private static final float BORDE_UBICACION = 400f;     // distancia mínima a los bordes del mapa
    private static final float DIST_MIN_SPAWN = 500f;      // distancia mínima al spawn del jugador
    private static final float DIST_MIN_ENTRE = 600f;      // distancia mínima entre templo y rescate
    private static final int INTENTOS_UBICACION = 5000;

    // Espacio libre mínimo entre los bordes de dos objetos
    private static final float MARGEN_OBJETOS = 90f;
    
    // Distancia mínima entre un objeto y el agua
    private static final float MARGEN_AGUA = 40f;
    

    // "Tamaño" de cada cosa a efectos de separación
    private static final float RADIO_ARBOL = 50f;
    private static final float RADIO_ROCA = 40f;
    private static final float RADIO_MENA = 40f;
    private static final float RADIO_PLANTA = 20f;
    private static final float RADIO_ARBUSTO = 30f;

    // Zonas reservadas (radio físico de lo que ya está ahí)
    private static final float RADIO_SPAWN = 60f;
    private static final float RADIO_RESCATE = 90f;
 
 // Tamaño real del templo en el mundo (PNG 470 x 256 con la escala de Principal)
    private static final float ESCALA_TEMPLO = 1.5f;   // tiene que ser igual a Principal.ESCALA_TEMPLO
    private static final float TEMPLO_ANCHO = 470f * ESCALA_TEMPLO;
    private static final float TEMPLO_ALTO = 256f * ESCALA_TEMPLO;

    // Centro del templo respecto a su esquina inferior izquierda
    private static final float TEMPLO_CENTRO_OX = TEMPLO_ANCHO / 2f;
    private static final float TEMPLO_CENTRO_OY = TEMPLO_ALTO / 2f;

    // Radio que se reserva alrededor del templo (para recursos y distancias)
    private static final float RADIO_TEMPLO = 360f;

    // Pasto que tiene que haber alrededor del templo (para que se pueda rodear)
    private static final float MARGEN_TIERRA_TEMPLO = 64f;

    
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
    
 // Capas de suelo de piedra: se detectan por el nombre (cualquier capa de tiles
 // cuyo nombre contenga alguna de estas palabras)
    private static final String[] PALABRAS_PIEDRA = { "piedra", "roca", "stone", "rock" };

    private final List<TiledMapTileLayer> capasPiedra = new ArrayList<>();
    
    private Zona[] zonas;
    private LugarEspecial templo;
    private EstructuraRescateMundo estructuraRescate;

    private float rescateX;
    private float rescateY;

    private TiledMap tiledMap;
    private TiledMapTileLayer capaPasto;
    private TiledMapTileLayer capaPasto2;
    private int tileAnchoMapa;
    private int tileAltoMapa;
    
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

    	prepararCapas();

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

    random = new Random();

    zonas = new Zona[3];

    zonas[0] = new Zona("Zona 1", 0, 0, ancho / 3, alto);
    zonas[1] = new Zona("Zona 2", ancho / 3, 0, ancho / 3, alto);
    zonas[2] = new Zona(
            "Zona 3", (ancho / 3) * 2, 0, ancho / 3, alto);

    // Templo y estructura de rescate en lugares al azar
    ubicarTemploYRescate();

    // La zona "Templo" del mapa acompaña al templo real
    templo = new LugarEspecial(
            "Templo",
            temploX + TEMPLO_CENTRO_OX - 50f,
            temploY + TEMPLO_CENTRO_OY - 50f,
            100, 100);

    estructuraRescate = new EstructuraRescateMundo(
            rescateX, rescateY);

    arboles = new ArrayList<>();
    rocas = new ArrayList<>();
    plantas = new ArrayList<>();
    arbustosBayas = new ArrayList<>();

    reservarZonas();

    crearArboles();
    crearRocas();
    crearMenas();
    crearPlantas();
    crearArbustosBayas();
    }
    
 // ==========================================================
 // UBICACIÓN ALEATORIA DE TEMPLO Y ESTRUCTURA DE RESCATE
 // ==========================================================

 private float distancia(float x1, float y1, float x2, float y2) {

     float dx = x1 - x2;
     float dy = y1 - y2;

     return (float) Math.sqrt(dx * dx + dy * dy);
 }

 // ¿Todo el círculo (centro, mitad del radio y borde) está sobre pasto?
private boolean rectanguloDeTierra(float x, float y, float w, float h,
                                    float margen) {

    float x0 = x - margen;
    float y0 = y - margen;
    float x1 = x + w + margen;
    float y1 = y + h + margen;

    int pasosX = Math.max(2, (int) Math.ceil((x1 - x0) / 96f));
    int pasosY = Math.max(2, (int) Math.ceil((y1 - y0) / 96f));

    for (int i = 0; i <= pasosX; i++) {
        for (int j = 0; j <= pasosY; j++) {

            float px = x0 + (x1 - x0) * i / pasosX;
            float py = y0 + (y1 - y0) * j / pasosY;

            if (!esTierra(px, py)) {
                return false;
            }
        }
    }

    return true;
}

// ¿Todo el círculo (centro, mitad del radio y borde) está sobre pasto?
private boolean zonaDeTierra(float cx, float cy, float radio) {

    if (!esTierra(cx, cy)) {
        return false;
    }

    for (int grado = 0; grado < 360; grado += 20)  {

        float rad = (float) Math.toRadians(grado);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        if (!esTierra(cx + cos * radio, cy + sin * radio)
                || !esTierra(cx + cos * radio * 0.5f,
                             cy + sin * radio * 0.5f)) {
            return false;
        }
    }

    return true;
}

// Busca un lugar para el templo. Devuelve true si lo encontró.
private boolean buscarLugarTemplo(float margenTierra, float distMinSpawn,
                                  float borde) {

    float rangoX = ancho - 2f * borde - TEMPLO_ANCHO;
    float rangoY = alto - 2f * borde - TEMPLO_ALTO;

    if (rangoX <= 0 || rangoY <= 0) {
        return false;
    }

    for (int i = 0; i < INTENTOS_UBICACION; i++) {

        float x = borde + random.nextFloat() * rangoX;
        float y = borde + random.nextFloat() * rangoY;

        float cx = x + TEMPLO_CENTRO_OX;
        float cy = y + TEMPLO_CENTRO_OY;

        if (distancia(cx, cy, SPAWN_X, SPAWN_Y)
                < RADIO_TEMPLO + distMinSpawn) {
            continue;
        }

        if (!rectanguloDeTierra(x, y, TEMPLO_ANCHO, TEMPLO_ALTO,
                                margenTierra)) {
            continue;
        }

        temploX = x;
        temploY = y;
        return true;
    }

    return false;
}

private void ubicarTemploYRescate() {

    // ----- Templo: primero estricto, después cada vez más permisivo -----
    boolean temploOk =
            buscarLugarTemplo(MARGEN_TIERRA_TEMPLO, DIST_MIN_SPAWN,
                              BORDE_UBICACION)
            || buscarLugarTemplo(0f, DIST_MIN_SPAWN / 2f, 150f)
            || buscarLugarTemplo(0f, 0f, 0f);

    // Último recurso: al lado del jugador, como estaba antes
    if (!temploOk) {
        temploX = SPAWN_X + 300f;
        temploY = SPAWN_Y + 100f;

        System.out.println(
                "AVISO: no se encontro un lugar de pasto para el templo, "
                + "se usa la posicion fija.");
    }

    float temploCx = temploX + TEMPLO_CENTRO_OX;
    float temploCy = temploY + TEMPLO_CENTRO_OY;

    // ----- Estructura de rescate -----
    boolean rescateOk = false;

    for (int i = 0; i < INTENTOS_UBICACION && !rescateOk; i++) {

        float cx = BORDE_UBICACION
                + random.nextFloat() * (ancho - 2f * BORDE_UBICACION);

        float cy = BORDE_UBICACION
                + random.nextFloat() * (alto - 2f * BORDE_UBICACION);

        if (distancia(cx, cy, SPAWN_X, SPAWN_Y)
                < RADIO_RESCATE + DIST_MIN_SPAWN) {
            continue;
        }

        if (distancia(cx, cy, temploCx, temploCy)
                < RADIO_TEMPLO + RADIO_RESCATE + DIST_MIN_ENTRE) {
            continue;
        }

        if (!zonaDeTierra(cx, cy, RADIO_RESCATE)) {
            continue;
        }

        rescateX = cx - ANCHO_RESCATE / 2f;
        rescateY = cy - ALTO_RESCATE / 2f;
        rescateOk = true;
    }

    if (!rescateOk) {
        rescateX = ancho * 0.50f;
        rescateY = alto * 0.50f;

        System.out.println(
                "AVISO: no se encontro un lugar para la estructura de "
                + "rescate, se usa el centro del mapa.");
    }

    System.out.println("Templo en: " + (int) temploX + ", " + (int) temploY
            + " | Rescate en: " + (int) rescateX + ", " + (int) rescateY);
}

 public float getTemploX() {
     return temploX;
 }

 public float getTemploY() {
     return temploY;
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
                temploX + TEMPLO_CENTRO_OX,
                temploY + TEMPLO_CENTRO_OY,
                RADIO_TEMPLO));
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

    // permitirPiedra = false: no aparece nada que toque un sector de piedra.
    private int generar(int cantidad, float radio, boolean permitirPiedra,
                    java.util.function.BiConsumer<Float, Float> fabrica) {

    int creados = 0;
    int intentos = 0;

    while (creados < cantidad && intentos < 10000) {

        intentos++;

        float x = 100 + random.nextFloat() * (ancho - 200);
        float y = 100 + random.nextFloat() * (alto - 200);

        if (!zonaDeTierra(x, y, radio + MARGEN_AGUA)
                || !hayEspacio(x, y, radio)) {
            continue;
        }

        if (!permitirPiedra && hayPiedraEnZona(x, y, radio)) {
            continue;
        }

        fabrica.accept(x, y);
        ocupados.add(new Ocupado(x, y, radio));
        creados++;
    }

    return creados;
}

    private void crearArboles() {

        int n = generar(CANTIDAD_ARBOLES, RADIO_ARBOL, false,
                (x, y) -> arboles.add(new arbol(x, y)));

        System.out.println("Total de árboles creados: " + n);
    }

    private void crearRocas() {

        int n = generar(CANTIDAD_ROCAS, RADIO_ROCA, true,
                (x, y) -> rocas.add(new roca(x, y)));

        System.out.println("Total de rocas creadas: " + n);
    }

    // Las menas viven en la misma lista que las rocas (son rocas especiales)
    private void crearMenas() {

        int hierro = generar(CANTIDAD_MENAS_HIERRO, RADIO_MENA, false,
                (x, y) -> rocas.add(new Mena(Mena.Tipo.HIERRO, x, y)));

        int oro = generar(CANTIDAD_MENAS_ORO, RADIO_MENA, true,
                (x, y) -> rocas.add(new Mena(Mena.Tipo.ORO, x, y)));

        int carbon = generar(CANTIDAD_MENAS_CARBON, RADIO_MENA, false,
                (x, y) -> rocas.add(new Mena(Mena.Tipo.CARBON, x, y)));

        System.out.println("Menas creadas: " + hierro + " de hierro, "
                + oro + " de oro, " + carbon + " de carbon");
    }

    private void crearPlantas() {

        int n = generar(CANTIDAD_PLANTAS, RADIO_PLANTA, false,
                (x, y) -> plantas.add(new planta(x, y)));

        System.out.println("Total de plantas creadas: " + n);
    }

    private void crearArbustosBayas() {

        int n = generar(CANTIDAD_ARBUSTOS_BAYAS, RADIO_ARBUSTO, false,
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

    // Solo se comprueban los objetos que estén a menos de esta distancia
	private static final float RADIO_BUSQUEDA = 250f;
	
	public boolean puedeColocarEstructura(Estructura nueva) {
	
	    if (nueva == null) {
	        return false;
	    }
	
	    Rectangle hitbox = nueva.getHitbox();
	
	    if (hitbox == null) {
	        return false;
	    }
	
	    return puedeCaminar(hitbox);
	}

	

// ¿Está lejos este objeto como para ni mirar su hitbox?
private boolean lejos(Rectangle h, float objetoX, float objetoY) {

    float dx = objetoX - (h.x + h.width / 2f);
    float dy = objetoY - (h.y + h.height / 2f);

    return dx * dx + dy * dy > RADIO_BUSQUEDA * RADIO_BUSQUEDA;
}

private boolean hayObstaculo(Rectangle hitbox) {

    for (arbol a : arboles) {
        if (!a.estaTalado()
                && !lejos(hitbox, a.getPosicionX(), a.getPosicionY())
                && hitbox.overlaps(a.getHitbox())) {
            return true;
        }
    }

    for (roca r : rocas) {
        if (!lejos(hitbox, r.getPosicionX(), r.getPosicionY())
                && hitbox.overlaps(r.getHitbox())) {
            return true;
        }
    }

    for (planta p : plantas) {
        if (!p.estaAgotada()
                && !lejos(hitbox, p.getPosicionX(), p.getPosicionY())
                && hitbox.overlaps(hitboxPlanta(p))) {
            return true;
        }
    }

    // Los arbustos ocupan espacio tengan bayas o no
    for (arbustoBayas arbusto : arbustosBayas) {
        if (!lejos(hitbox, arbusto.getPosicionX(), arbusto.getPosicionY())
                && hitbox.overlaps(hitboxArbusto(arbusto))) {
            return true;
        }
    }

    if (hitbox.overlaps(hitboxRescate())) {
        return true;
    }

    if (colisionaConTemplo(hitbox)) {
        return true;
    }

    for (Estructura estructura : estructurasConstruidas) {
        if (hitbox.overlaps(estructura.getHitbox())) {
            return true;
        }
    }

    return false;
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

        // Fuera del mapa por abajo o por la izquierda (evita el redondeo a tile 0)
        if (x < 0 || y < 0) {
            return false;
        }

        // Seguridad: si todavía no se cargaron las capas, se cargan acá
        if (tileAnchoMapa <= 0 || tileAltoMapa <= 0) {
            prepararCapas();
        }

        int tileX = (int) (x / tileAnchoMapa);
        int tileY = (int) (y / tileAltoMapa);

        return hayCelda(capaPasto, tileX, tileY)
                || hayCelda(capaPasto2, tileX, tileY);
    }

    private boolean hayCelda(TiledMapTileLayer capa, int tileX, int tileY) {

        if (capa == null) {
            return false;
        }

        TiledMapTileLayer.Cell celda = capa.getCell(tileX, tileY);

        return celda != null && celda.getTile() != null;
    }
    
 // ¿Hay suelo de piedra en este punto?
    private boolean esPiedra(float x, float y) {

        if (capasPiedra.isEmpty() || x < 0 || y < 0) {
            return false;
        }

        int tileX = (int) (x / tileAnchoMapa);
        int tileY = (int) (y / tileAltoMapa);

        for (TiledMapTileLayer capa : capasPiedra) {

            if (hayCelda(capa, tileX, tileY)) {
                return true;
            }
        }

        return false;
    }

    // ¿Hay piedra en alguna parte del círculo (centro, mitad del radio y borde)?
    private boolean hayPiedraEnZona(float cx, float cy, float radio) {

        if (capasPiedra.isEmpty()) {
            return false;
        }

        if (esPiedra(cx, cy)) {
            return true;
        }

        for (int grado = 0; grado < 360; grado += 20) {

            float rad = (float) Math.toRadians(grado);
            float cos = (float) Math.cos(rad);
            float sin = (float) Math.sin(rad);

            if (esPiedra(cx + cos * radio, cy + sin * radio)
                    || esPiedra(cx + cos * radio * 0.5f,
                                cy + sin * radio * 0.5f)) {
                return true;
            }
        }

        return false;
    }

    // ==========================================================
    // COLISIONES DEL JUGADOR
    // ==========================================================

    public boolean puedeCaminar(Rectangle hitbox) {

	    if (hitbox == null) {
	        return false;
	    }

	    // Dentro de los límites del mapa
	    if (hitbox.x < 0 || hitbox.y < 0
	            || hitbox.x + hitbox.width > ancho
	            || hitbox.y + hitbox.height > alto) {
	        return false;
	    }

	    // Las cuatro esquinas sobre pasto
	    if (!esTierra(hitbox.x, hitbox.y)
	            || !esTierra(hitbox.x + hitbox.width, hitbox.y)
	            || !esTierra(hitbox.x, hitbox.y + hitbox.height)
	            || !esTierra(hitbox.x + hitbox.width,
	                         hitbox.y + hitbox.height)) {
	        return false;
	    }

	    return !hayObstaculo(hitbox);
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
    
   
    // Carga las capas de pasto y de piedra y el tamaño de tile una sola vez
	private void prepararCapas() {
	
	    tileAnchoMapa = tiledMap.getProperties()
	            .get("tilewidth", Integer.class);
	
	    tileAltoMapa = tiledMap.getProperties()
	            .get("tileheight", Integer.class);
	
	    capaPasto = (TiledMapTileLayer) tiledMap.getLayers().get("pasto");
	    capaPasto2 = (TiledMapTileLayer) tiledMap.getLayers().get("pasto 2");
	
	    capasPiedra.clear();
	
	    StringBuilder nombres = new StringBuilder();
	
	    for (MapLayer capa : tiledMap.getLayers()) {
	
	        nombres.append("\"").append(capa.getName()).append("\" ");
	
	        if (!(capa instanceof TiledMapTileLayer)
	                || capa == capaPasto
	                || capa == capaPasto2) {
	            continue;
	        }
	
	        String nombre = capa.getName() == null
	                ? "" : capa.getName().toLowerCase();
	
	        for (String palabra : PALABRAS_PIEDRA) {
	
	            if (nombre.contains(palabra)) {
	                capasPiedra.add((TiledMapTileLayer) capa);
	                break;
	            }
	        }
	    }
	
	    System.out.println("Capas del mapa: " + nombres);
	
	    if (capasPiedra.isEmpty()) {
	
	        System.out.println(
	                "AVISO: no se detecto ninguna capa de piedra. "
	                + "Cambia PALABRAS_PIEDRA por parte del nombre de la capa.");
	
	    } else {
	
	        System.out.println(
	                "Capas de piedra detectadas: " + capasPiedra.size());
	    }
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
