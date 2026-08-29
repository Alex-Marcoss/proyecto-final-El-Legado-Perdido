package Juego.facuAlex;

public class arbol extends objetoMundo {

    private int maderaDisponible;

    public arbol(int maderaDisponible) { // Constructor
        super("Arbol");
        this.maderaDisponible = maderaDisponible;
    }

    // -------------------------------------------------------------

    @Override
    public tipoHerramienta getHerramientaNecesaria() {
        return tipoHerramienta.HACHA; // el arbol recibe que solo puede ser talado con el tipo de herramienta HACHA
    }

    // ----------------------------------------------------------------
    
    @Override
    public Recursos recolectarRecurso() { // funcion para recolectar recurso madera

        if (maderaDisponible <= 0) {
            return null;
        }

        int cantidad = (int)(Math.random() * 2) + 1;

        maderaDisponible--;

        return new Recursos("Madera", cantidad);
    }

    // ----------------------------------------------------------------
    
    @Override
    public int getEnergiaNecesaria() {
        return 3; // gasta la energia necesaria para talar
    }
}
