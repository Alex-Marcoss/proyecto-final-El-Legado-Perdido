package Juego.facuAlex.jugador;

import com.badlogic.gdx.math.Rectangle;

import Juego.facuAlex.recursos.*;
import Juego.facuAlex.Herramientas.Herramienta;
import Juego.facuAlex.Herramientas.tipoHerramienta;
import Juego.facuAlex.Mapa.Mapa;
import Juego.facuAlex.enemigos.Animal;
import Juego.facuAlex.enemigos.Enemigo;
import Juego.facuAlex.receta.Receta;
import Juego.facuAlex.recursos.Comida;
import Juego.facuAlex.recursos.Item;
import Juego.facuAlex.recursos.Recursos;
import Juego.facuAlex.recursos.arbol;
import Juego.facuAlex.recursos.roca;
import Juego.facuAlex.sistemas.Construccion;
import Juego.facuAlex.sistemas.Energia;
import Juego.facuAlex.inventario.*;

public class Jugador {

	String nombre;
	int vida;
	int hambre;
	float energia;
	private Herramienta herramientaEquipada;
	private float posicionX;
	private float posicionY;
	private Energia sistemaEnergia;
	
	inventario inventario;
	
	// -------------------------------------
	

	public Jugador(String nombre) { // constructor jugador

	    this.nombre = nombre;
	    this.vida = 100;
	    this.hambre = 100;
	    this.energia = 100;
	    this.inventario = new inventario();
	    this.posicionX = 0;
	    this.posicionY = 0;
	    this.sistemaEnergia = new Energia(2.67f);
	}
	
	
	// ------------------------ Movimientos -------------------------------
	
	
	// solo los pies (no todo).
	// Asi puede pasar "por detras" de un arbol y la colision se siente natural.
	private static final float HITBOX_ANCHO = 24f;
	private static final float HITBOX_ALTO = 12f;
	private static final float HITBOX_OFFSET_X = 20f; // (64 - 24) / 2 -> centrado

	public Rectangle getHitbox() {
	    return crearHitbox(posicionX, posicionY);
	}

	private Rectangle crearHitbox(float x, float y) {
	    return new Rectangle(x + HITBOX_OFFSET_X, y, HITBOX_ANCHO, HITBOX_ALTO);
	}

	// Mueve en X y en Y por separado: si choca de costado, igual se desliza
	// por la pared en vez de quedarse trabado.
	// Devuelve true si se movio aunque sea un poco.
	private boolean moverConColision(float x, float y, Mapa mapa) {

	    boolean seMovio = false;

	    float nuevaX = posicionX + x;

	    if (mapa.puedeCaminar(crearHitbox(nuevaX, posicionY))) {
	        posicionX = nuevaX;
	        seMovio = true;
	    }

	    float nuevaY = posicionY + y;

	    if (mapa.puedeCaminar(crearHitbox(posicionX, nuevaY))) {
	        posicionY = nuevaY;
	        seMovio = true;
	    }

	    return seMovio;
	}

	// Coloca al jugador directamente (sin colisiones). Sirve para el spawn.
	public void setPosicion(float x, float y) {
	    posicionX = x;
	    posicionY = y;
	}

	public void mover(float x, float y, Mapa mapa) {

	    moverConColision(x, y, mapa);
	}

	public boolean correr(float x, float y, Mapa mapa, float delta) {

	    // Energia necesaria para correr durante este frame
	    float energiaNecesaria = 10f * delta;

	    // Si no hay energia suficiente, no puede correr
	    if (energia <= 0) {
	        return false;
	    }

	    // Si esta pegado a un obstaculo no se mueve ni gasta energia
	    if (!moverConColision(x, y, mapa)) {
	        return false;
	    }

	    // Gastar energia proporcionalmente al tiempo
	    gastarEnergia(energiaNecesaria);

	    return true;
	}

	public float getPosicionX() {
		return posicionX;
	}
	
	public float getPosicionY() {
		return posicionY;
	}
	
	
	// -------------------------------------
	// Recoleccion de items y mostrar de inventario
	
	public void recogerItem(Item item) {
		inventario.agregarItem(item);	
	}
	
