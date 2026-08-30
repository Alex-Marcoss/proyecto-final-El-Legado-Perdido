package Juego.facuAlex.Herramientas;

import Juego.facuAlex.recursos.Item;

public class Herramienta extends Item {

	int durabilidad;
	int daño;
	private tipoHerramienta tipo;
		
	
	public Herramienta(String nombre, int durabilidad, int daño, tipoHerramienta tipo) { // constructor
	    super(nombre);
	    this.durabilidad = durabilidad;
	    this.daño = daño;
	    this.tipo = tipo;
	}

    // -----------------------------------------------------------------------------------------------
    
	 public boolean usar() { // funcion para usar las herramientas
	        if (durabilidad > 0) {
	        	
	            durabilidad--;
	            System.out.println(nombre + " usada/o.");
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
    
	  // -----------------------------------------------------------------------------------------------
    
	  public tipoHerramienta getTipo() {
		    return tipo; // obtener tipo de herramienta
		}
	
	
}
