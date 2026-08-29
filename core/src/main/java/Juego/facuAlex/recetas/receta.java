package juego;

public class Receta {

    private Item resultado;
    private Ingrediente[] ingredientes;

    public Receta(Item resultado, Ingrediente[] ingredientes) {
        this.resultado = resultado;
        this.ingredientes = ingredientes;
    }

    public Item getResultado() {
        return resultado;
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