	public void mostrarInventario() {
		inventario.mostrarInventario();	
	}
	
	public inventario getInventario() {
	    return inventario;
	}
	
	// --------------------------------------------
	// Estados de jugador y supervivencia
	
	public void mostrarEstado() {
	    System.out.println("Vida: " + vida);
	    System.out.println("Hambre: " + hambre);
	    System.out.println("Energia: " + energia);
	}
	
	// -------------------------------------------------
	
	public void comprobarSupervivencia() {

	    if (hambre == 0) {
	        recibirDanio(10);
	    }
	}
	
	//-------------------------------------------------
	
	public int getVida() {
	    return vida;
	}

	public int getHambre() {
	    return hambre;
	}

	public int getEnergia() {
	    return (int)energia;
	}
	
	// ---------------------------------------------------------------
	
	//  ----------------------------- vida -----------------------------
	
	public void recibirDanio(int cantidad) {
	    vida = vida - cantidad;

	    if (vida < 0) {
	        vida = 0;
	    }

	    if (vida == 0) {
	        System.out.println("El jugador ha muerto.");
	        System.out.println("GAME OVER");
	    }
	}
	
	// Se activa cuando un enemigo le pega al jugador, para que
	// JugadorControl muestre la animacion de recibir dano.
	// (el dano por hambre NO lo activa, asi no lo frena cada tick)
	private boolean golpeRecibido = false;

	public void marcarGolpeRecibido() {

	    if (vida > 0) {
	        golpeRecibido = true;
	    }
	}

	// Devuelve true una sola vez por cada golpe recibido
	public boolean consumirGolpeRecibido() {

	    boolean resultado = golpeRecibido;
	    golpeRecibido = false;
	    return resultado;
	}

	public void curar(int cantidad) {
		
		vida = vida + cantidad;
		
		if(vida > 100) {
			vida = 100;
		}
		
	}
	
	public boolean estaVivo() {
	    return vida > 0;
	}
	
	public boolean gameOver() {
	    return vida <= 0;
	}
	
	// ----------------------------- Energia -----------------------------
	
	public void gastarEnergia(float cantidad) {

	    energia -= cantidad;

	    if (energia < 0) {
	        energia = 0;
	    }

	    sistemaEnergia.registrarGasto();
	}
	
	public void recuperarEnergia(float cantidad) {

	    energia += cantidad;

	    if (energia > 100) {
	        energia = 100;
	    }
	}
	
	public void actualizarEnergia(float segundos) {
	    sistemaEnergia.actualizar(this, segundos);
	}

	public Energia getSistemaEnergia() {
	    return sistemaEnergia;
	}

	
	// ----------------------------- Hambre -----------------------------
	
	public void perderHambre(int cantidad) {
		
		hambre = hambre - cantidad;
		
		if (hambre < 0) {
			hambre = 0;
		}
		
	}
	
	public void comer(Comida comida) {

	    if (!inventario.tieneRecurso(
	            comida.getNombre(), 1)) {

	        System.out.println(
	            "No tenes " + comida.getNombre() + "."
	        );

	        return;
	    }

	    hambre += comida.getHambreRecuperada();

	    if (hambre > 100) {
	        hambre = 100;
	    }

	    inventario.gastarRecurso(
	        comida.getNombre(), 1
	    );

	    System.out.println(
	        "Comiste " + comida.getNombre() +
	        " y recuperaste " +
	        comida.getHambreRecuperada() +
	        " de hambre."
	    );
	}
	
	// -----------------------------------------------------------------------
	
	// Herramientas y equipacion
	
	public void equiparHerramienta(Herramienta herramienta) {

	    herramientaEquipada = herramienta;

	    if (herramienta != null) {

	        System.out.println(
	            "Equipaste: " +
	            herramienta.getNombre()
	        );

	    } else {

	        System.out.println(
	            "No tenes ninguna herramienta equipada."
	        );
	    }
	}
	
	public Herramienta getHerramientaEquipada() {

	    return herramientaEquipada;
	}
	
	// -------------------------------------------------------------------------
	// Interacciones
	
