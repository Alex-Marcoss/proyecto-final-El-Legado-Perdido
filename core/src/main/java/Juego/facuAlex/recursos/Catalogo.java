package Juego.facuAlex.recursos;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;

public final class Catalogo {

    // ---- Nombres (usá siempre estas constantes) ----
    public static final String MADERA = "Madera";
    public static final String PIEDRA = "Piedra";
    public static final String FIBRA = "Fibra";
    public static final String BAYAS = "Bayas";
    public static final String CARNE = "Carne";
    public static final String CARNE_COCIDA = "Carne cocida";

    public static final String HILO = "Hilo";
    public static final String LANA = "Lana";
    public static final String CUERO = "Cuero";
    public static final String CUERDA = "Cuerda";
    public static final String PALOS = "Palos";
    public static final String PAPEL = "Papel";
    public static final String VENDAJE = "Vendaje";
    public static final String MENA_HIERRO = "Mena de hierro";
    public static final String HIERRO = "Hierro";
    public static final String MENA_ORO = "Mena de oro";
    public static final String ORO = "Oro";
    public static final String CARBON = "Carbon";

    private static final Map<String, String> ICONOS =
            new HashMap<String, String>();

    static {
        ICONOS.put(MADERA, "objetos/madera.png");
        ICONOS.put(PIEDRA, "objetos/piedra.png");
        ICONOS.put(FIBRA, "objetos/fibra.png");
        ICONOS.put(BAYAS, "objetos/baya.png");
        ICONOS.put(CARNE, "objetos/carne.png");
        ICONOS.put(CARNE_COCIDA, "objetos/carne_cocida.png");

        ICONOS.put(HILO, "objetos/hilo.png");
        ICONOS.put(LANA, "objetos/lana.png");
        ICONOS.put(CUERO, "objetos/cuero.png");
        ICONOS.put(CUERDA, "objetos/cuerda.png");
        ICONOS.put(PALOS, "objetos/palos.png");
        ICONOS.put(PAPEL, "objetos/papel.png");
        ICONOS.put(VENDAJE, "objetos/vendaje.png");
        ICONOS.put(MENA_HIERRO, "objetos/hierro.png");
        ICONOS.put(HIERRO, "objetos/lingoteHierro.png");
        ICONOS.put(MENA_ORO, "objetos/oro.png");
        ICONOS.put(ORO, "objetos/lingoteOro.png");
        ICONOS.put(CARBON, "objetos/carbon.png");
    }

    private Catalogo() {}

    public static String rutaIcono(String nombre) {
        return ICONOS.get(nombre);
    }

    // Crea cualquier recurso conocido. Si el PNG todavía no existe,
    // el item se crea igual, solo que sin ícono.
    public static Recursos crear(String nombre, int cantidad) {

        if (nombre.equals(CARNE_COCIDA)) {
            return carneCocida(cantidad);
        }

        Recursos r = new Recursos(nombre, cantidad);
        ponerIcono(r, nombre);

        return r;
    }

    public static Comida carneCocida(int cantidad) {

        Comida c = new Comida(CARNE_COCIDA, cantidad, 40);
        ponerIcono(c, CARNE_COCIDA);

        return c;
    }

    private static void ponerIcono(Item item, String nombre) {

        String ruta = ICONOS.get(nombre);

        if (ruta != null && Gdx.files.internal(ruta).exists()) {
            item.cargarIcono(ruta);
        }
    }
}