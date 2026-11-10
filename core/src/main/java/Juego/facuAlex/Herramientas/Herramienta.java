package Juego.facuAlex.Herramientas;

import Juego.facuAlex.recursos.Item;

public class Herramienta extends Item {

	int durabilidad;
	final int durabilidadMaxima;   // para calcular la barra de desgaste
	int daño;
	private tipoHerramienta tipo;


	public Herramienta(String nombre, int durabilidad, int daño, tipoHerramienta tipo) { // constructor
	    super(nombre);
	    this.durabilidad = durabilidad;
	    this.durabilidadMaxima = durabilidad;
	    this.daño = daño;
	    this.tipo = tipo;

	}


	public int getDanio() {
	    return daño;
	}

    // -----------------------------------------------------------------------------------------------

	 public boolean usar() { // funcion para usar las herramientas
	        if (durabilidad > 0) {

	            durabilidad--;
	            System.out.println(nombre + " usada/o.");

	            if (durabilidad <= 0) {
	                System.out.println(nombre + " se rompio.");
	            }

	            return true;

	        }
	        else {
	            System.out.println(nombre + " está rota.");
	            return false;
	        }
	    }

     // -----------------------------------------------------------------------------------------------

	  // @Override
	  public void mostrarInfo() {
	        System.out.println(nombre + " | Durabilidad: " + durabilidad);
	    }

	  // -----------------------------------------------------------------------------------------------

	  public int getDurabilidad() {
	        return durabilidad; // obtener durabilidad
	    }

	  public int getDurabilidadMaxima() {
	        return durabilidadMaxima;
	    }

	  // true cuando ya no le queda durabilidad (hay que sacarla del inventario)
	  public boolean estaRota() {
	        return durabilidad <= 0;
	    }

	  // 0 = rota, 1 = nueva. Es lo que usa la barra de desgaste
	  public float getFraccionDurabilidad() {
	        if (durabilidadMaxima <= 0) {
	            return 0f;
	        }
	        return (float) durabilidad / durabilidadMaxima;
	    }

	  // true si ya se uso (la barra solo se muestra en ese caso, como en Minecraft)
	  public boolean estaDesgastada() {
	        return durabilidad < durabilidadMaxima;
	    }

	  // -----------------------------------------------------------------------------------------------

	  public tipoHerramienta getTipo() {
		    return tipo; // obtener tipo de herramienta
		}

	// 1 = herramienta común, 2 = de hierro
	  public int getNivel() {
	      return 1;
	  }
}