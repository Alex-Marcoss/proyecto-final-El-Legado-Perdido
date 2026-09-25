package Juego.facuAlex;

import Juego.facuAlex.Herramientas.Herramienta;
import Juego.facuAlex.Herramientas.tipoHerramienta;
import Juego.facuAlex.recursos.Item;
import Juego.facuAlex.recursos.Recursos;

public class inventario {

    private Item[] items;

    private int cantidad;

    public inventario() {

        this.items = new Item[20];

        cantidad = 0;
    }

    // ==========================================================
    // AGREGAR ITEM
    // ==========================================================

    public void agregarItem(Item item) {

        if (item == null) {
            return;
        }

        if (cantidad < items.length) {

            items[cantidad] = item;

            cantidad++;
        }
    }

    // ==========================================================
    // AGREGAR RECURSO
    // ==========================================================

    public void agregarRecurso(
            Recursos recurso,
            int cantidad) {

        for (int i = 0; i < this.cantidad; i++) {

            if (items[i].getNombre().equals(
                    recurso.getNombre())) {

                items[i].agregarCantidad(cantidad);

                return;
            }
        }

        if (this.cantidad < items.length) {

            items[this.cantidad] = recurso;

            this.cantidad++;
        }
    }

    // ==========================================================
    // OBTENER ITEMS
    // ==========================================================

    public Item getItem(int posicion) {

        if (posicion < 0 ||
            posicion >= cantidad) {

            return null;
        }

        return items[posicion];
    }

    public int getCantidad() {

        return cantidad;
    }

    // ==========================================================
    // MOSTRAR INVENTARIO
    // ==========================================================

    public void mostrarInventario() {

        System.out.println(
            "------------------ Inventario ------------------"
        );

        for (int i = 0; i < cantidad; i++) {

            items[i].mostrarInfo();
        }
    }

    // ==========================================================
    // VERIFICAR RECURSO
    // ==========================================================

    public boolean tieneRecurso(
            String nombre,
            int cantidad) {

        for (int i = 0; i < this.cantidad; i++) {

            if (items[i].getNombre().equals(nombre)) {

                if (items[i] instanceof Recursos) {

                    Recursos recurso =
                        (Recursos) items[i];

                    return recurso.getCantidad() >= cantidad;
                }
            }
        }

        return false;
    }

    // ==========================================================
    // VERIFICAR ITEM
    // ==========================================================

    public boolean tieneItem(String nombre) {

        for (int i = 0; i < cantidad; i++) {

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

        for (int i = 0; i < cantidad; i++) {

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

        for (int i = 0; i < this.cantidad; i++) {

            if (items[i].getNombre().equals(nombre)) {

                if (items[i] instanceof Recursos) {

                    Recursos recurso =
                        (Recursos) items[i];

                    if (recurso.getCantidad() >= cantidad) {

                        recurso.agregarCantidad(-cantidad);

                        return true;
                    }
                }
            }
        }

        return false;
    }
}