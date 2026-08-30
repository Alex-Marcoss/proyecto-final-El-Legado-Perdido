package Juego.facuAlex;

import Juego.facuAlex.Herramientas.tipoHerramienta;
import Juego.facuAlex.recursos.Recursos;

public class objetoMundo {
	
    protected String nombre;

    
    
    public objetoMundo(String nombre) { // constructor
        this.nombre = nombre;
    }
    
    public String getNombre() { // obtener nombre
        return nombre;
    }
    
    // ----------------------------------------------------------------------
    // interaccion con jugador
    
    public void interactuar(Jugador jugador) {
        System.out.println("No se puede interactuar con este objeto.");
    }
    
    
    // validar tipo de herramienta para el bojeto
    public tipoHerramienta getHerramientaNecesaria() {
        return null;
    }
    
   // recolectar recrusos
    public Recursos recolectarRecurso() {
        return null;
    }

    // obtener energia necesaria
    public int getEnergiaNecesaria() {
        return 0;
    }
}
