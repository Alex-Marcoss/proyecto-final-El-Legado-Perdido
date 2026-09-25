package Juego.facuAlex.Herramientas;

public class hacha extends Herramienta {

    public hacha(int durabilidad, int daño) {

        super(
            "Hacha",
            durabilidad,
            daño,
            tipoHerramienta.HACHA
        );

        cargarIcono("objetos/hacha.png");
    }
}