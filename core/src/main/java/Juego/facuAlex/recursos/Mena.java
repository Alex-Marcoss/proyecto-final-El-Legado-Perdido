package Juego.facuAlex.recursos;

public class Mena extends roca {

    public enum Tipo {

        //     nombre            recurso que suelta     sprite
        HIERRO("Mena de hierro", Catalogo.MENA_HIERRO, "recursos/MenaHierro.png",
                1, 3, 6, 1, 2),

        ORO("Mena de oro", Catalogo.MENA_ORO, "recursos/MenaOro.png",
                2, 2, 7, 1, 1),

        CARBON("Mena de carbon", Catalogo.CARBON, "recursos/MenaCarbon.png",
                1, 3, 5, 1, 2);

        final String nombre;
        final String recurso;
        final String textura;
        final int nivelMinimo;    // 1 = cualquier pico, 2 = pico de hierro
        final int usos;           // veces que se puede minar
        final int energia;
        final int minimo;         // cantidad mínima por golpe
        final int maximo;         // cantidad máxima por golpe

        Tipo(String nombre, String recurso, String textura,
             int nivelMinimo, int usos, int energia, int minimo, int maximo) {

            this.nombre = nombre;
            this.recurso = recurso;
            this.textura = textura;
            this.nivelMinimo = nivelMinimo;
            this.usos = usos;
            this.energia = energia;
            this.minimo = minimo;
            this.maximo = maximo;
        }
    }

    private final Tipo tipo;

    public Mena(Tipo tipo, float x, float y) {

        // Usa el PNG propio de cada mineral, sin tinte
        super(tipo.nombre, x, y, tipo.textura, null);

        this.tipo = tipo;
        this.usosRestantes = tipo.usos;
    }

    @Override
    public int getNivelMinimo() {
        return tipo.nivelMinimo;
    }

    @Override
    public int getEnergiaNecesaria() {
        return tipo.energia;
    }

    @Override
    public Recursos recolectarRecurso() {

        if (usosRestantes <= 0) {
            return null;
        }

        usosRestantes--;

        int cantidad = tipo.minimo
                + (int) (Math.random() * (tipo.maximo - tipo.minimo + 1));

        return Catalogo.crear(tipo.recurso, cantidad);
    }
}