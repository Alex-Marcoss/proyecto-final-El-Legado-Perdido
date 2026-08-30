package Juego.facuAlex.recursos;

public class Gema extends Item {

    public Gema() {
        super("Gema");
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Objeto especial: " + nombre);
    }
}