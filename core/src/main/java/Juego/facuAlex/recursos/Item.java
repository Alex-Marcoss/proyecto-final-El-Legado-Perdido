package Juego.facuAlex.recursos;

import com.badlogic.gdx.graphics.Texture;

public class Item {

    protected String nombre;
    private Texture icono;
    private String rutaIcono;
    

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
        rutaIcono = ruta;

        icono.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );
    }

    // Carga el mismo icono que tiene otro item (cada item tiene su
    // propia textura, asi liberar uno no afecta al otro)
    public void copiarIconoDe(Item otro) {

        if (otro != null && otro.rutaIcono != null) {
            cargarIcono(otro.rutaIcono);
        }
    }

    public Texture getIcono() {
        return icono;
    }

    public void liberarIcono() {
        if (icono != null) {
            icono.dispose();
            icono = null;
            rutaIcono = null;
        }
    }
    	
    
}
