package com.plassb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "plantas")
public class Planta {
    
    @Id
    private Long id;
    
    private String nombre;
    private int capacidadTotal;
    private int capacidadDisponible;
    
    // Constructores con int (primitivos)
    public Planta() {}
    
    public Planta(String nombre, int capacidadTotal) {
        this.nombre = nombre;
        this.capacidadTotal = capacidadTotal;
        this.capacidadDisponible = capacidadTotal;
    }
    
    public Planta(String nombre, int capacidadTotal, int capacidadDisponible) {
        this.nombre = nombre;
        this.capacidadTotal = capacidadTotal;
        this.capacidadDisponible = capacidadDisponible;
    }
    
    // Getters y Setters con int (primitivos)
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public int getCapacidadTotal() {
        return capacidadTotal;
    }
    
    public void setCapacidadTotal(int capacidadTotal) {
        this.capacidadTotal = capacidadTotal;
    }
    
    public int getCapacidadDisponible() {
        return capacidadDisponible;
    }
    
    public void setCapacidadDisponible(int capacidadDisponible) {
        this.capacidadDisponible = capacidadDisponible;
    }
    
    // Método para reducir capacidad
    public boolean reducirCapacidad(int cantidad) {
        if (capacidadDisponible >= cantidad) {
            capacidadDisponible -= cantidad;
            return true;
        }
        return false;
    }
    
    // Método para aumentar capacidad
    public void aumentarCapacidad(int cantidad) {
        this.capacidadDisponible += cantidad;
        if (this.capacidadDisponible > this.capacidadTotal) {
            this.capacidadDisponible = this.capacidadTotal;
        }
    }
}