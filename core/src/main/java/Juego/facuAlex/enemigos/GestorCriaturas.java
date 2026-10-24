package Juego.facuAlex.enemigos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import Juego.facuAlex.Mapa.Mapa;
import Juego.facuAlex.jugador.Jugador;

public class GestorCriaturas {

	private static final int VACAS_DIA = 8;
	private static final int VACAS_NOCHE = 2;
	private static final int LOBOS_NOCHE = 8;
	private static final int ARANAS_NOCHE = 8;

	// Los enemigos empiezan a aparecer cuando ya oscureció un poco
	private static final float UMBRAL_ENEMIGOS = 0.25f;

	// Anillo alrededor del jugador donde nacen (fuera de lo que se ve)
	private static final float ENEMIGO_DIST_MIN = 500f;
	private static final float ENEMIGO_DIST_MAX = 900f;
	private static final float VACA_DIST_MIN = 750f;
	private static final float VACA_DIST_MAX = 1100f;

	// Solo se retiran criaturas que están más lejos que esto
	private static final float DIST_FUERA_DE_VISTA = 750f;

	private static final float INTERVALO_AJUSTE = 1f;

	private float temporizador = 0f;

	public GestorCriaturas(Mapa mapa, Jugador jugador) {

	    for (int i = 0; i < VACAS_DIA; i++) {

	        colocar(new Vaca(), mapa, jugador, 0f);
	    }
	}

    private final List<Criatura> criaturas = new ArrayList<Criatura>();

    private final Random azar = new Random();


    private void colocar(Criatura criatura, Mapa mapa, Jugador jugador,
                         float distanciaMinima) {

        float x = 0f;
        float y = 0f;

        for (int intento = 0; intento < 50; intento++) {

            float px = 100 + azar.nextFloat() * (mapa.getAncho() - 200);
            float py = 100 + azar.nextFloat() * (mapa.getAlto() - 200);

            // Busca tierra libre cerca (mismo metodo que usas para el jugador)
            float[] libre = mapa.buscarPosicionLibre(px, py);

            // El hitbox de pies coincide: pies centrados = esquina + 32
            x = libre[0] + Criatura.MITAD_JUGADOR;
            y = libre[1];

            float dx = x - (jugador.getPosicionX() + Criatura.MITAD_JUGADOR);
            float dy = y - jugador.getPosicionY();

            if (Math.sqrt(dx * dx + dy * dy) >= distanciaMinima) {

                break;
            }
        }

        criatura.setPosicion(x, y);

        criaturas.add(criatura);
    }

    public void actualizar(Jugador jugador, Mapa mapa, float delta) {

        for (Criatura c : criaturas) {

            c.actualizar(jugador, mapa, delta);
        }

        criaturas.removeIf(Criatura::haDesaparecido);
    }

    // Misma logica que tus arboles: las de mas arriba se dibujan detras
    public void dibujarDetras(SpriteBatch batch, float yPies) {

        for (Criatura c : criaturas) {

            if (c.getPosicionY() > yPies) {

                c.dibujar(batch);
            }
        }
    }

    public void dibujarDelante(SpriteBatch batch, float yPies) {

        for (Criatura c : criaturas) {

            if (c.getPosicionY() <= yPies) {

                c.dibujar(batch);
            }
        }
    }
    
    /*
     * Se llama una vez por frame. Cada INTERVALO_AJUSTE segundos acerca la
     * cantidad de criaturas a la que corresponde según la oscuridad.
     */
    public void actualizarCiclo(float oscuridad, Jugador jugador,
                                Mapa mapa, float delta) {

        temporizador += delta;

        if (temporizador < INTERVALO_AJUSTE) {
            return;
        }

        temporizador = 0f;

        boolean hayNoche = oscuridad >= UMBRAL_ENEMIGOS;

        int vacas = Math.round(
                MathUtils.lerp(VACAS_DIA, VACAS_NOCHE, oscuridad));
        int lobos = hayNoche ? Math.round(LOBOS_NOCHE * oscuridad) : 0;
        int aranas = hayNoche ? Math.round(ARANAS_NOCHE * oscuridad) : 0;

        // Como mucho nace una criatura por vez, para que sea gradual
        boolean yaNacio = false;

        yaNacio |= ajustar(Vaca.class, Vaca::new, vacas, oscuridad,
                VACA_DIST_MIN, VACA_DIST_MAX, !yaNacio, jugador, mapa);

        yaNacio |= ajustar(Lobo.class, Lobo::new, lobos, oscuridad,
                ENEMIGO_DIST_MIN, ENEMIGO_DIST_MAX, !yaNacio, jugador, mapa);

        ajustar(Arana.class, Arana::new, aranas, oscuridad,
                ENEMIGO_DIST_MIN, ENEMIGO_DIST_MAX, !yaNacio, jugador, mapa);
    }

