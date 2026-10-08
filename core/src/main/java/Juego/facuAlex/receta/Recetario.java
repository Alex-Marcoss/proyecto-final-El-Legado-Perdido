package Juego.facuAlex.receta;

import java.util.ArrayList;
import java.util.List;

import Juego.facuAlex.Herramientas.hacha;
import Juego.facuAlex.Herramientas.pico;

public class Recetario {

    private List<Receta> recetas;

    public Recetario() {

        recetas = new ArrayList<>();

        // ==========================================
        // HACHA
        // ==========================================

        recetas.add(
            new Receta(
                new hacha(20, 10),
                new Ingrediente[] {
                    new Ingrediente("Madera", 3),
                    new Ingrediente("Piedra", 2),
                    new Ingrediente("Fibra",1)
                }
            )
        );

        // ==========================================
        // PICO
        // ==========================================

        recetas.add(
            new Receta(
                new pico(20, 15),
                new Ingrediente[] {
                    new Ingrediente("Madera", 3),
                    new Ingrediente("Piedra", 4),
                    new Ingrediente("Fibra",1)
                }
            )
        );
    }

    public List<Receta> getRecetas() {
        return recetas;
    }

    public Receta getReceta(int indice) {

        if (indice < 0 || indice >= recetas.size()) {
            return null;
        }

        return recetas.get(indice);
    }

    public int getCantidadRecetas() {
        return recetas.size();
    }
}