	public void interactuar(objetoMundo objeto) {

	    tipoHerramienta herramientaNecesaria = objeto.getHerramientaNecesaria();

	    // Comprobar herramienta
	    if (herramientaNecesaria != null) {

	        if (herramientaEquipada == null) {
	            System.out.println("No tenes una herramienta equipada.");
	            return;
	        }

	        if (herramientaEquipada.getTipo() != herramientaNecesaria) {
	            System.out.println("La herramienta no sirve para este objeto.");
	            return;
	        }
	    }

	    // Comprobar energía
	    if (energia < objeto.getEnergiaNecesaria()) {
	        System.out.println("No tenes suficiente energia.");
	        return;
	    }

	    // Obtener recurso
	    Recursos recurso = objeto.recolectarRecurso();

	    if (recurso == null) {
	        System.out.println("No queda ningun recurso.");
	        return;
	    }

	    // Gastar herramienta
	    if (herramientaEquipada != null) {
	        herramientaEquipada.usar();
	    }

	    // Gastar energía
	    gastarEnergia(objeto.getEnergiaNecesaria());

	    // Agregar al inventario
	    inventario.agregarRecurso(recurso, recurso.getCantidad());

	    System.out.println("Conseguiste "
	            + recurso.getCantidad()
	            + " de "
	            + recurso.getNombre()
	            + ".");
	}
	
	
	// --------------------------------------------------------
	// atacar
	
	public void atacar(Enemigo enemigo) {

    if (enemigo == null) {
        return;
    }

    if (!enemigo.estaVivo()) {
        return;
    }

    int daño = 20;

    enemigo.recibirDaño(daño);

    System.out.println(
        "Atacaste a " +
        enemigo.getNombre() +
        " y causaste " +
        daño +
        " de daño."
    );

    if (!enemigo.estaVivo()) {

        System.out.println(
            enemigo.getNombre() +
            " fue derrotado."
        );
    }
}
	
	// ----------------------------------------------------------
	
	// obtener comida
	
	public void recogerDropsAnimal(Animal animal) {

	    Recursos[] drops = animal.obtenerDrops();

	    if (drops == null) {

	        System.out.println(
	            "Este animal no tiene recursos para entregar."
	        );

	        return;
	    }

	    for (int i = 0; i < drops.length; i++) {

	        inventario.agregarRecurso(
	            drops[i],
	            drops[i].getCantidad()
	        );

	        System.out.println(
	            "Conseguiste "
	            + drops[i].getCantidad()
	            + " de "
	            + drops[i].getNombre()
	            + "."
	        );
	    	}
		}

	
	
	// ---------------------------------------------------------------------------------
	// Fabricacion
	
	public void fabricar(Receta receta) {

	    if (!receta.puedeCrear(inventario)) {
	        System.out.println("No tenes los recursos necesarios.");
	        return;
	    }

	    receta.gastarIngredientes(inventario);

	    inventario.agregarItem(receta.getResultado());

	    System.out.println(
	        "Fabricaste: " + receta.getResultado().getNombre()
	    );
	}
	
	// ---------------------------------------------------------------------------------
    //Construir

    public void construir(Construccion construccion) {

    if (!construccion.puedeConstruir(inventario)) {

        System.out.println(
            "No tenes los materiales necesarios."
        );

        return;
    }

    construccion.gastarMateriales(inventario);

    System.out.println(
        "Construiste: " +
        construccion.getNombre()
    );
}
// -------------------------------------------------

    // Comprueba SI se puede talar el arbol, sin gastar nada.
    // Se usa antes de empezar la animacion del hachazo.
    public boolean puedeTalar(arbol arbol) {

        if (arbol == null) {
            return false;
        }

        if (herramientaEquipada == null) {

            System.out.println(
                "Necesitas equipar un hacha para talar."
            );

            return false;
        }

        if (herramientaEquipada.getTipo() != tipoHerramienta.HACHA) {

            System.out.println(
                "Necesitas tener un hacha equipada."
            );

            return false;
        }

        if (energia < arbol.getEnergiaNecesaria()) {

            System.out.println(
                "No tienes suficiente energia."
            );

            return false;
        }

        if (herramientaEquipada.getDurabilidad() <= 0) {

            System.out.println(
                "El hacha no tiene suficiente durabilidad."
            );

            return false;
        }

        return true;
    }

