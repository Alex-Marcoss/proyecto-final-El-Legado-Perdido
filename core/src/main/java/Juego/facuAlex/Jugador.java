package Juego.facuAlex;

import Juego.facuAlex.Herramientas.Herramienta;
import Juego.facuAlex.Herramientas.tipoHerramienta;
import Juego.facuAlex.Mapa.Mapa;
import Juego.facuAlex.enemigos.Animal;
import Juego.facuAlex.enemigos.Enemigo;
import Juego.facuAlex.receta.Receta;
import Juego.facuAlex.recursos.Comida;
import Juego.facuAlex.recursos.Item;
import Juego.facuAlex.recursos.Recursos;
import Juego.facuAlex.sistemas.Construccion;
import Juego.facuAlex.sistemas.Energia;

public class Jugador {

	String nombre;
	int vida;
	int hambre;
	int energia;
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
	    this.sistemaEnergia = new Energia(5, 3);
	}
	
	
	// ------------------------ Movimientos -------------------------------
	
	
	public void mover(float x, float y, Mapa mapa) {

	    float nuevaX = posicionX + x;
	    float nuevaY = posicionY + y;

	    if (mapa.estaDentro(nuevaX, nuevaY)) {

	        posicionX = nuevaX;
	        posicionY = nuevaY;

	    } else {

	        System.out.println("No podes salir del mapa.");
	    }
	}
	
	public void correr(float x, float y, Mapa mapa) {

	    int energiaNecesaria = 2;

	    if (energia < energiaNecesaria) {
	        System.out.println("No tenes suficiente energia para correr.");
	        return;
	    }

	    float nuevaX = posicionX + x;
	    float nuevaY = posicionY + y;

	    if (mapa.estaDentro(nuevaX, nuevaY)) {

	        posicionX = nuevaX;
	        posicionY = nuevaY;

	        gastarEnergia(energiaNecesaria);

	    } else {
	        System.out.println("No podes salir del mapa.");
	    }
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
	    return energia;
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
	
	public void gastarEnergia(int cantidad) {

	    energia = energia - cantidad;

	    if (energia < 0) {
	        energia = 0;
	    }

	    sistemaEnergia.registrarGasto();
	}
	
	public void recuperarEnergia(int cantidad) {
		
		energia = energia + cantidad;
		if(energia > 100) {
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
	}

	public void mostrarHerramienta() {

	    if (herramientaEquipada != null) {
	        System.out.println("Herramienta equipada: " 
	                           + herramientaEquipada.getNombre());
	    } else {
	        System.out.println("No hay ninguna herramienta equipada.");
	    }
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
	        System.out.println(
	            enemigo.getNombre() + " ya esta derrotado."
	        );
	        return;
	    }

	    int daño = 20;
	    int energiaNecesaria = 5;

	    // Comprobar energía
	    if (energia < energiaNecesaria) {

	        System.out.println(
	            "No tenes suficiente energia para atacar."
	        );

	        return;
	    }

	    // Realizar ataque
	    enemigo.recibirDaño(daño);

	    gastarEnergia(energiaNecesaria);

	    System.out.println(
	        "Atacaste a " + enemigo.getNombre() +
	        " y causaste " + daño + " de daño."
	    );

	    if (!enemigo.estaVivo()) {

	        System.out.println(
	            enemigo.getNombre() + " fue derrotado."
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

	
	// ----------------------------------------------
	
	
	
	
	
}


