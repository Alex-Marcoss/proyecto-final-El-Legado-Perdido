package Juego.facuAlex.enemigos.guardian;

import Juego.facuAlex.enemigos.Enemigo;
import Juego.facuAlex.jugador.Jugador;

public class Guardian extends Enemigo {

	
private float velocidad;

private float distanciaDeteccion;

private float distanciaAtaque;

private float tiempoEntreAtaques;

private float tiempoAtaque;

// =====================================================
// DIRECCIÓN
// =====================================================

public enum Direccion {

    ARRIBA,
    ABAJO,
    IZQUIERDA,
    DERECHA
}

private Direccion direccion;

// =====================================================
// ESTADOS DEL ATAQUE
// =====================================================

public enum EstadoAtaque {

    NORMAL,
    PREPARANDO,
    ATACANDO
}

private EstadoAtaque estadoAtaque;

// =====================================================
// TIEMPOS
// =====================================================

private static final float TIEMPO_PREPARACION = 0.8f;

private static final float TIEMPO_IMPACTO = 0.2f;

private float tiempoEstado;

// =====================================================
// CONSTRUCTOR
// =====================================================

public Guardian() {

    super(
        "Guardian del Templo",
        250,
        35
    );

    velocidad = 80f;

    distanciaDeteccion = 400f;

    distanciaAtaque = 90f;

    tiempoEntreAtaques = 1.5f;

    tiempoAtaque = 0f;

    estadoAtaque = EstadoAtaque.NORMAL;

    tiempoEstado = 0f;

    direccion = Direccion.ABAJO;
}

// =====================================================
// ACTUALIZAR
// =====================================================

public void actualizar(
        Jugador jugador,
        float delta) {

    if (jugador == null) {
        return;
    }

    if (!estaVivo()) {
        return;
    }

    // =================================================
    // COOLDOWN
    // =================================================

    if (tiempoAtaque > 0) {

        tiempoAtaque -= delta;
    }

    // =================================================
    // PREPARANDO ATAQUE
    // =================================================

    if (estadoAtaque == EstadoAtaque.PREPARANDO) {

        tiempoEstado -= delta;

        // Durante la preparación no se mueve

        if (tiempoEstado <= 0) {

            estadoAtaque = EstadoAtaque.ATACANDO;

            tiempoEstado = TIEMPO_IMPACTO;

            ejecutarAtaque(jugador);
        }

        return;
    }

    // =================================================
    // ATACANDO
    // =================================================

    if (estadoAtaque == EstadoAtaque.ATACANDO) {

        tiempoEstado -= delta;

        if (tiempoEstado <= 0) {

            estadoAtaque = EstadoAtaque.NORMAL;
        }

        return;
    }

    // =================================================
    // DISTANCIA AL JUGADOR
    // =================================================

    float diferenciaX =
            jugador.getPosicionX()
            - getPosicionX();

    float diferenciaY =
            jugador.getPosicionY()
            - getPosicionY();

    float distancia =
            (float) Math.sqrt(
                diferenciaX * diferenciaX +
                diferenciaY * diferenciaY
            );

    // =================================================
    // FUERA DEL RANGO DE DETECCIÓN
    // =================================================

    if (distancia > distanciaDeteccion) {

        return;
    }

    // =================================================
    // RANGO DE ATAQUE
    // =================================================

    if (distancia <= distanciaAtaque) {

        if (tiempoAtaque <= 0) {

            comenzarAtaque(
                diferenciaX,
                diferenciaY
            );
        }

        return;
    }

    // =================================================
    // PERSEGUIR AL JUGADOR
    // =================================================

    if (distancia > 0) {

        float direccionX =
                diferenciaX / distancia;

        float direccionY =
                diferenciaY / distancia;

        // Actualizamos hacia dónde está mirando

        actualizarDireccion(
            diferenciaX,
            diferenciaY
        );

        float movimiento =
                velocidad * delta;

        setPosicion(
            getPosicionX()
                + direccionX * movimiento,

            getPosicionY()
                + direccionY * movimiento
        );
    }
}

// =====================================================
// ACTUALIZAR DIRECCIÓN
// =====================================================

private void actualizarDireccion(
        float diferenciaX,
        float diferenciaY) {

    /*
     * Elegimos el eje en el que el jugador
     * está más lejos.
     *
     * Esto evita que el Guardian cambie
     * constantemente entre direcciones.
     */

    if (Math.abs(diferenciaX) >
            Math.abs(diferenciaY)) {

        if (diferenciaX > 0) {

            direccion = Direccion.DERECHA;

        } else {

            direccion = Direccion.IZQUIERDA;
        }

    } else {

        if (diferenciaY > 0) {

            direccion = Direccion.ARRIBA;

        } else {

            direccion = Direccion.ABAJO;
        }
    }
}

// =====================================================
// COMENZAR ATAQUE
// =====================================================

private void comenzarAtaque(
        float diferenciaX,
        float diferenciaY) {

    // Primero fija la dirección del ataque

    actualizarDireccion(
        diferenciaX,
        diferenciaY
    );

    estadoAtaque =
            EstadoAtaque.PREPARANDO;

    tiempoEstado =
            TIEMPO_PREPARACION;

    System.out.println(
        "¡El Guardian esta preparando un ataque!"
    );
}

// =====================================================
// EJECUTAR ATAQUE
// =====================================================

private void ejecutarAtaque(
        Jugador jugador) {

    if (jugador == null) {
        return;
    }

    if (!jugador.estaVivo()) {
        return;
    }

    float diferenciaX =
            jugador.getPosicionX()
            - getPosicionX();

    float diferenciaY =
            jugador.getPosicionY()
            - getPosicionY();

    float distancia =
            (float) Math.sqrt(
                diferenciaX * diferenciaX +
                diferenciaY * diferenciaY
            );

    // =================================================
    // EL JUGADOR ESCAPÓ
    // =================================================

    if (distancia > distanciaAtaque) {

        System.out.println(
            "El ataque del Guardian fallo."
        );

        tiempoAtaque =
                tiempoEntreAtaques;

        return;
    }

    // =================================================
    // ATAQUE ACERTADO
    // =================================================

    atacar(jugador);

    tiempoAtaque =
            tiempoEntreAtaques;
}

// =====================================================
// GETTERS
// =====================================================

public float getVelocidad() {

    return velocidad;
}

public float getDistanciaDeteccion() {

    return distanciaDeteccion;
}

public float getDistanciaAtaque() {

    return distanciaAtaque;
}

public EstadoAtaque getEstadoAtaque() {

    return estadoAtaque;
}

public Direccion getDireccion() {

    return direccion;
}

public boolean estaPreparandoAtaque() {

    return estadoAtaque ==
            EstadoAtaque.PREPARANDO;
}

public float getTiempoPreparacion() {

    return tiempoEstado;
}


}
