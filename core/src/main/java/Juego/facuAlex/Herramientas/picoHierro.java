package Juego.facuAlex.Herramientas;

import com.badlogic.gdx.Gdx;

public class picoHierro extends Herramienta {

    public static final int DURABILIDAD = 60;   // el pico común tiene 20
    public static final int DANIO = 22;         // el pico común tiene 15

    public picoHierro() {

        super(
            "Pico de hierro",
            DURABILIDAD,
            DANIO,
            tipoHerramienta.PICO
        );

        // Mientras no exista el ícono propio, usa el del pico común
        cargarIcono(
            Gdx.files.internal("objetos/pico_hierro.png").exists()
                ? "objetos/pico_hierro.png"
                : "objetos/pico.png"
        );
    }

    // Nivel 2: puede minar la mena de oro
    @Override
    public int getNivel() {
        return 2;
    }
}