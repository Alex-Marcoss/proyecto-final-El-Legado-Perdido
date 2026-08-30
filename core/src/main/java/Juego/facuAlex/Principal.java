package Juego.facuAlex;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import Juego.facuAlex.enemigos.Animal;
import Juego.facuAlex.enemigos.Araña;
import Juego.facuAlex.enemigos.Guardian;
import Juego.facuAlex.receta.Ingrediente;
import Juego.facuAlex.recursos.Comida;
import Juego.facuAlex.recursos.Gema;
import Juego.facuAlex.sistemas.CicloDia;
import Juego.facuAlex.sistemas.EstructuraRescate;
import Juego.facuAlex.sistemas.ObjetivoJuego;
import Juego.facuAlex.sistemas.Supervivencia;
import Juego.facuAlex.sistemas.UnionSistema;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
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

    	

    	 Jugador jugador = new Jugador("Facu");

         Comida carne = new Comida(
             "Carne",
             2,
             25
         );

         Animal jabali = new Animal(
             "Jabali",
             40,
             15,
             carne
         );

         System.out.println("===== ANIMAL =====");

         jabali.mostrarEstado();

         System.out.println();

         // Derrotar al animal
         jugador.atacar(jabali);
         jugador.atacar(jabali);

         System.out.println();

         // Primera recoleccion
         System.out.println("PRIMERA RECOLECCION:");

         jugador.ObtenerComida(jabali);

         jugador.mostrarInventario();

         System.out.println();

         // Segunda recoleccion
         System.out.println("SEGUNDA RECOLECCION:");

         jugador.ObtenerComida(jabali);

         jugador.mostrarInventario();
    	
        
    }
 }

    



