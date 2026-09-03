
package Juego.facuAlex;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import Juego.facuAlex.Mapa.Mapa;
import Juego.facuAlex.Mapa.Zona;
import Juego.facuAlex.enemigos.Animal;
import Juego.facuAlex.enemigos.Vaca;
import Juego.facuAlex.sistemas.ObjetivoJuego;
import Juego.facuAlex.sistemas.combate;
import Juego.facuAlex.enemigos.Gallina;
import Juego.facuAlex.enemigos.Guardian;

public class Principal extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture image;

    @Override
    public void create() {

        System.out.println("El Legado Perdido iniciado correctamente");

        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
    }

    @Override
    public void render() {

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }

    @Override
    public void dispose() {

        batch.dispose();
        image.dispose();
    }

    public static void main(String[] args) {

  
    	Mapa mapa = new Mapa(900, 600);

    	Jugador jugador = new Jugador("Facu");

    	System.out.println("Energia inicial: " + jugador.getEnergia());

    	jugador.correr(10, 0, mapa);

    	System.out.println("Energia despues de correr: " + jugador.getEnergia());

    	jugador.actualizarEnergia(1);

    	System.out.println("Despues de 1 segundo: " + jugador.getEnergia());

    	jugador.actualizarEnergia(1);

    	System.out.println("Despues de 2 segundos: " + jugador.getEnergia());

    	jugador.actualizarEnergia(1);

    	System.out.println("Despues de 3 segundos: " + jugador.getEnergia());
    	
    	
}
}

