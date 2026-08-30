package Juego.facuAlex.sistemas;

import Juego.facuAlex.inventario;
import Juego.facuAlex.enemigos.Guardian;

public class ObjetivoJuego {

    private EstadoObjetivo estado;

    public ObjetivoJuego() {
        estado = EstadoObjetivo.EXPLORANDO;
    }

    public EstadoObjetivo getEstado() {
        return estado;
    }

    public void encontrarTemplo() {

        if (estado == EstadoObjetivo.EXPLORANDO) {

            estado = EstadoObjetivo.TEMPLO_ENCONTRADO;

            System.out.println("Encontraste el templo.");
        }
    }

    public void derrotarBoss() {

        if (estado == EstadoObjetivo.TEMPLO_ENCONTRADO) {

            estado = EstadoObjetivo.BOSS_DERROTADO;

            System.out.println("Derrotaste al Boss.");
        }
    }

    public void obtenerGema() {

        if (estado == EstadoObjetivo.BOSS_DERROTADO) {

            estado = EstadoObjetivo.GEMA_OBTENIDA;

            System.out.println("Conseguiste la gema.");
        }
    }

    public void llegarAlEscape() {

        if (estado == EstadoObjetivo.GEMA_OBTENIDA) {

            estado = EstadoObjetivo.VICTORIA;

            System.out.println("¡Escapaste de la isla!");
            System.out.println("¡VICTORIA!");
        }
    }

    public boolean juegoTerminado() {

        return estado == EstadoObjetivo.VICTORIA;
    }

    public void mostrarObjetivo() {

        System.out.println(
            "Objetivo actual: " + estado
        );
    }
    
    
    public void comprobarGuardian(Guardian guardian) {

        if (estado == EstadoObjetivo.TEMPLO_ENCONTRADO && !guardian.estaVivo()) {

            estado = EstadoObjetivo.BOSS_DERROTADO;

            System.out.println("El Guardian del Templo fue derrotado.");
        }
    }
    
    public void comprobarGema(inventario inventario) {

        if (estado != EstadoObjetivo.BOSS_DERROTADO) {
            return;
        }

        if (inventario.tieneItem("Gema")) {

            estado = EstadoObjetivo.GEMA_OBTENIDA;

            System.out.println("La gema fue obtenida.");
               
        }
    }
    
    public void comprobarEscape(EstructuraRescate estructura) {

        if (estado == EstadoObjetivo.GEMA_OBTENIDA
                && estructura.estaActiva()) {

            estado = EstadoObjetivo.VICTORIA;

            System.out.println(
                "¡La estructura de rescate fue activada!"
            );

            System.out.println(
                "¡Los rescatistas vienen por vos!"
            );

            System.out.println(
                "¡VICTORIA!"
            );
        }
    }
    
    
}