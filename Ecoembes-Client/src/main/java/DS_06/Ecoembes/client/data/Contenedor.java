package DS_06.Ecoembes.client.data;

import java.util.Date;

public class Contenedor {
    private long id;
    private int codigoPostal;
    private float capacidad;
    private String nivelDeLlenado;
    private Date fechaVaciado;
    
    public Contenedor() {
    }
    
    public Contenedor(long id, int codigoPostal, float capacidad, String nivelDeLlenado, Date fechaVaciado) {
        this.id = id;
        this.codigoPostal = codigoPostal;
        this.capacidad = capacidad;
        this.nivelDeLlenado = nivelDeLlenado;
        this.fechaVaciado = fechaVaciado;
    }
    
    // Getters y Setters
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
    
    public String getNivelDeLlenado() {
        return nivelDeLlenado;
    }
    
    public void setNivelDeLlenado(String nivelDeLlenado) {
        this.nivelDeLlenado = nivelDeLlenado;
    }
    
    public Date getFechaVaciado() {
        return fechaVaciado;
    }
    
    public void setFechaVaciado(Date fechaVaciado) {
        this.fechaVaciado = fechaVaciado;
    }
}
