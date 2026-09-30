package Juego.facuAlex;

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

        for (int i = 0; i < items.length; i++) {

            if (items[i] == null) {
                continue;
            }

            if (items[i].getNombre().equals(nombre)
                    && items[i] instanceof Recursos) {

                Recursos recurso =
                        (Recursos) items[i];

                return recurso.getCantidad() >= cantidad;
            }
        }

        return false;
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

        for (int i = 0; i < items.length; i++) {

            if (items[i] == null) {
                continue;
            }

            if (items[i].getNombre().equals(nombre)
                    && items[i] instanceof Recursos) {

                Recursos recurso =
                        (Recursos) items[i];

                if (recurso.getCantidad() >= cantidad) {

                    recurso.agregarCantidad(-cantidad);

                    // Si se queda sin unidades,
                    // liberar el slot.
                    if (recurso.getCantidad() <= 0) {

                        items[i] = null;

                        this.cantidad--;
                    }

                    return true;
                }
            }
        }

        return false;
    }
}