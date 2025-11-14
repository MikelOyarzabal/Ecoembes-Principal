package DS_06.Ecoembes.dto;

import java.util.ArrayList;

import DS_06.Ecoembes.entity.Contenedor;
import DS_06.Ecoembes.entity.Llenado;

public class PlantaReciclajeDTO {
	private long id;
	private String nombre;
	private int capacidad;
	private int capacidadDisponible;
	private ArrayList<Contenedor> listaContenedor;

	public PlantaReciclajeDTO() {
		super();
		this.listaContenedor = new ArrayList<>();
        this.capacidad = 0;
        this.capacidadDisponible = 0;
	}
	public PlantaReciclajeDTO(long id, String nombre, ArrayList<Contenedor>listaContenedor) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.listaContenedor = listaContenedor != null ? listaContenedor : new ArrayList<>();
		this.calcularCapacidades();
	}
	private void calcularCapacidades() {
	    this.capacidad = 0;
	    this.capacidadDisponible = 0;
	    
	    if (this.listaContenedor != null && !this.listaContenedor.isEmpty()) {
	        for (Contenedor contenedor : this.listaContenedor) {
	            // Capacidad total: suma de todas las capacidades de contenedores
	            this.capacidad += contenedor.getCapacidad();
	            
	            // Capacidad disponible: basada en el nivel de llenado
	            float factorDisponibilidad = calcularFactorDisponibilidad(contenedor.getNivelDeLlenado());
	            this.capacidadDisponible += contenedor.getCapacidad() * factorDisponibilidad;
	        }
	    }
	}

	// Método auxiliar para calcular el factor de disponibilidad según el nivel de llenado
	private float calcularFactorDisponibilidad(Llenado nivelLlenado) {
	    if (nivelLlenado == null) return 1.0f;
	    
	    // Usamos los valores del enum: VERDE=100, NARANJA=50, ROJO=0
	    // El factor de disponibilidad es: (100 - valor_llenado) / 100
	    return (100 - nivelLlenado.getValor()) / 100.0f;
	}
	

	
	public ArrayList<Contenedor> getListaContenedor() {
		return listaContenedor;
	}
	public void setListaContenedor(ArrayList<Contenedor> listaContenedor) {
		this.listaContenedor = listaContenedor;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getCapacidad() {
		return capacidad;
	}
	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}
	public int getCapacidadDisponible() {
		return capacidadDisponible;
	}
	public void setCapacidadDisponible(int capacidadDisponible) {
		this.capacidadDisponible = capacidadDisponible;
	}
	
}
