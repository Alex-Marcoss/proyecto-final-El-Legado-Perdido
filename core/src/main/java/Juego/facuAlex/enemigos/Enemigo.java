package Juego.facuAlex.enemigos;

public class Enemigo {

	    private String nombre;
	    private int vida;
	    private int daño;

	    public Enemigo(String nombre, int vida, int daño) { // constructor
	        this.nombre = nombre;
	        this.vida = vida;
	        this.daño = daño;
	    }
        // -----------------------------------------------------------------------
    
	    public String getNombre() {
	        return nombre; // obtiene nombre de enemigo
	    }

	    public int getVida() {
	        return vida; // obtiene vida de enemigo
	    }

	    public int getDaño() {
	        return daño; // obtiene el daño que genera el enemigo
	    }
    // -----------------------------------------------------------------------
	    public void recibirDaño(int cantidad) { // funcion para que pierda vida el enemigo

	        vida -= cantidad;

	        if (vida < 0) {
	            vida = 0;
	        }
	    }
    // -----------------------------------------------------------------------
	    public boolean estaVivo() { // funcion que verifica si el enemigo esta vivo o no
	        if (vida > 0) {
	        	return true;
	        }
	    	return false;
	    }
    // -----------------------------------------------------------------------
	    public void mostrarEstado() { // se muestra el estado del enemigo

	        System.out.println("Enemigo: " + nombre);
	        System.out.println("Vida: " + vida);
	        System.out.println("Daño: " + daño);
	    }
    // -----------------------------------------------------------------------
}    
	
	

