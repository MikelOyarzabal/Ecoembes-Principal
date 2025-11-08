package dto;

import java.util.Date;

import entity.Llenado;

public class ContenedorDTO {
	private long id;
	private int codigoPostal;
	private float capacidad;
	private Llenado nivelDeLlenado;
	private Date fechaVaciado;
	public ContenedorDTO() {
		super();
	}
	public ContenedorDTO(long id, int codigoPostal, float capacidad, Llenado nivelDeLlenado, Date fechaVaciado) {
		super();
		this.id = id;
		this.codigoPostal = codigoPostal;
		this.capacidad = capacidad;
		this.nivelDeLlenado = nivelDeLlenado;
		this.fechaVaciado = fechaVaciado;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getCodigoPostal() {
		return codigoPostal;
	}
	public void setCodigoPostal(int codigoPostal) {
		this.codigoPostal = codigoPostal;
	}
	public float getCapacidad() {
		return capacidad;
	}
	public void setCapacidad(float capacidad) {
		this.capacidad = capacidad;
	}
	public Llenado getNivelDeLlenado() {
		return nivelDeLlenado;
	}
	public void setNivelDeLlenado(Llenado nivelDeLlenado) {
		this.nivelDeLlenado = nivelDeLlenado;
	}
	public Date getFechaVaciado() {
		return fechaVaciado;
	}
	public void setFechaVaciado(Date fechaVaciado) {
		this.fechaVaciado = fechaVaciado;
	}
}
