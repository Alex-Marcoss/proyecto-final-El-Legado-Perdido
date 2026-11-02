package Juego.facuAlex.receta;

import java.util.function.Supplier;

import Juego.facuAlex.inventario.inventario;
import Juego.facuAlex.recursos.Item;

public class Receta {

    // Solo para mostrar en la interfaz (ícono y nombre)
    private final Item resultado;

    private final Ingrediente[] ingredientes;
    
 // Por defecto una receta necesita mesa de fabricación
    private boolean requiereMesa = true;

    // Marca la receta como fabricable sin mesa (devuelve this para encadenar)
    public Receta sinMesa() {
        this.requiereMesa = false;
        return this;
    }

    public boolean requiereMesa() {
        return requiereMesa;
    }
    
    // Crea un item NUEVO cada vez que se fabrica
    private final Supplier<Item> fabrica;

    public Receta(Supplier<Item> fabrica, Ingrediente[] ingredientes) {
        this.fabrica = fabrica;
        this.resultado = fabrica.get();
        this.ingredientes = ingredientes;
    }

    public Item getResultado() {
        return resultado;
    }

    // Item recién creado para entregarle al jugador
    public Item crearResultado() {
        return fabrica.get();
    }

    public Ingrediente[] getIngredientes() {
        return ingredientes;
    }

    public boolean puedeCrear(inventario inventario) {

        for (int i = 0; i < ingredientes.length; i++) {

            Ingrediente ingrediente = ingredientes[i];

            if (!inventario.tieneRecurso(
                    ingrediente.getNombreRecurso(),
                    ingrediente.getCantidad())) {

                return false;
            }
        }

        return true;
    }

    public void gastarIngredientes(inventario inventario) {

        for (int i = 0; i < ingredientes.length; i++) {

            Ingrediente ingrediente = ingredientes[i];

            inventario.gastarRecurso(
                    ingrediente.getNombreRecurso(),
                    ingrediente.getCantidad()
            );
        }
    }
}