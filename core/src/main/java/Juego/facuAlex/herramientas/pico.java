package juego;

public class Pico extends Herramienta { 
    
    public Pico(int durabilidad, int daño) { // constructor

        super("Pico", durabilidad, daño, tipoHerramienta.PICO); //definicion de pico como herramienta
    }
}	
