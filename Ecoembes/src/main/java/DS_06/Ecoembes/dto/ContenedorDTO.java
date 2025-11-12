package DS_06.Ecoembes.dto;

import DS_06.Ecoembes.entity.Llenado;

public class ContenedorDTO {
	private long id;
	private int codigoPostal;
	private float capacidad;
	private Llenado nivelDeLlenado;
	private long fechaVaciado;
	public ContenedorDTO() {
		super();
	}
	public ContenedorDTO(long id, int codigoPostal, float capacidad, Llenado nivelDeLlenado, long fechaVaciado) {
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
	public long getFechaVaciado() {
		return fechaVaciado;
	}
	public void setFechaVaciado(long fechaVaciado) {
		this.fechaVaciado = fechaVaciado;
	}
}
