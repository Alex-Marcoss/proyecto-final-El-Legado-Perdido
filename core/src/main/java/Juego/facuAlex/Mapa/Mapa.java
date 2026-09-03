package Juego.facuAlex.Mapa;

public class Mapa {

    private float ancho;

    private float alto;

    private Zona[] zonas;

    private LugarEspecial templo;

    public Mapa(float ancho, float alto) {

        this.ancho = ancho;

        this.alto = alto;

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

        templo = new LugarEspecial(
            "Templo",
            ancho * 0.75f,
            alto * 0.65f,
            100,
            100
        );
    }

    public boolean estaDentro(float x, float y) {

        return x >= 0 &&
               x <= ancho &&
               y >= 0 &&
               y <= alto;
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
}