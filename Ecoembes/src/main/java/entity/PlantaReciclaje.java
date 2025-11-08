package entity;

import java.util.ArrayList;

public class PlantaReciclaje {
	private long id;
	private String nombre;
	private int capacidad;
	private int capacidadDisponible;
	private ArrayList<Contenedor> listaContenedor;
	public PlantaReciclaje() {
		super();
	}
	public PlantaReciclaje(long id, String nombre, int capacidadDisponible) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.setCapacidad();
		this.capacidadDisponible = capacidadDisponible;
		this.listaContenedor=new ArrayList<Contenedor>();
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
	public void setCapacidad() {
		for(Contenedor c: listaContenedor) {
			this.capacidad += c.getCapacidad();
		}
	}
	public int getCapacidadDisponible() {
		return capacidadDisponible;
	}
	public void setCapacidadDisponible(int capacidadDisponible) {
		this.capacidadDisponible = capacidadDisponible;
	}
	

}
