package Juego.facuAlex.Herramientas;

import com.badlogic.gdx.Gdx;

import Juego.facuAlex.recursos.Item;

/*
 * Armadura equipable.
 *
 * Funciona como "vida extra": cuando el jugador recibe daño, la armadura lo
 * absorbe primero. Cuando se le acaba la protección, se rompe y desaparece.
 */
public class Armadura extends Item {

    public static final String NOMBRE_CUERO = "Armadura de cuero";
    public static final String NOMBRE_HIERRO = "Armadura de hierro";

    public static final int PROTECCION_CUERO = 30;
    public static final int PROTECCION_HIERRO = 60;

    private final int proteccionMaxima;
    private int proteccion;

    public Armadura(String nombre, int proteccionMaxima, String icono) {

        super(nombre);

        this.proteccionMaxima = proteccionMaxima;
        this.proteccion = proteccionMaxima;

        // Si todavía no existe el PNG, el item se crea igual (sin ícono)
        if (Gdx.files.internal(icono).exists()) {
            cargarIcono(icono);
        }
    }

    public static Armadura deCuero() {
        return new Armadura(
                NOMBRE_CUERO,
                PROTECCION_CUERO,
                "objetos/armaduraCuero.png"
        );
    }

    public static Armadura deHierro() {
        return new Armadura(
                NOMBRE_HIERRO,
                PROTECCION_HIERRO,
                "objetos/armaduraHierro.png"
        );
    }

    // Absorbe parte del daño. Devuelve el daño que NO pudo absorber.
    public int absorber(int danio) {

        int absorbido = Math.min(proteccion, danio);

        proteccion -= absorbido;

        return danio - absorbido;
    }

    public int getProteccion() {
        return proteccion;
    }

    public int getProteccionMaxima() {
        return proteccionMaxima;
    }

    public boolean estaRota() {
        return proteccion <= 0;
    }

    @Override
    public void mostrarInfo() {
        System.out.println(
                nombre + " | Proteccion: " + proteccion + "/" + proteccionMaxima
        );
    }
}