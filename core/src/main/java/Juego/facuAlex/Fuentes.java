package Juego.facuAlex;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Fuentes {

    private static final String RUTA_TITULO =
            "fuentes/CinzelDecorative-Bold.ttf";

    private static final String RUTA_TEXTO =
            "fuentes/Cinzel.ttf";

    private static final String CARACTERES =
            FreeTypeFontGenerator.DEFAULT_CHARS
            + "\u00e1\u00e9\u00ed\u00f3\u00fa"
            + "\u00c1\u00c9\u00cd\u00d3\u00da"
            + "\u00f1\u00d1"
            + "\u00fc\u00dc"
            + "\u00bf\u00a1";

    // Fuente decorativa para títulos
    public static BitmapFont titulo(int tamano) {
        return crear(RUTA_TITULO, tamano);
    }

    // Fuente normal para textos
    public static BitmapFont texto(int tamano) {
        return crear(RUTA_TEXTO, tamano);
    }

    // Crea una fuente y la reduce si el texto supera el ancho indicado
    public static BitmapFont tituloAjustado(
            String texto,
            float anchoMaximo,
            int tamanoInicial) {

        GlyphLayout medida = new GlyphLayout();

        int tamano = tamanoInicial;

        BitmapFont fuente = titulo(tamano);

        medida.setText(fuente, texto);

        while (medida.width > anchoMaximo && tamano > 16) {

            fuente.dispose();

            tamano -= 4;

            fuente = titulo(tamano);

            medida.setText(fuente, texto);
        }

        medida = null;

        return fuente;
    }

    private static BitmapFont crear(String ruta, int tamano) {

        FreeTypeFontGenerator generador =
                new FreeTypeFontGenerator(Gdx.files.internal(ruta));

        FreeTypeFontParameter parametros =
                new FreeTypeFontParameter();

        parametros.size = tamano;
        parametros.characters = CARACTERES;

        parametros.minFilter = Texture.TextureFilter.Linear;
        parametros.magFilter = Texture.TextureFilter.Linear;

        BitmapFont fuente = generador.generateFont(parametros);

        generador.dispose();

        return fuente;
    }
}