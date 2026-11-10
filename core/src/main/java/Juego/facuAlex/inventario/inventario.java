package Juego.facuAlex.inventario;

import Juego.facuAlex.Herramientas.Herramienta;
import Juego.facuAlex.Herramientas.tipoHerramienta;
import Juego.facuAlex.recursos.Item;
import Juego.facuAlex.recursos.Recursos;

public class inventario {

    private Item[] items;

    // Cantidad de espacios ocupados
    private int cantidad;

    private static final int CANTIDAD_SLOTS = 30;

    public inventario() {

        items = new Item[CANTIDAD_SLOTS];

        cantidad = 0;
    }

    // ==========================================================
    // AGREGAR ITEM
    // ==========================================================

    public void agregarItem(Item item) {

        if (item == null) {
            return;
        }

        // Buscar primer espacio libre
        for (int i = 0; i < items.length; i++) {

            if (items[i] == null) {

                items[i] = item;
                cantidad++;

                return;
            }
        }

        System.out.println("Inventario lleno.");
    }

    // ==========================================================
    // AGREGAR RECURSO
    // ==========================================================

    public void agregarRecurso(
            Recursos recurso,
            int cantidad) {

        if (recurso == null || cantidad <= 0) {
            return;
        }

        // Primero buscar un recurso del mismo tipo
        for (int i = 0; i < items.length; i++) {

            if (items[i] == null) {
                continue;
            }

            if (items[i].getNombre().equals(recurso.getNombre())
                    && items[i] instanceof Recursos) {

                Recursos recursoExistente =
                        (Recursos) items[i];

                recursoExistente.agregarCantidad(cantidad);

                return;
            }
        }

        // Si no existe, buscar espacio libre
        for (int i = 0; i < items.length; i++) {

            if (items[i] == null) {

                items[i] = recurso;

                this.cantidad++;

                return;
            }
        }

        System.out.println("Inventario lleno.");
    }

    // ==========================================================
    // OBTENER ITEM
    // ==========================================================

    public Item getItem(int posicion) {

        if (posicion < 0 || posicion >= items.length) {
            return null;
        }

        return items[posicion];
    }

    // ==========================================================
    // CANTIDAD DE ITEMS
    // ==========================================================

    public int getCantidad() {

        return cantidad;
    }

    // ==========================================================
    // CANTIDAD DE SLOTS
    // ==========================================================

    public int getCantidadSlots() {

        return items.length;
    }

    // ==========================================================
    // MOVER / INTERCAMBIAR ITEMS
    // ==========================================================

    public boolean intercambiarItems(
            int posicionOrigen,
            int posicionDestino) {

        if (posicionOrigen < 0 ||
            posicionOrigen >= items.length ||
            posicionDestino < 0 ||
            posicionDestino >= items.length) {

            return false;
        }

        if (posicionOrigen == posicionDestino) {
            return false;
        }

        Item temporal = items[posicionOrigen];

        items[posicionOrigen] = items[posicionDestino];

        items[posicionDestino] = temporal;

        return true;
    }

    // ==========================================================
    // MOVER ITEM
    // ==========================================================

    public boolean moverItem(
            int posicionOrigen,
            int posicionDestino) {

        if (posicionOrigen < 0 ||
            posicionOrigen >= items.length ||
            posicionDestino < 0 ||
            posicionDestino >= items.length) {

            return false;
        }

        if (posicionOrigen == posicionDestino) {
            return false;
        }

        if (items[posicionOrigen] == null) {
            return false;
        }

        // Si el destino está vacío
        if (items[posicionDestino] == null) {

            items[posicionDestino] = items[posicionOrigen];

            items[posicionOrigen] = null;

            return true;
        }

        // Mismo recurso en los dos slots: se juntan en un solo stack
        if (items[posicionOrigen] instanceof Recursos
                && items[posicionDestino] instanceof Recursos
                && items[posicionOrigen].getNombre()
                        .equals(items[posicionDestino].getNombre())) {

            ((Recursos) items[posicionDestino]).agregarCantidad(
                    ((Recursos) items[posicionOrigen]).getCantidad());

            items[posicionOrigen] = null;
            this.cantidad--;

            return true;
        }

        // Si hay otro objeto, intercambiar
        return intercambiarItems(
                posicionOrigen,
                posicionDestino
        );
    }

    // ==========================================================
    // MOSTRAR INVENTARIO
    // ==========================================================

    public void mostrarInventario() {

        System.out.println(
                "------------------ Inventario ------------------"
        );

        for (int i = 0; i < items.length; i++) {

            if (items[i] != null) {

                System.out.println(
                        "Slot " + i + ": "
                        + items[i].getNombre()
                );

                items[i].mostrarInfo();
            }
        }
    }

    // ==========================================================
    // VERIFICAR RECURSO
    // ==========================================================

    public boolean tieneRecurso(
            String nombre,
            int cantidad) {

        return contarRecurso(nombre) >= cantidad;
    }

    // Total de un recurso sumando todos los casilleros donde este
    // (un recurso puede estar dividido en varios stacks)
    public int contarRecurso(String nombre) {

        int total = 0;

        for (int i = 0; i < items.length; i++) {

            if (items[i] instanceof Recursos
                    && items[i].getNombre().equals(nombre)) {

                total += ((Recursos) items[i]).getCantidad();
            }
        }

        return total;
    }

