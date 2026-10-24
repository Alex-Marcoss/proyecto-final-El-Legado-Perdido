
package Juego.facuAlex.jugador;

import com.badlogic.gdx.math.Rectangle;

import Juego.facuAlex.recursos.*;
import Juego.facuAlex.Herramientas.Herramienta;
import Juego.facuAlex.Herramientas.tipoHerramienta;
import Juego.facuAlex.Mapa.InteriorTemplo;
import Juego.facuAlex.Mapa.Mapa;
import Juego.facuAlex.Mapa.Templo;
import Juego.facuAlex.enemigos.Animal;
import Juego.facuAlex.enemigos.Enemigo;
import Juego.facuAlex.receta.Receta;
import Juego.facuAlex.sistemas.Construccion;
import Juego.facuAlex.sistemas.Energia;
import Juego.facuAlex.sistemas.Supervivencia;
import Juego.facuAlex.inventario.inventario;

public class Jugador {

    private String nombre;
    private int vida;
    private int hambre;
    private float energia;

    private Herramienta herramientaEquipada;
    private float posicionX;
    private float posicionY;

    private Energia sistemaEnergia;
    private Supervivencia sistemaSupervivencia;

    private InteriorTemplo interior;
    private Templo templo;

    private inventario inventario;

    private boolean golpeRecibido = false;

    // Hitbox de los pies del jugador.
    private static final float HITBOX_ANCHO = 24f;
    private static final float HITBOX_ALTO = 12f;
    private static final float HITBOX_OFFSET_X = 20f;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.vida = 100;
        this.hambre = 100;
        this.energia = 100;

        this.inventario = new inventario();
        this.posicionX = 0;
        this.posicionY = 0;

