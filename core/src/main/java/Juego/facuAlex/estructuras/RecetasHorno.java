package Juego.facuAlex.estructuras;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import Juego.facuAlex.recursos.Catalogo;
import Juego.facuAlex.recursos.Item;

public final class RecetasHorno {

    public static final class Receta {

        public final String entrada;
        public final String salida;
        public final float tiempo;      // segundos para cocinar una unidad

        private final Supplier<Item> fabrica;

        private Receta(String entrada, String salida, float tiempo,
                       Supplier<Item> fabrica) {

            this.entrada = entrada;
            this.salida = salida;
            this.tiempo = tiempo;
            this.fabrica = fabrica;
        }

        public Item crearSalida() {
            return fabrica.get();
        }
    }

    private static final Map<String, Receta> RECETAS =
            new HashMap<String, Receta>();

    // nombre del combustible -> segundos que dura UNA unidad
    private static final Map<String, Float> COMBUSTIBLES =
            new HashMap<String, Float>();

    static {

        // ---- Recetas ----
        receta(Catalogo.CARNE, Catalogo.CARNE_COCIDA, 8f,
                () -> Catalogo.carneCocida(1));

        receta(Catalogo.MENA_HIERRO, Catalogo.HIERRO, 10f,
                () -> Catalogo.crear(Catalogo.HIERRO, 1));

        receta(Catalogo.MENA_ORO, Catalogo.ORO, 12f,
                () -> Catalogo.crear(Catalogo.ORO, 1));

        receta(Catalogo.MADERA, Catalogo.CARBON, 8f,
                () -> Catalogo.crear(Catalogo.CARBON, 1));

        // ---- Combustibles ----
        COMBUSTIBLES.put(Catalogo.MADERA, 16f);
        COMBUSTIBLES.put(Catalogo.FIBRA, 5f);
        COMBUSTIBLES.put(Catalogo.CARBON, 40f);
    }

    private RecetasHorno() {}

    private static void receta(String entrada, String salida, float tiempo,
                               Supplier<Item> fabrica) {

        RECETAS.put(entrada, new Receta(entrada, salida, tiempo, fabrica));
    }

    public static Receta getReceta(String nombreEntrada) {
        return RECETAS.get(nombreEntrada);
    }

    // 0 si no sirve como combustible
    public static float getDuracionCombustible(String nombre) {

        Float d = COMBUSTIBLES.get(nombre);

        return d == null ? 0f : d;
    }
}