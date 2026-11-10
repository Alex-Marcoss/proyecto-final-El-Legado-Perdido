package Juego.facuAlex.estructuras;

import Juego.facuAlex.inventario.inventario;
import Juego.facuAlex.receta.Ingrediente;
import Juego.facuAlex.recursos.Catalogo;

/**
 * Tabla de costos de las estructuras que se construyen en el modo construccion.
 *
 * Para cambiar un costo, edita SOLO la tabla COSTOS de abajo.
 * El texto del panel, la comprobacion de materiales y el cobro
 * salen de esta misma tabla.
 */
public final class CostosEstructuras {

    // Mismos numeros que usa Principal (teclas 1 a 5)
    public static final int FOGATA = 1;
    public static final int MESA = 2;
    public static final int HORNO = 3;
    public static final int COFRE = 4;
    public static final int CAMA = 5;

    // Nombre de cada estructura (posicion 0 = id 1)
    private static final String[] NOMBRES = {
        "Fogata",
        "Mesa de fabricacion",
        "Horno",
        "Cofre",
        "Cama"
    };

    // =====================================================
    // TABLA DE COSTOS (posicion 0 = id 1)
    // Usar siempre las constantes de Catalogo
    // =====================================================

    private static final Ingrediente[][] COSTOS = {

        // 1 - Fogata
        {
            new Ingrediente(Catalogo.MADERA, 5),
            new Ingrediente(Catalogo.PIEDRA, 4)
        },

        // 2 - Mesa de fabricacion
        {
            new Ingrediente(Catalogo.MADERA, 5),
            new Ingrediente(Catalogo.FIBRA, 2)
        },

        // 3 - Horno
        {
            new Ingrediente(Catalogo.PIEDRA, 8)
        },

        // 4 - Cofre
        {
            new Ingrediente(Catalogo.MADERA, 8),
            new Ingrediente(Catalogo.HIERRO, 1)      // lingote de hierro
        },

        // 5 - Cama
        {
            new Ingrediente(Catalogo.MADERA, 4),
            new Ingrediente(Catalogo.CUERDA, 1),
            new Ingrediente(Catalogo.LANA, 3)
        }
    };

    private CostosEstructuras() {
    }

    // =====================================================
    // CONSULTAS
    // =====================================================

    public static String nombre(int id) {

        if (!idValido(id)) {
            return "Ninguna";
        }

        return NOMBRES[id - 1];
    }

    public static Ingrediente[] getCosto(int id) {

        if (!idValido(id)) {
            return new Ingrediente[0];
        }

        return COSTOS[id - 1];
    }

    // Texto para el panel: "Costo: 5 madera + 2 fibra"
    public static String textoCosto(int id) {

        Ingrediente[] costo = getCosto(id);

        if (costo.length == 0) {
            return "";
        }

        StringBuilder texto = new StringBuilder("Costo: ");

        for (int i = 0; i < costo.length; i++) {

            if (i > 0) {
                texto.append(" + ");
            }

            texto.append(costo[i].getCantidad())
                 .append(" ")
                 .append(nombreVisible(costo[i].getNombreRecurso()));
        }

        return texto.toString();
    }

    // =====================================================
    // MATERIALES
    // =====================================================

    // Devuelve el nombre del primer material que falta, o null si alcanza
    public static String materialFaltante(inventario inv, int id) {

        for (Ingrediente ingrediente : getCosto(id)) {

            if (!inv.tieneRecurso(
                    ingrediente.getNombreRecurso(),
                    ingrediente.getCantidad())) {

                return nombreVisible(ingrediente.getNombreRecurso());
            }
        }

        return null;
    }

    public static boolean puedePagar(inventario inv, int id) {
        return materialFaltante(inv, id) == null;
    }

    // Gasta los materiales (llamar solo despues de comprobar con puedePagar)
    public static void cobrar(inventario inv, int id) {

        for (Ingrediente ingrediente : getCosto(id)) {

            inv.gastarRecurso(
                ingrediente.getNombreRecurso(),
                ingrediente.getCantidad()
            );
        }
    }

    // =====================================================
    // INTERNOS
    // =====================================================

    private static boolean idValido(int id) {
        return id >= 1 && id <= COSTOS.length;
    }

    // Como se muestra cada material en pantalla
    private static String nombreVisible(String nombre) {

        if (Catalogo.HIERRO.equals(nombre)) {
            return "lingote de hierro";
        }

        return nombre.toLowerCase();
    }
}