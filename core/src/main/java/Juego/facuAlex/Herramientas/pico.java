package Juego.facuAlex.Herramientas;


public class pico extends Herramienta {

    public pico(int durabilidad, int daño) {

        super(
            "Pico",
            durabilidad,
            daño,
            tipoHerramienta.PICO
        );

        cargarIcono("objetos/pico.png");
    }
}