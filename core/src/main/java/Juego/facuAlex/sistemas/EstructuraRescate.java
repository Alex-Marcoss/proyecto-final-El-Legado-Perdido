package Juego.facuAlex.sistemas;

import Juego.facuAlex.inventario;
import Juego.facuAlex.receta.Ingrediente;

public class EstructuraRescate {

    private boolean tieneGema;
    private boolean activa;
    private Ingrediente[] materiales;

    public EstructuraRescate(Ingrediente[] materiales) {

        this.tieneGema = false;
        this.activa = false;
        this.materiales = materiales;
    }

    public void colocarGema() {

        tieneGema = true;

        System.out.println(
            "Colocaste la gema en la estructura."
        );
    }

    public boolean puedeActivarse(inventario inventario) {

        if (!tieneGema) {
            return false;
        }

        for (int i = 0; i < materiales.length; i++) {

            Ingrediente material = materiales[i];

            if (!inventario.tieneRecurso(
                    material.getNombreRecurso(),
                    material.getCantidad())) {

                return false;
            }
        }

        return true;
    }

    public void activar(inventario inventario) {

        if (!puedeActivarse(inventario)) {

            System.out.println(
                "La estructura no puede activarse."
            );

            return;
        }

        for (int i = 0; i < materiales.length; i++) {

            Ingrediente material = materiales[i];

            inventario.gastarRecurso(
                material.getNombreRecurso(),
                material.getCantidad()
            );
        }

        activa = true;

        System.out.println( "La estructura se activo.");
          

        System.out.println("Un enorme rayo aparece en el cielo.");
           

        System.out.println("¡El rescate ha sido solicitado!");
           
    }

    public boolean estaActiva() {
        return activa;
    }
}