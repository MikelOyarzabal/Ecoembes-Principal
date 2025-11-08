package dto;

import java.util.ArrayList;

import entity.Contenedor;

public class PlantaReciclajeDTO {
	private long id;
	private String nombre;
	private int capacidad;
	private int capacidadDisponible;
	private ArrayList<Contenedor> listaContenedor;

	public PlantaReciclajeDTO() {
		super();
	}
	public PlantaReciclajeDTO(long id, String nombre, int capacidad, int capacidadDisponible) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.capacidad = capacidad;
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
