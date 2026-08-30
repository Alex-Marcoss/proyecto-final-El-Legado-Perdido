package Juego.facuAlex.recursos;

import Juego.facuAlex.objetoMundo;
import Juego.facuAlex.Herramientas.tipoHerramienta;

public class planta extends objetoMundo {

    private int fibraDisponible;

    public planta(int fibraDisponible) { // constructor
        super("Planta");
        this.fibraDisponible = fibraDisponible;
    }

    // ----------------------------------------------------------------
    
    @Override
    public tipoHerramienta getHerramientaNecesaria() {
        return null; // planta no necesita ninguna herramienta para recolectarse
    }

    // ----------------------------------------------------------------
    
    @Override
    public Recursos recolectarRecurso() { // recoleccion de recurso

        if (fibraDisponible <= 0) {
            return null;
        }
                
        fibraDisponible--;

        return new Recursos("Fibra", 1);
    } 

    // ----------------------------------------------------------------
    
    @Override
    public int getEnergiaNecesaria() {
        return 1; // energia que consume recolectar fibra
    }
}
