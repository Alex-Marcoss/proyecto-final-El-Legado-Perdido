package Juego.facuAlex.Mapa;

public class Zona {

    private String nombre;

    private float x;
    private float y;

    private float ancho;
    private float alto;

    public Zona(String nombre, float x, float y, float ancho, float alto) {

        this.nombre = nombre;
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }


	public boolean contiene(float posicionX, float posicionY) {

        return posicionX >= x &&
               posicionX <= x + ancho &&
               posicionY >= y &&
               posicionY <= y + alto;
    }

    public String getNombre() {
        return nombre;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getAncho() {
        return ancho;
    }

    public float getAlto() {
        return alto;
    }
}