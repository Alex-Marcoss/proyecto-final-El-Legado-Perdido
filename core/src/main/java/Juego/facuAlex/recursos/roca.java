package Juego.facuAlex.recursos;

import Juego.facuAlex.objetoMundo;
import Juego.facuAlex.Herramientas.tipoHerramienta;

public class roca extends objetoMundo {

    private int piedraDisponible;
    
    public roca(int piedraDisponible) { // constructor
        super("Roca");
        this.piedraDisponible = piedraDisponible;
    }

    // ----------------------------------------------------------------
    
    @Override
    public tipoHerramienta getHerramientaNecesaria() {
        return tipoHerramienta.PICO; // Roca necesita el tipo de herramienta PICO
    }

    // ----------------------------------------------------------------
    
    @Override
    public Recursos recolectarRecurso() { // Funcion que extrae la piedra

        if (piedraDisponible <= 0) {
            return null;
        }

        int cantidad = (int)(Math.random() * 2) + 1;

        piedraDisponible--;

        return new Recursos("Piedra", cantidad);
    }

    // ----------------------------------------------------------------
    
    @Override
    public int getEnergiaNecesaria() {
        return 5; // Energia necesaria para minar la roca 
    }
}
