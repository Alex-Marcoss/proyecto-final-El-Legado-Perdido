package Juego.facuAlex.sistemas;

import java.util.ArrayList;
import java.util.List;

public final class Luces {

    public static final class Luz {
        public final float x, y, radio, intensidad;

        Luz(float x, float y, float radio, float intensidad) {
            this.x = x;
            this.y = y;
            this.radio = radio;
            this.intensidad = intensidad;
        }
    }

    private static final int MAXIMO = 32;
    private static final List<Luz> luces = new ArrayList<Luz>();

    private Luces() {}

    // intensidad: 0 a 1
    public static void agregar(float x, float y, float radio, float intensidad) {
        if (luces.size() < MAXIMO) {
            luces.add(new Luz(x, y, radio, intensidad));
        }
    }

    public static List<Luz> getLuces() {
        return luces;
    }

    public static void limpiar() {
        luces.clear();
    }
}