    // ==========================================================
    // VERIFICAR ITEM
    // ==========================================================

    public boolean tieneItem(String nombre) {

        for (int i = 0; i < items.length; i++) {

            if (items[i] == null) {
                continue;
            }

            if (items[i].getNombre().equals(nombre)) {

                return true;
            }
        }

        return false;
    }
    
    
	 // ==========================================================
	 // GASTAR ITEM
	 // ==========================================================
	
	 public boolean gastarItem(String nombre) {
	
	     for (int i = 0; i < items.length; i++) {
	
	         if (items[i] == null) {
	             continue;
	         }
	
	         if (items[i].getNombre().equals(nombre)) {
	
	             items[i] = null;
	             cantidad--;
	
	             return true;
	         }
	     }
	
	     return false;
	 }

    // ==========================================================
    // OBTENER HERRAMIENTA
    // ==========================================================

    public Herramienta obtenerHerramienta(
            tipoHerramienta tipo) {

        for (int i = 0; i < items.length; i++) {

            if (items[i] instanceof Herramienta) {

                Herramienta herramienta =
                        (Herramienta) items[i];

                if (herramienta.getTipo() == tipo) {

                    return herramienta;
                }
            }
        }

        return null;
    }

    // ==========================================================
    // GASTAR RECURSO
    // ==========================================================

    public boolean gastarRecurso(
            String nombre,
            int cantidad) {

        if (cantidad <= 0) {
            return true;
        }

        if (contarRecurso(nombre) < cantidad) {
            return false;
        }

        int falta = cantidad;

        for (int i = 0; i < items.length && falta > 0; i++) {

            if (!(items[i] instanceof Recursos)
                    || !items[i].getNombre().equals(nombre)) {
                continue;
            }

            Recursos recurso = (Recursos) items[i];

            int sacar = Math.min(falta, recurso.getCantidad());

            recurso.agregarCantidad(-sacar);
            falta -= sacar;

            // Si se queda sin unidades, liberar el slot.
            if (recurso.getCantidad() <= 0) {

                items[i] = null;

                this.cantidad--;
            }
        }

        return true;
    }

    // Gasta 1 unidad de ESE stack en particular (el que el jugador
    // tiene en la mano o en la barra), no de otro del mismo recurso.
    // Devuelve false si ese stack no esta en el inventario.
    public boolean gastarUnidadDe(Recursos stack) {

        for (int i = 0; i < items.length; i++) {

            if (items[i] == stack && stack != null) {

                stack.agregarCantidad(-1);

                if (stack.getCantidad() <= 0) {

                    items[i] = null;

                    this.cantidad--;
                }

                return true;
            }
        }

        return false;
    }

    // ==========================================================
    // DIVIDIR UN STACK
    // ==========================================================

    // Pasa "cantidad" unidades del stack de "origen" al slot "destino".
    // El destino tiene que estar vacio o tener el mismo recurso.
    // Siempre queda al menos 1 unidad en el origen.
    public boolean dividirRecurso(
            int origen,
            int destino,
            int cantidad) {

        if (origen < 0 || origen >= items.length
                || destino < 0 || destino >= items.length
                || origen == destino
                || cantidad <= 0) {

            return false;
        }

        if (!(items[origen] instanceof Recursos)) {
            return false;
        }

        Recursos fuente = (Recursos) items[origen];

        if (cantidad >= fuente.getCantidad()) {
            return false;
        }

        // Destino vacio: nace un stack nuevo
        if (items[destino] == null) {

            Recursos nuevo = fuente.separar(cantidad);

            if (nuevo == null) {
                return false;
            }

            items[destino] = nuevo;
            this.cantidad++;

            return true;
        }

        // Mismo recurso: se suma al stack que ya esta
        if (items[destino] instanceof Recursos
                && items[destino].getNombre().equals(fuente.getNombre())) {

            fuente.agregarCantidad(-cantidad);
            ((Recursos) items[destino]).agregarCantidad(cantidad);

            return true;
        }

        return false;
    }

	 // ==========================================================
	 // TRANSFERENCIAS ENTRE INVENTARIOS
	 // ==========================================================
	
	 public Item quitarItem(int posicion) {
	
	     if (posicion < 0 || posicion >= items.length) {
	         return null;
	     }
	
	     Item item = items[posicion];
	
	     if (item != null) {
	         items[posicion] = null;
	         cantidad--;
	     }
	
	     return item;
	 }
	
	 // Solo coloca el item si el slot está vacío
	 public boolean ponerItem(int posicion, Item item) {
	
	     if (item == null
	             || posicion < 0
	             || posicion >= items.length
	             || items[posicion] != null) {
	
	         return false;
	     }
	
	     items[posicion] = item;
	     cantidad++;
	
	     return true;
	 }
	
	 public Recursos buscarRecurso(String nombre) {
	
	     for (int i = 0; i < items.length; i++) {
	
	         if (items[i] instanceof Recursos
	                 && items[i].getNombre().equals(nombre)) {
	
	             return (Recursos) items[i];
	         }
	     }
	
	     return null;
	 }
	
	 public int primerSlotLibre() {
	
	     for (int i = 0; i < items.length; i++) {
	
	         if (items[i] == null) {
	             return i;
	         }
	     }
	
	     return -1;
	 }
    
}