    public void talarArbol(arbol arbol, Mapa mapa) {

        if (arbol == null || mapa == null) {
            return;
        }

        // Comprobar herramienta equipada
        if (herramientaEquipada == null) {
            System.out.println(
                "Necesitas equipar un hacha para talar."
            );
            return;
        }

        // Comprobar que sea un hacha
        if (herramientaEquipada.getTipo() != tipoHerramienta.HACHA) {
            System.out.println(
                "Necesitas tener un hacha equipada."
            );
            return;
        }

        int energiaNecesaria =
            arbol.getEnergiaNecesaria();

        if (energia < energiaNecesaria) {
            System.out.println(
                "No tienes suficiente energia."
            );
            return;
        }

        // Usar el hacha equipada
        if (!herramientaEquipada.usar()) {
            return;
        }

        gastarEnergia(energiaNecesaria);

        Recursos madera =
            arbol.recolectarRecurso();

        if (madera != null) {

            inventario.agregarRecurso(
                madera,
                madera.getCantidad()
            );

            System.out.println(
                "Conseguiste " +
                madera.getCantidad() +
                " de madera."
            );
        }

        if (arbol.estaTalado()) {

            mapa.eliminarArbol(arbol);

            System.out.println(
                "El arbol fue talado completamente."
            );
        }
    }
    
    // Comprueba SI se puede picar la roca, sin gastar nada.
    // Se usa antes de empezar la animacion: si no se puede
    // (no hay pico, no hay energia, pico roto), no se anima el golpe.
    public boolean puedeMinar(roca roca) {

        if (roca == null) {
            return false;
        }

        if (herramientaEquipada == null) {

            System.out.println(
                "Necesitas equipar un pico para minar."
            );

            return false;
        }

        if (herramientaEquipada.getTipo() != tipoHerramienta.PICO) {

            System.out.println(
                "Necesitas tener un pico equipado."
            );

            return false;
        }

        if (energia < roca.getEnergiaNecesaria()) {

            System.out.println(
                "No tienes suficiente energia para minar."
            );

            return false;
        }

        if (herramientaEquipada.getDurabilidad() <= 0) {

            System.out.println(
                "El pico no tiene suficiente durabilidad."
            );

            return false;
        }

        return true;
    }

    public void minarRoca(roca roca, Mapa mapa) {

        if (roca == null || mapa == null) {
            return;
        }

        // ==================================================
        // COMPROBAR QUE EL PICO ESTÉ EQUIPADO
        // ==================================================

        if (herramientaEquipada == null) {

            System.out.println(
                "Necesitas equipar un pico para minar."
            );

            return;
        }

        if (herramientaEquipada.getTipo() != tipoHerramienta.PICO) {

            System.out.println(
                "Necesitas tener un pico equipado."
            );

            return;
        }


        // ==================================================
        // COMPROBAR ENERGÍA
        // ==================================================

        int energiaNecesaria =
            roca.getEnergiaNecesaria();

        if (energia < energiaNecesaria) {

            System.out.println(
                "No tienes suficiente energia para minar."
            );

            return;
        }


        // ==================================================
        // USAR EL PICO
        // ==================================================

        if (!herramientaEquipada.usar()) {

            System.out.println(
                "El pico no tiene suficiente durabilidad."
            );

            return;
        }


        // ==================================================
        // GASTAR ENERGÍA
        // ==================================================

        gastarEnergia(energiaNecesaria);


        // ==================================================
        // OBTENER PIEDRA
        // ==================================================

        Recursos piedra =
            roca.recolectarRecurso();

        if (piedra != null) {

            inventario.agregarRecurso(
                piedra,
                piedra.getCantidad()
            );

            System.out.println(
                "Conseguiste " +
                piedra.getCantidad() +
                " de piedra."
            );
        }


        // ==================================================
        // COMPROBAR SI LA ROCA SE AGOTÓ
        // ==================================================

        if (roca.estaAgotada()) {

            mapa.eliminarRoca(roca);

            System.out.println(
                "La roca se agoto."
            );
        }
    }
	
	
	
	
	
}


