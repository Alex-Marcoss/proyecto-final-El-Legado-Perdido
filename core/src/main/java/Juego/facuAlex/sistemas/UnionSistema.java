package Juego.facuAlex.sistemas;

import Juego.facuAlex.Jugador;
import Juego.facuAlex.Mapa.Mapa;

public class UnionSistema {

    private Jugador jugador;

    private CicloDia cicloDia;
    private Supervivencia supervivencia;
    private ObjetivoJuego objetivo;

    public UnionSistema(Jugador jugador) {

        this.jugador = jugador;

        this.supervivencia = new Supervivencia(10, 10);

        this.cicloDia = new CicloDia(supervivencia);

        this.objetivo = new ObjetivoJuego();
    }

    public void pasarHora() {

        cicloDia.pasarHora(jugador);

    }

    public void actualizar(float segundos) {

        jugador.actualizarEnergia(segundos);

    }

    public void actualizarObjetivo(Mapa mapa, EstructuraRescate estructura) {

        objetivo.comprobarTemplo(jugador, mapa);

        if (objetivo.getGuardian() != null) {
            objetivo.comprobarGuardian(objetivo.getGuardian());
        }

        objetivo.comprobarGema(jugador.getInventario());

        if (estructura != null) {
            objetivo.comprobarEscape(estructura);
        }
    }

    public void mostrarEstado() {

        cicloDia.mostrarEstado();

        System.out.println();

        jugador.mostrarEstado();

    }

    public ObjetivoJuego getObjetivo() {

        return objetivo;

    }
}