package Juego;

public class GeneradorEnemigos {

    public Enemigo generar(CicloDia ciclo) {

        if (!ciclo.esNoche()) { // si durante el ciclo dia/noche es de dia los enemigos no apareceran
            System.out.println("Es de día. No aparecen enemigos.");
            return null;
        }

        int numero = (int)(Math.random() * 2);
        
        if (numero == 0) { // el enemigo varia dependiendo el math.random que elige un numero aleatorio
            return new Lobo();
        } 
        else {
            return new Araña(); // el enemigo varia dependiendo el math.random que elige un numero aleatorio
        }
    }
}