        this.sistemaEnergia = new Energia(3.67f);
        this.sistemaSupervivencia = new Supervivencia(10, 5);
    }

    // --------------------------------------------------
    // MOVIMIENTO Y COLISIONES
    // --------------------------------------------------

    public Rectangle getHitbox() {
        return crearHitbox(posicionX, posicionY);
    }

    private Rectangle crearHitbox(float x, float y) {
        return new Rectangle(
            x + HITBOX_OFFSET_X,
            y,
            HITBOX_ANCHO,
            HITBOX_ALTO
        );
    }

    public void setInterior(InteriorTemplo interior) {
        this.interior = interior;
    }

    public void setTemplo(Templo templo) {
        this.templo = templo;
    }

    private boolean puedeCaminar(Rectangle hitbox, Mapa mapa) {
        if (interior != null) {
            return !interior.colisiona(hitbox);
        }

        if (templo != null && templo.colisiona(hitbox)) {
            return false;
        }

        return mapa.puedeCaminar(hitbox);
    }

    private boolean moverConColision(float x, float y, Mapa mapa) {
        boolean seMovio = false;

        float nuevaX = posicionX + x;

        if (puedeCaminar(crearHitbox(nuevaX, posicionY), mapa)) {
            posicionX = nuevaX;
            seMovio = true;
        }

        float nuevaY = posicionY + y;

        if (puedeCaminar(crearHitbox(posicionX, nuevaY), mapa)) {
            posicionY = nuevaY;
            seMovio = true;
        }

        return seMovio;
    }

    public void setPosicion(float x, float y) {
        posicionX = x;
        posicionY = y;
    }

    public void mover(float x, float y, Mapa mapa) {
        moverConColision(x, y, mapa);
    }

    public boolean correr(float x, float y, Mapa mapa, float delta) {
        float energiaNecesaria = 10f * delta;

        if (energia <= 0) {
            return false;
        }

        if (!moverConColision(x, y, mapa)) {
            return false;
        }

        gastarEnergia(energiaNecesaria);
        return true;
    }

    public float getPosicionX() {
        return posicionX;
    }

    public float getPosicionY() {
        return posicionY;
    }

    // --------------------------------------------------
    // INVENTARIO
    // --------------------------------------------------

    public void recogerItem(Item item) {
        if (item != null) {
            inventario.agregarItem(item);
        }
    }

    public void mostrarInventario() {
        inventario.mostrarInventario();
    }

    public inventario getInventario() {
        return inventario;
    }

    // --------------------------------------------------
    // CONSTRUCCIONES: COMPROBAR Y GASTAR MATERIALES
    // --------------------------------------------------

    public boolean tieneMateriales(String nombre, int cantidad) {
        if (nombre == null || cantidad <= 0) {
            return false;
        }

        return inventario.tieneRecurso(nombre, cantidad);
    }

    public boolean gastarMateriales(String nombre, int cantidad) {
        if (!tieneMateriales(nombre, cantidad)) {
            return false;
        }

        inventario.gastarRecurso(nombre, cantidad);

        return !inventario.tieneRecurso(nombre, cantidad);
    }

    // --------------------------------------------------
    // ESTADO Y SUPERVIVENCIA
    // --------------------------------------------------

    public void mostrarEstado() {
        System.out.println("Vida: " + vida);
        System.out.println("Hambre: " + hambre);
        System.out.println("Energia: " + energia);
    }

    public void comprobarSupervivencia() {
        if (hambre == 0) {
            recibirDanio(10);
        }
    }

    public String getNombre() {
        return nombre;
    }

    public int getVida() {
        return vida;
    }

    public int getHambre() {
        return hambre;
    }

    public int getEnergia() {
        return (int) energia;
    }

    public void recibirDanio(int cantidad) {
        if (cantidad <= 0 || vida <= 0) {
            return;
        }

        vida -= cantidad;

        if (vida < 0) {
            vida = 0;
        }

        if (sistemaSupervivencia != null) {
            sistemaSupervivencia.registrarDanio();
        }

        if (vida == 0) {
            System.out.println("El jugador ha muerto.");
            System.out.println("GAME OVER");
        }
    }

    public void marcarGolpeRecibido() {
        if (vida > 0) {
            golpeRecibido = true;
        }
    }

    public boolean consumirGolpeRecibido() {
        boolean resultado = golpeRecibido;
        golpeRecibido = false;
        return resultado;
    }

    public void curar(int cantidad) {
        if (cantidad <= 0) {
            return;
        }

        vida += cantidad;

        if (vida > 100) {
            vida = 100;
        }
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public boolean gameOver() {
        return vida <= 0;
    }

    // --------------------------------------------------
    // ENERGÍA
    // --------------------------------------------------

    public void gastarEnergia(float cantidad) {
        if (cantidad <= 0) {
            return;
        }

        energia -= cantidad;

        if (energia < 0) {
            energia = 0;
        }

        sistemaEnergia.registrarGasto();
    }

    public void recuperarEnergia(float cantidad) {
        if (cantidad <= 0) {
            return;
        }

        energia += cantidad;

        if (energia > 100) {
            energia = 100;
        }
    }

    public void actualizarEnergia(float segundos) {
        sistemaEnergia.actualizar(this, segundos);
    }

    public Energia getSistemaEnergia() {
        return sistemaEnergia;
    }

    // --------------------------------------------------
    // HAMBRE Y COMIDA
    // --------------------------------------------------

    public void perderHambre(int cantidad) {
        hambre -= cantidad;

        if (hambre < 0) {
            hambre = 0;
        }
    }

    public void recuperarHambre(int cantidad) {
        hambre += cantidad;

        if (hambre > 100) {
            hambre = 100;
        }
    }

    public void comer(Comida comida) {
        if (comida == null) {
            return;
        }

        if (!inventario.tieneRecurso(comida.getNombre(), 1)) {
            System.out.println("No tenes " + comida.getNombre() + ".");
            return;
        }

        int hambreAntes = hambre;

        recuperarHambre(comida.getHambreRecuperada());
        inventario.gastarRecurso(comida.getNombre(), 1);

        int hambreRecuperada = hambre - hambreAntes;

        System.out.println(
            "Comiste " + comida.getNombre()
            + " y recuperaste " + hambreRecuperada
            + " de hambre."
        );
    }

    public void actualizarSupervivencia(float delta) {
        sistemaSupervivencia.actualizar(this, delta);
    }

    public Supervivencia getSistemaSupervivencia() {
        return sistemaSupervivencia;
    }

    // --------------------------------------------------
    // HERRAMIENTAS
    // --------------------------------------------------

    public void equiparHerramienta(Herramienta herramienta) {
        herramientaEquipada = herramienta;

        if (herramienta != null) {
            System.out.println("Equipaste: " + herramienta.getNombre());
        } else {
            System.out.println("No tenes ninguna herramienta equipada.");
        }
    }

    public Herramienta getHerramientaEquipada() {
        return herramientaEquipada;
    }

    // --------------------------------------------------
    // RECOLECCIÓN GENERAL
    // --------------------------------------------------

    public void interactuar(objetoMundo objeto) {
        if (objeto == null) {
            return;
        }

        tipoHerramienta herramientaNecesaria =
            objeto.getHerramientaNecesaria();

        if (herramientaNecesaria != null) {
            if (herramientaEquipada == null) {
                System.out.println("No tenes una herramienta equipada.");
                return;
            }

            if (herramientaEquipada.getTipo() != herramientaNecesaria) {
                System.out.println("La herramienta no sirve para este objeto.");
                return;
            }
        }

        if (energia < objeto.getEnergiaNecesaria()) {
            System.out.println("No tenes suficiente energia.");
            return;
        }

        Recursos recurso = objeto.recolectarRecurso();

        if (recurso == null) {
            System.out.println("No queda ningun recurso.");
            return;
        }

        if (herramientaEquipada != null
                && !herramientaEquipada.usar()) {
            return;
        }

        gastarEnergia(objeto.getEnergiaNecesaria());

        inventario.agregarRecurso(
            recurso,
            recurso.getCantidad()
        );

        System.out.println(
            "Conseguiste " + recurso.getCantidad()
            + " de " + recurso.getNombre() + "."
        );
    }

    // --------------------------------------------------
    // COMBATE
    // --------------------------------------------------

    public void atacar(Enemigo enemigo) {
        if (enemigo == null || !enemigo.estaVivo()) {
            return;
        }

        int danio = 20;
        enemigo.recibirDaño(danio);

        System.out.println(
            "Atacaste a " + enemigo.getNombre()
            + " y causaste " + danio + " de daño."
        );

        if (!enemigo.estaVivo()) {
            System.out.println(enemigo.getNombre() + " fue derrotado.");
        }
    }

    // --------------------------------------------------
    // RECOGER RECURSOS DE ANIMALES
    // --------------------------------------------------

    public void recogerDropsAnimal(Animal animal) {
        if (animal == null) {
            return;
        }

        Recursos[] drops = animal.obtenerDrops();

        if (drops == null) {
            System.out.println("Este animal no tiene recursos para entregar.");
            return;
        }

        for (Recursos drop : drops) {
            if (drop == null) {
                continue;
            }

            inventario.agregarRecurso(drop, drop.getCantidad());

            System.out.println(
                "Conseguiste " + drop.getCantidad()
                + " de " + drop.getNombre() + "."
            );
        }
    }

    // --------------------------------------------------
    // FABRICACIÓN
    // --------------------------------------------------

    public void fabricar(Receta receta) {
        if (receta == null) {
            return;
        }

        if (!receta.puedeCrear(inventario)) {
            System.out.println("No tenes los recursos necesarios.");
            return;
        }

        receta.gastarIngredientes(inventario);
        inventario.agregarItem(receta.getResultado());

        System.out.println(
            "Fabricaste: " + receta.getResultado().getNombre()
        );
    }

    // --------------------------------------------------
    // CONSTRUCCIÓN MEDIANTE EL SISTEMA EXISTENTE
    // --------------------------------------------------

    public void construir(Construccion construccion) {
        if (construccion == null) {
            return;
        }

        if (!construccion.puedeConstruir(inventario)) {
            System.out.println("No tenes los materiales necesarios.");
            return;
        }

        construccion.gastarMateriales(inventario);

        System.out.println("Construiste: " + construccion.getNombre());
    }

    // --------------------------------------------------
    // TALADO DE ÁRBOLES
    // --------------------------------------------------

    public boolean puedeTalar(arbol arbol) {
        if (arbol == null) {
            return false;
        }

        if (herramientaEquipada == null
                || herramientaEquipada.getTipo() != tipoHerramienta.HACHA) {
            System.out.println("Necesitas tener un hacha equipada.");
            return false;
        }

        if (energia < arbol.getEnergiaNecesaria()) {
            System.out.println("No tienes suficiente energia.");
            return false;
        }

        if (herramientaEquipada.getDurabilidad() <= 0) {
            System.out.println("El hacha no tiene suficiente durabilidad.");
            return false;
        }

        return true;
    }

    public void talarArbol(arbol arbol, Mapa mapa) {
        if (arbol == null || mapa == null) {
            return;
        }

        if (herramientaEquipada == null
                || herramientaEquipada.getTipo() != tipoHerramienta.HACHA) {
            System.out.println("Necesitas tener un hacha equipada.");
            return;
        }

        int energiaNecesaria = arbol.getEnergiaNecesaria();

        if (energia < energiaNecesaria) {
            System.out.println("No tienes suficiente energia.");
            return;
        }

        if (!herramientaEquipada.usar()) {
            return;
        }

        gastarEnergia(energiaNecesaria);

        Recursos madera = arbol.recolectarRecurso();

        if (madera != null) {
            inventario.agregarRecurso(madera, madera.getCantidad());

            System.out.println(
                "Conseguiste " + madera.getCantidad() + " de madera."
            );
        }

        if (arbol.estaTalado()) {
            mapa.eliminarArbol(arbol);
            System.out.println("El arbol fue talado completamente.");
        }
    }

    // --------------------------------------------------
    // MINADO DE ROCAS
    // --------------------------------------------------

    public boolean puedeMinar(roca roca) {
        if (roca == null) {
            return false;
        }

        if (herramientaEquipada == null
                || herramientaEquipada.getTipo() != tipoHerramienta.PICO) {
            System.out.println("Necesitas tener un pico equipado.");
            return false;
        }

        if (energia < roca.getEnergiaNecesaria()) {
            System.out.println("No tienes suficiente energia para minar.");
            return false;
        }

        if (herramientaEquipada.getDurabilidad() <= 0) {
            System.out.println("El pico no tiene suficiente durabilidad.");
            return false;
        }

        return true;
    }

    public void minarRoca(roca roca, Mapa mapa) {
        if (roca == null || mapa == null) {
            return;
        }

        if (herramientaEquipada == null
                || herramientaEquipada.getTipo() != tipoHerramienta.PICO) {
            System.out.println("Necesitas tener un pico equipado.");
            return;
        }

        int energiaNecesaria = roca.getEnergiaNecesaria();

        if (energia < energiaNecesaria) {
            System.out.println("No tienes suficiente energia para minar.");
            return;
        }

        if (!herramientaEquipada.usar()) {
            System.out.println("El pico no tiene suficiente durabilidad.");
            return;
        }

        gastarEnergia(energiaNecesaria);

        Recursos piedra = roca.recolectarRecurso();

        if (piedra != null) {
            inventario.agregarRecurso(piedra, piedra.getCantidad());

            System.out.println(
                "Conseguiste " + piedra.getCantidad() + " de piedra."
            );
        }

        if (roca.estaAgotada()) {
            mapa.eliminarRoca(roca);
            System.out.println("La roca se agoto.");
        }
    }
}
