package Juego.facuAlex;

import Juego.facuAlex.Mapa.Mapa;
import Juego.facuAlex.enemigos.Guardian;
import Juego.facuAlex.receta.Ingrediente;
import Juego.facuAlex.recursos.Gema;
import Juego.facuAlex.recursos.Recursos;
import Juego.facuAlex.sistemas.EstructuraRescate;
import Juego.facuAlex.sistemas.UnionSistema;

public class Pruebas {

public static void main(String[] args) {

        // Crear mapa y jugador
        Mapa mapa = new Mapa(900, 600);
        Jugador jugador = new Jugador("Facu");

        // Crear sistema general
        UnionSistema sistemas = new UnionSistema(jugador);

        System.out.println("=== OBJETIVO INICIAL ===");
        sistemas.getObjetivo().mostrarObjetivo();

        // Mover al jugador hacia el templo
        System.out.println("\n=== MOVIENDO AL JUGADOR ===");

        jugador.mover(700, 400, mapa);

        System.out.println(
            "Posicion jugador: " +
            jugador.getPosicionX() + ", " +
            jugador.getPosicionY()
        );

        // Actualizar objetivo
        sistemas.actualizarObjetivo(mapa, null);

        System.out.println("\n=== OBJETIVO DESPUES DE ACTUALIZAR ===");
        sistemas.getObjetivo().mostrarObjetivo();
        
        System.out.println("\n=== COMBATE ===");

        Guardian guardian = sistemas.getObjetivo().getGuardian();

        while (guardian.estaVivo()) {
            jugador.atacar(guardian);
        }

        System.out.println("\n=== ACTUALIZANDO OBJETIVO ===");

        sistemas.actualizarObjetivo(mapa, null);

        sistemas.getObjetivo().mostrarObjetivo();
        
        System.out.println("\n=== OBTENIENDO GEMA ===");

        // Simulamos que el jugador recoge la gema
        Gema gema = new Gema();
        jugador.recogerItem(gema);

        // Actualizamos el objetivo
        sistemas.actualizarObjetivo(mapa, null);

        System.out.println("\n=== OBJETIVO DESPUES DE OBTENER LA GEMA ===");

        sistemas.getObjetivo().mostrarObjetivo();
        
        System.out.println("\n=== CONSTRUYENDO ESTRUCTURA DE RESCATE ===");

     // Materiales necesarios
     Ingrediente[] materiales = {
         new Ingrediente("Madera", 10),
         new Ingrediente("Piedra", 10)
     };

     // Crear estructura
     EstructuraRescate estructura = new EstructuraRescate(materiales);

     // Simulamos que el jugador consiguió los materiales
     jugador.getInventario().agregarRecurso(
         new Recursos("Madera", 10),
         10
     );

     jugador.getInventario().agregarRecurso(
         new Recursos("Piedra", 10),
         10
     );

     // Colocamos la gema
     estructura.colocarGema();

     // Activamos la estructura
     estructura.activar(jugador.getInventario());

     // Actualizamos el objetivo
     sistemas.actualizarObjetivo(mapa, estructura);

     System.out.println("\n=== ESTADO FINAL ===");
     sistemas.getObjetivo().mostrarObjetivo();
    }
}