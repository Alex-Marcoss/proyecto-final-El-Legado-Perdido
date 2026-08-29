package Juego;

public class Construccion {

    private String nombre;
    private int vida;
    private Ingrediente[] materiales;

    public Construccion(String nombre, int vida, Ingrediente[] materiales) {
        this.nombre = nombre;
        this.vida = vida;
        this.materiales = materiales;
    }

    public String getNombre() {
        return nombre;
    }

    public int getVida() {
        return vida;
    }

    public Ingrediente[] getMateriales() {
        return materiales;
    }

    public void mostrarInfo() {

        System.out.println("Construccion: " + nombre);
        System.out.println("Vida: " + vida);

        System.out.println("Materiales necesarios:");

        for (int i = 0; i < materiales.length; i++) {

            System.out.println(
                "- " +
                materiales[i].getNombreRecurso() +
                ": " +
                materiales[i].getCantidad()
            );
        }
    }
    
    public boolean puedeConstruir(inventario inventario) {

        for (int i = 0; i < materiales.length; i++) {

            Ingrediente material = materiales[i];

            if (!inventario.tieneRecurso(
                    material.getNombreRecurso(),
                    material.getCantidad())) {

                return false;
            }
        }

        return true;
    }
    
    public void gastarMateriales(inventario inventario) {

        for (int i = 0; i < materiales.length; i++) {

            Ingrediente material = materiales[i];

            inventario.gastarRecurso(
                material.getNombreRecurso(),
                material.getCantidad()
            );
        }
    }
    
    
}