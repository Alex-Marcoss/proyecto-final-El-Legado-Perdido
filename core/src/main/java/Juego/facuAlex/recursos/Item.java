package Juego.facuAlex.recursos;

import com.badlogic.gdx.graphics.Texture;

public class Item {

    protected String nombre;
    private Texture icono;

    public Item(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void mostrarInfo() {
        System.out.println(nombre);
    }

    public void agregarCantidad(int cantidad) {
        // Los Items normales no manejan cantidad.
    }

    // ==============================
    // ICONO
    // ==============================

    public void cargarIcono(String ruta) {

        if (icono != null) {
            icono.dispose();
        }

        icono = new Texture(ruta);

        icono.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );
    }

    public Texture getIcono() {
        return icono;
    }

    public void liberarIcono() {
        if (icono != null) {
            icono.dispose();
            icono = null;
        }
    }
}