    private boolean ajustar(Class<? extends Criatura> tipo,
                            Supplier<Criatura> fabrica,
                            int objetivo, float oscuridad,
                            float distMin, float distMax,
                            boolean puedeNacer,
                            Jugador jugador, Mapa mapa) {

        int actuales = contarVivas(tipo);

        if (actuales < objetivo) {

            return puedeNacer
                    && colocarEnAnillo(fabrica.get(), mapa, jugador,
                                       distMin, distMax);
        }

        if (actuales > objetivo) {

            // De día cerrado se retira lo que quede aunque se vea
            retirarMasLejana(tipo, jugador, oscuridad <= 0.01f);
        }

        return false;
    }

    private int contarVivas(Class<? extends Criatura> tipo) {

        int cantidad = 0;

        for (Criatura c : criaturas) {

            if (tipo.isInstance(c) && c.estaVivo()) {
                cantidad++;
            }
        }

        return cantidad;
    }

    // Quita la criatura de ese tipo más lejana al jugador, si no se ve
    private void retirarMasLejana(Class<? extends Criatura> tipo,
                                  Jugador jugador, boolean forzar) {

        Criatura lejana = null;
        float mayor = 0f;

        for (Criatura c : criaturas) {

            if (!tipo.isInstance(c) || !c.estaVivo()) {
                continue;
            }

            float d = distanciaAlJugador(c, jugador);

            if (d > mayor) {
                mayor = d;
                lejana = c;
            }
        }

        if (lejana != null && (forzar || mayor > DIST_FUERA_DE_VISTA)) {
            criaturas.remove(lejana);
        }
    }

    private boolean colocarEnAnillo(Criatura criatura, Mapa mapa,
                                    Jugador jugador,
                                    float distMin, float distMax) {

        float jx = jugador.getPosicionX() + Criatura.MITAD_JUGADOR;
        float jy = jugador.getPosicionY();

        for (int intento = 0; intento < 30; intento++) {

            float angulo = azar.nextFloat() * MathUtils.PI2;
            float dist = distMin + azar.nextFloat() * (distMax - distMin);

            float px = MathUtils.clamp(jx + MathUtils.cos(angulo) * dist,
                                       100f, mapa.getAncho() - 100f);
            float py = MathUtils.clamp(jy + MathUtils.sin(angulo) * dist,
                                       100f, mapa.getAlto() - 100f);

            float[] libre = mapa.buscarPosicionLibre(px, py);

            float x = libre[0] + Criatura.MITAD_JUGADOR;
            float y = libre[1];

            float dx = x - jx;
            float dy = y - jy;

            if (Math.sqrt(dx * dx + dy * dy) >= distMin) {

                criatura.setPosicion(x, y);
                criaturas.add(criatura);
                return true;
            }
        }

        return false; // no encontró lugar; reintenta en el próximo ciclo
    }

    private float distanciaAlJugador(Criatura c, Jugador jugador) {

        float dx = c.getPosicionX()
                - (jugador.getPosicionX() + Criatura.MITAD_JUGADOR);
        float dy = c.getPosicionY() - jugador.getPosicionY();

        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /*
     * Criatura viva mas cercana en la direccion en la que mira el jugador.
     * (pieX, pieY)  = pies del jugador
     * (dirX, dirY)  = hacia donde mira, por ejemplo (1, 0) = derecha
     * alcance       = que tan lejos llega el golpe
     * ancho         = cuanto se tolera hacia los costados
     */
    public Criatura getCriaturaAlFrente(float pieX, float pieY,
                                        float dirX, float dirY,
                                        float alcance, float ancho) {

        Criatura elegida = null;

        float menor = alcance;

        for (Criatura c : criaturas) {

            if (!c.estaVivo()) {

                continue;
            }

            float rx = c.getPosicionX() - pieX;
            float ry = c.getPosicionY() - pieY;

            // distancia hacia adelante y desvio hacia los costados
            float adelante = rx * dirX + ry * dirY;
            float costado = Math.abs(rx * dirY - ry * dirX);

            if (adelante >= -10f && adelante <= menor && costado <= ancho) {

                menor = adelante;

                elegida = c;
            }
        }

        return elegida;
    }

    public void dispose() {

        CriaturaAnimacion.liberarTexturas();

        criaturas.clear();
    }
}