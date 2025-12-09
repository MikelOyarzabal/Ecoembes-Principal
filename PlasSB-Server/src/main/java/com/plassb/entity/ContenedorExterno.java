package com.plassb.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ContenedorExterno {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long idExterno; // ID del contenedor en el sistema Ecoembes
    private int codigoPostal;
    private float capacidad;
    private String nivelLlenado;
    private Date fechaVaciado;
    private Date fechaRecepcion;
    
    @ManyToOne
    private Planta planta;
    
    // Constructor, getters y setters
    public ContenedorExterno() {}
    
    public ContenedorExterno(Long idExterno, int codigoPostal, float capacidad, String nivelLlenado, Date fechaVaciado) {
        this.idExterno = idExterno;
        this.codigoPostal = codigoPostal;
        this.capacidad = capacidad;
        this.nivelLlenado = nivelLlenado;
        this.fechaVaciado = fechaVaciado;
        this.fechaRecepcion = new Date();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getIdExterno() {
        return idExterno;
    }
    
    public void setIdExterno(Long idExterno) {
        this.idExterno = idExterno;
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
    
    public String getNivelLlenado() {
        return nivelLlenado;
    }
    
    public void setNivelLlenado(String nivelLlenado) {
        this.nivelLlenado = nivelLlenado;
    }
    
    public Date getFechaVaciado() {
        return fechaVaciado;
    }
    
    public void setFechaVaciado(Date fechaVaciado) {
        this.fechaVaciado = fechaVaciado;
    }
    
    public Date getFechaRecepcion() {
        return fechaRecepcion;
    }
    
    public void setFechaRecepcion(Date fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }
    
    public Planta getPlanta() {
        return planta;
    }
    
    public void setPlanta(Planta planta) {
        this.planta = planta;
    }
}