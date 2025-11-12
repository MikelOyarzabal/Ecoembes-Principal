package DS_06.Ecoembes.entity;

import java.util.ArrayList;


public class PlantaReciclaje {
	private long id;
	private String nombre;
	private int capacidad;
	private int capacidadDisponible;
	private ArrayList<Contenedor> listaContenedor;
	public PlantaReciclaje() {
		super();
		this.listaContenedor = new ArrayList<>();
	}
	public PlantaReciclaje(long id, String nombre, ArrayList<Contenedor>listaContenedor) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.listaContenedor = listaContenedor != null ? listaContenedor : new ArrayList<>();
		this.setCapacidad();
	}
	
	public ArrayList<Contenedor> getListaContenedor() {
		return listaContenedor;
	}
	public void setListaContenedor(ArrayList<Contenedor> listaContenedor) {
		this.listaContenedor = listaContenedor != null ? listaContenedor : new ArrayList<>();
        this.setCapacidad(); // Recalcular capacidad cuando se cambia la lista
        this.setCapacidadDisponible(); // Recalcular capacidad disponible
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
	public void setCapacidad() {
		this.capacidad=0;
		if (listaContenedor != null && !listaContenedor.isEmpty()) {
		for(Contenedor c: listaContenedor) {
			this.capacidad += c.getCapacidad();
		}
		}
	}
	public int getCapacidadDisponible() {
		return capacidadDisponible;
	}
	public void setCapacidadDisponible() {
		this.capacidadDisponible = 0;
        if (listaContenedor != null && !listaContenedor.isEmpty()) { 
            for (Contenedor c : listaContenedor) {
                int nivelLlenado = c.getNivelDeLlenado() != null ? c.getNivelDeLlenado().getValor() : 0;
                int capacidadDisponibleContenedor = (int) (c.getCapacidad() * (100 - nivelLlenado) / 100);
                this.capacidadDisponible += capacidadDisponibleContenedor;
            }
        }
    }
	public void agregarContenedor(Contenedor contenedor) {
        if (contenedor != null) {
            if (this.listaContenedor == null) {
                this.listaContenedor = new ArrayList<>();
            }
            this.listaContenedor.add(contenedor);
            this.setCapacidad(); 
            this.setCapacidadDisponible(); 
        }
    }
}
