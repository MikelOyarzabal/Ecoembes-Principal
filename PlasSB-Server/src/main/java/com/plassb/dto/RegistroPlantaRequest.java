package com.plassb.dto;

public class RegistroPlantaRequest {
    private Long id;
    private String nombre;
    private int capacidadTotal;
    
    // Constructores
    public RegistroPlantaRequest() {
    }
    
    public RegistroPlantaRequest(Long id, String nombre, int capacidadTotal) {
        this.id = id;
        this.nombre = nombre;
        this.capacidadTotal = capacidadTotal;
    }
    
    // Getters y Setters
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
    
    @Override
    public String toString() {
        return "RegistroPlantaRequest{id=" + id + ", nombre='" + nombre + "', capacidadTotal=" + capacidadTotal + "}";
    }
}