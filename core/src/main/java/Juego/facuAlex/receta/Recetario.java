package Juego.facuAlex.receta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Juego.facuAlex.Herramientas.*;
import Juego.facuAlex.inventario.inventario;
import Juego.facuAlex.recursos.Catalogo;

public class Recetario {

    // Una receta + lo que hace falta para que aparezca en la mesa
    private static class Entrada {

        final Receta receta;
        final String requiereFabricado;  // resultado que hay que haber fabricado
        final String requiereTener;      // item que hay que haber tenido
        boolean desbloqueada;

        Entrada(Receta receta, String requiereFabricado, String requiereTener) {

            this.receta = receta;
            this.requiereFabricado = requiereFabricado;
            this.requiereTener = requiereTener;

            // Todas las recetas están disponibles desde el principio
            this.desbloqueada = true;
        }
    }

    private final List<Entrada> todas = new ArrayList<Entrada>();
    private final List<Receta> visibles = new ArrayList<Receta>();
    private final Set<String> fabricados = new HashSet<String>();

    public Recetario() {

        // ==========================================
        // NIVEL 1: herramientas básicas (siempre disponibles)
        // ==========================================

    	Receta recetaHacha = new Receta(
    	        () -> new hacha(20, 10),
    	        new Ingrediente[] {
    	            new Ingrediente(Catalogo.MADERA, 3),
    	            new Ingrediente(Catalogo.PIEDRA, 2),
    	            new Ingrediente(Catalogo.FIBRA, 1)
    	        }
    	).sinMesa();

    	Receta recetaPico = new Receta(
    	        () -> new pico(20, 15),
    	        new Ingrediente[] {
    	            new Ingrediente(Catalogo.MADERA, 3),
    	            new Ingrediente(Catalogo.PIEDRA, 4),
    	            new Ingrediente(Catalogo.FIBRA, 1)
    	        }
    	).sinMesa();

        agregar(recetaHacha, null, null);
        agregar(recetaPico, null, null);

        String hachaFabricada = recetaHacha.getResultado().getNombre();

        // ==========================================
        // NIVEL 2: materiales (se desbloquean al fabricar el hacha)
        // ==========================================

        agregar(new Receta(
                () -> Catalogo.crear(Catalogo.PALOS, 2),
                new Ingrediente[] {
                    new Ingrediente(Catalogo.MADERA, 1)
                }),
                hachaFabricada, null);

        agregar(new Receta(
                () -> Catalogo.crear(Catalogo.CUERDA, 1),
                new Ingrediente[] {
                    new Ingrediente(Catalogo.FIBRA, 5)
                }).sinMesa(),
                hachaFabricada, null);

        agregar(new Receta(
                () -> Catalogo.crear(Catalogo.PAPEL, 1),
                new Ingrediente[] {
                    new Ingrediente(Catalogo.FIBRA, 2)
                }).sinMesa(),
                hachaFabricada, null);

        // Lana: aparece cuando el jugador consigue hilo
        agregar(new Receta(
                () -> Catalogo.crear(Catalogo.LANA, 1),
                new Ingrediente[] {
                    new Ingrediente(Catalogo.HILO, 4)
                }),
                null, Catalogo.HILO);

        // Vendaje: aparece después de fabricar papel
        agregar(new Receta(
                () -> Catalogo.crear(Catalogo.VENDAJE, 1),
                new Ingrediente[] {
                    new Ingrediente(Catalogo.PAPEL, 3)
                }),
                Catalogo.PAPEL, null);

        // ==========================================
        // NIVEL 3: herramientas de hierro (aparecen al conseguir hierro)
        // ==========================================

        agregar(new Receta(
                () -> new picoHierro(),
                new Ingrediente[] {
                    new Ingrediente(Catalogo.PALOS, 2),
                    new Ingrediente(Catalogo.HIERRO, 3),
                    new Ingrediente(Catalogo.CUERDA, 1)
                }),
                null, Catalogo.HIERRO);

        agregar(new Receta(
                () -> new hachaHierro(),
                new Ingrediente[] {
                    new Ingrediente(Catalogo.PALOS, 2),
                    new Ingrediente(Catalogo.HIERRO, 3),
                    new Ingrediente(Catalogo.CUERDA, 1)
                }),
                null, Catalogo.HIERRO);

        agregar(new Receta(
                () -> new espadaHierro(),
                new Ingrediente[] {
                    new Ingrediente(Catalogo.PALOS, 1),
                    new Ingrediente(Catalogo.HIERRO, 2),
                    new Ingrediente(Catalogo.CUERDA, 1)
                }),
                null, Catalogo.HIERRO);

        agregar(new Receta(
                () -> Armadura.deCuero(),
                new Ingrediente[] {
                    new Ingrediente(Catalogo.CUERO, 8)
                }),
                null, Catalogo.CUERO);

        agregar(new Receta(
                () -> Armadura.deHierro(),
                new Ingrediente[] {
                    new Ingrediente(Catalogo.HIERRO, 8)
                }),
                null, Catalogo.HIERRO);

        reconstruirVisibles();
    }

    // =====================================================
    // ALTA
    // =====================================================

    private void agregar(Receta receta, String requiereFabricado,
                         String requiereTener) {

        todas.add(new Entrada(receta, requiereFabricado, requiereTener));
    }

 // true = recetario común (solo recetas sin mesa); false = todas (mesa)
    private boolean soloSinMesa;

    public void setSoloSinMesa(boolean soloSinMesa) {
        this.soloSinMesa = soloSinMesa;
        reconstruirVisibles();
    }

    private void reconstruirVisibles() {

        visibles.clear();

        for (Entrada e : todas) {
            if (e.desbloqueada
                    && (!soloSinMesa || !e.receta.requiereMesa())) {
                visibles.add(e.receta);
            }
        }
    }
    // =====================================================
    // DESBLOQUEOS
    // =====================================================

    // Anota que el jugador fabricó esto (puede desbloquear otras recetas)
    public void registrarFabricado(Receta receta) {

        fabricados.add(receta.getResultado().getNombre());
    }

    // Devuelve los nombres de las recetas que se acaban de desbloquear
    public List<String> actualizarDesbloqueos(inventario inv) {

        List<String> nuevas = new ArrayList<String>();

        for (Entrada e : todas) {

            if (e.desbloqueada) {
                continue;
            }

            boolean fabricadoOk = e.requiereFabricado == null
                    || fabricados.contains(e.requiereFabricado);

            boolean tenerOk = e.requiereTener == null
                    || inv.tieneItem(e.requiereTener);

            if (fabricadoOk && tenerOk) {
                e.desbloqueada = true;
                nuevas.add(e.receta.getResultado().getNombre());
            }
        }

        if (!nuevas.isEmpty()) {
            reconstruirVisibles();
        }

        return nuevas;
    }

    // =====================================================
    // CONSULTAS (solo recetas desbloqueadas)
    // =====================================================

    public List<Receta> getRecetas() {
        return visibles;
    }

    public Receta getReceta(int indice) {

        if (indice < 0 || indice >= visibles.size()) {
            return null;
        }

        return visibles.get(indice);
    }

    public int getCantidadRecetas() {
        return visibles.size();
    }

    public int indiceDe(Receta receta) {
        return visibles.indexOf(receta);
    }
}