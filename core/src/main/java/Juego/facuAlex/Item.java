package Juego.facuAlex;

public class Item{
	
	String nombre;
	
	public Item(String nombre) { // constructor
		this.nombre = nombre;
	}

    // ----------------------------------------------------------------
	public String getNombre() {
		return nombre; // obtener nombre
	}
    
	// ----------------------------------------------------------------
	public void mostrarInfo() {
	        System.out.println(nombre); // mostrar el nombre del item
	    }

    // ----------------------------------------------------------------
	public void agregarCantidad(int cantidad) {
        // Por defecto no hace nada, creada para que se pueda utilizar agregar cantidad en inventario
	}


	
}
