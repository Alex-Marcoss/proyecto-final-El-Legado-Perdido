package Juego.facuAlex;

public class Recursos extends Item{

	int cantidad;
	
	public Recursos(String nombre, int cantidad) { // constructor
		super(nombre);
		this.cantidad = cantidad;
	}

    // ----------------------------------------------------------------
    
	public int getCantidad() {
	        return cantidad; //obtener cantidad
	   }

    // ----------------------------------------------------------------
    
	@Override
	 public void agregarCantidad(int cantidad) {
	        this.cantidad += cantidad; // sobreescribir agregarcantidad de item para sumar las cantidades del recurso
	    }

    // ----------------------------------------------------------------
    
	@Override
    public void mostrarInfo() {
        System.out.println(nombre + ": " + cantidad); // sobreescribir el nombre de item para agregarle cantidad
    }
	
	
	
}
