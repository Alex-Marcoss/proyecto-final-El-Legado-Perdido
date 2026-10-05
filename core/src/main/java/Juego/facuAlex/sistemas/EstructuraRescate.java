package Juego.facuAlex.sistemas;

import Juego.facuAlex.inventario;
import Juego.facuAlex.receta.Ingrediente;

public class EstructuraRescate {

    private int faseActual;
    private boolean tieneGema;
    private boolean activa;

    private Ingrediente[][] materialesPorFase;

    public EstructuraRescate() {

        faseActual = 1;
        tieneGema = false;
        activa = false;

        materialesPorFase = new Ingrediente[4][];

        // ==================================================
        // FASE 1
        // ==================================================

        materialesPorFase[0] = new Ingrediente[] {
            new Ingrediente("Madera", 10),
            new Ingrediente("Piedra", 5)
        };

        // ==================================================
        // FASE 2
        // ==================================================

        materialesPorFase[1] = new Ingrediente[] {
            new Ingrediente("Madera", 15),
            new Ingrediente("Piedra", 10)
        };

        // ==================================================
        // FASE 3
        // ==================================================

        materialesPorFase[2] = new Ingrediente[] {
            new Ingrediente("Madera", 10),
            new Ingrediente("Piedra", 15)
        };

        // ==================================================
        // FASE 4
        // ==================================================

        materialesPorFase[3] = new Ingrediente[] {
            new Ingrediente("Madera", 15),
            new Ingrediente("Piedra", 20)
        };
    }

    // ==========================================================
    // CONSTRUCCIÓN
    // ==========================================================

    public boolean puedeConstruir(inventario inventario) {

        if (inventario == null) {
            return false;
        }

        if (faseActual > 4) {
            return false;
        }

        Ingrediente[] materiales =
                materialesPorFase[faseActual - 1];

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

    public void construir(inventario inventario) {

        if (faseActual > 4) {

            System.out.println(
                    "La estructura ya esta completamente construida."
            );

            return;
        }

        if (!puedeConstruir(inventario)) {

            System.out.println(
                    "No tenes todos los materiales necesarios."
            );

            mostrarMateriales(inventario);

            return;
        }

        Ingrediente[] materiales =
                materialesPorFase[faseActual - 1];

        // Gastar materiales
        for (int i = 0; i < materiales.length; i++) {

            Ingrediente material = materiales[i];

            inventario.gastarRecurso(
                    material.getNombreRecurso(),
                    material.getCantidad()
            );
        }

        System.out.println(
                "Construiste la fase " + faseActual + "."
        );

        faseActual++;

        if (faseActual <= 4) {

            System.out.println(
                    "Ahora comienza la fase " + faseActual + "."
            );

        } else {

            System.out.println(
                    "================================"
            );

            System.out.println(
                    "ESTRUCTURA COMPLETAMENTE CONSTRUIDA"
            );

            System.out.println(
                    "Ahora necesitas colocar la Gema Azul."
            );

            System.out.println(
                    "================================"
            );
        }
    }

    // ==========================================================
    // MOSTRAR MATERIALES
    // ==========================================================

    public void mostrarMateriales(inventario inventario) {

        if (faseActual > 4) {

            System.out.println(
                    "La estructura ya esta completamente construida."
            );

            return;
        }

        Ingrediente[] materiales =
                materialesPorFase[faseActual - 1];

        System.out.println(
                "===== FASE " + faseActual + " ====="
        );

        System.out.println(
                "Materiales necesarios:"
        );

        for (int i = 0; i < materiales.length; i++) {

            Ingrediente material = materiales[i];

            int cantidadNecesaria =
                    material.getCantidad();

            int cantidadActual =
                    obtenerCantidadRecurso(
                            inventario,
                            material.getNombreRecurso()
                    );

            System.out.println(
                    material.getNombreRecurso()
                    + ": "
                    + cantidadActual
                    + " / "
                    + cantidadNecesaria
            );
        }
    }

    // ==========================================================
    // OBTENER CANTIDAD DE UN RECURSO
    // ==========================================================

    private int obtenerCantidadRecurso(
            inventario inventario,
            String nombre) {

        int cantidadTotal = 0;

        for (int i = 0;
                i < inventario.getCantidadSlots();
                i++) {

            if (inventario.getItem(i) == null) {
                continue;
            }

            if (inventario.getItem(i)
                    instanceof Juego.facuAlex.recursos.Recursos) {

                Juego.facuAlex.recursos.Recursos recurso =
                        (Juego.facuAlex.recursos.Recursos)
                        inventario.getItem(i);

                if (recurso.getNombre().equals(nombre)) {

                    cantidadTotal +=
                            recurso.getCantidad();
                }
            }
        }

        return cantidadTotal;
    }

    // ==========================================================
    // COLOCAR GEMA
    // ==========================================================

    public boolean colocarGema(inventario inventario) {

        if (faseActual <= 4) {

            System.out.println(
                    "Primero tenes que completar todas las fases."
            );

            return false;
        }

        if (tieneGema) {

            System.out.println(
                    "La Gema Azul ya esta colocada."
            );

            return false;
        }

        if (!inventario.tieneItem("Gema Azul")) {

            System.out.println(
                    "No tenes la Gema Azul."
            );

            return false;
        }

        // Sacar la gema del inventario
        inventario.gastarItem("Gema Azul");

        tieneGema = true;

        System.out.println(
                "Colocaste la Gema Azul en la estructura."
        );

        return true;
    }

    // ==========================================================
    // ACTIVAR ESTRUCTURA
    // ==========================================================

    public boolean puedeActivarse() {

        return faseActual > 4
                && tieneGema
                && !activa;
    }

    public void activar() {

        if (!puedeActivarse()) {

            if (faseActual <= 4) {

                System.out.println(
                        "La estructura aun no esta terminada."
                );

            } else if (!tieneGema) {

                System.out.println(
                        "Falta colocar la Gema Azul."
                );

            } else {

                System.out.println(
                        "La estructura ya esta activa."
                );
            }

            return;
        }

        activa = true;

        System.out.println(
                "================================"
        );

        System.out.println(
                "ESTRUCTURA DE RESCATE ACTIVADA"
        );

        System.out.println(
                "Un enorme rayo aparece en el cielo."
        );

        System.out.println(
                "¡El rescate ha sido solicitado!"
        );

        System.out.println(
                "================================"
        );
    }

    // ==========================================================
    // GETTERS
    // ==========================================================

    public int getFaseActual() {
        return faseActual;
    }

    public boolean tieneGema() {
        return tieneGema;
    }

    public boolean estaActiva() {
        return activa;
    }

    public boolean estaCompletamenteConstruida() {
        return faseActual > 4;
    }

    public Ingrediente[] getMaterialesFaseActual() {

        if (faseActual > 4) {
            return new Ingrediente[0];
        }

        return materialesPorFase[faseActual - 1];
    }
}