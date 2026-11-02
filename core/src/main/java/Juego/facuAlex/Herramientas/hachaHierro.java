package Juego.facuAlex.Herramientas;

import com.badlogic.gdx.Gdx;

public class hachaHierro extends Herramienta {

    public static final int DURABILIDAD = 60;   // el hacha común tiene 20
    public static final int DANIO = 16;         // el hacha común tiene 10

    public hachaHierro() {

        super(
            "Hacha de hierro",
            DURABILIDAD,
            DANIO,
            tipoHerramienta.HACHA
        );

        // Mientras no exista el ícono propio, usa el del hacha común
        cargarIcono(
            Gdx.files.internal("objetos/hacha_hierro.png").exists()
                ? "objetos/hacha_hierro.png"
                : "objetos/hacha.png"
        );
    }

    @Override
    public int getNivel() {
        return 2;
    }
}