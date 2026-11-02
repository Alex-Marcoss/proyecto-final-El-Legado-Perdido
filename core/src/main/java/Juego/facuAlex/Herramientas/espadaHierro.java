package Juego.facuAlex.Herramientas;

import com.badlogic.gdx.Gdx;

public class espadaHierro extends Herramienta {

    public static final int DURABILIDAD = 60;
    public static final int DANIO = 30;

    public espadaHierro() {

        super(
            "Espada de hierro",
            DURABILIDAD,
            DANIO,
            tipoHerramienta.ESPADA
        );

        // Mientras no exista el ícono propio, usa el del hacha
        cargarIcono(
            Gdx.files.internal("objetos/espada.png").exists()
                ? "objetos/espada.png"
                : "objetos/hacha_hierro.png"
        );
    }

    @Override
    public int getNivel() {
        return 2;
    }
}