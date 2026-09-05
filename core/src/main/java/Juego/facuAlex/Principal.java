package Juego.facuAlex;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import Juego.facuAlex.Mapa.Mapa;

public class Principal extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture jugadorSprite;

    private Jugador jugador;
    private Mapa mapa;

    @Override
    public void create() {

        batch = new SpriteBatch();

        jugadorSprite = new Texture("jugador.png");

        jugador = new Jugador("Facu");

        mapa = new Mapa(900, 600);

        // Posición inicial del jugador
        jugador.mover(400, 250, mapa);
    }

    @Override
    public void render() {

        actualizarMovimiento();

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.begin();

        batch.draw(
            jugadorSprite,
            jugador.getPosicionX(),
            jugador.getPosicionY()
        );

        batch.end();
    }

    private void actualizarMovimiento() {

        float velocidad = 200f * Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            jugador.mover(0, velocidad, mapa);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            jugador.mover(0, -velocidad, mapa);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            jugador.mover(-velocidad, 0, mapa);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            jugador.mover(velocidad, 0, mapa);
        }
    }

    @Override
    public void dispose() {

        batch.dispose();
        jugadorSprite.dispose();
    }
}