package Juego.facuAlex;

public class inventario{
	
	Item[] items; 
	int cantidad;
	
	public inventario() { // constructor inventario
		
		this.items = new Item[10];
		cantidad = 0;
	}
	
	// --------------------------------------------------------------------
	// agregar item, recurso y mostrar inventario
	
	// ---------------------------------- item ----------------------------------------------
	public void agregarItem(Item item) {

	    if (cantidad < items.length) {
	        items[cantidad] = item;
	        cantidad++;
	    }
	}
	
	// ---------------------------------- Recurso ---------------------------------------------- 
	
	public void agregarRecurso(Recursos recurso, int cantidad) {

	    for (int i = 0; i < this.cantidad; i++) {

	        if (items[i].getNombre().equals(recurso.getNombre())) { // si el nombre del item es igual al obtenido
	            items[i].agregarCantidad(cantidad);
	            return;
	        }
	    }

	    if (this.cantidad < items.length) {
	        items[this.cantidad] = recurso;
	        this.cantidad++;
	    }
	}
	
	// ---------------------------------- Mostrar Inventario  ---------------------------------------------- 
	
	public void mostrarInventario() {
		
		System.out.println("------------------ Inventario ------------------");
	    for (int i = 0; i < cantidad; i++) {
	        items[i].mostrarInfo();
	    }
	}
	
	// -------------------------------------------------------------------------------- 
	// Verificacion y gasto de recursos
	
	// verificacion
	
	public boolean tieneRecurso(String nombre, int cantidad) {

	    for (int i = 0; i < this.cantidad; i++) {

	        if (items[i].getNombre().equals(nombre)) { // si el nombre del item es igual al obtenido

	            if (items[i] instanceof Recursos) { // si item pertenece a recursos

	                Recursos recurso = (Recursos) items[i];

	                return recurso.getCantidad() >= cantidad;
	            }
	        }
	    }

	    return false;
	}
	
	// ---------------------------------- Gasto  ---------------------------------------------- 
	
	public boolean gastarRecurso(String nombre, int cantidad) {

	    for (int i = 0; i < this.cantidad; i++) {

	        if (items[i].getNombre().equals(nombre)) { // si el nombre del item es igual al obtenido

	            if (items[i] instanceof Recursos) { // si item pertenece a recursos

	                Recursos recurso = (Recursos) items[i];

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
	

