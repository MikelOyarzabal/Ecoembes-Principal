package DS_06.Ecoembes.client.data;

import java.util.List;

/**
 * DTO para el resumen de una asignación de contenedores
 */
public class ResumenAsignacion {
    private PlantaReciclaje planta;
    private List<Contenedor> contenedoresAsignados;
    private int totalContenedores;
    private float capacidadTotalAsignada;
    private int envasesEstimados;
    private int capacidadRestantePlanta;
    private int porcentajeOcupacionPlanta;
    private boolean alertaSaturacion;
    
    public ResumenAsignacion() {
    }
    
    public ResumenAsignacion(PlantaReciclaje planta, List<Contenedor> contenedoresAsignados) {
        this.planta = planta;
        this.contenedoresAsignados = contenedoresAsignados;
        this.totalContenedores = contenedoresAsignados.size();
        
        // Calcular capacidad total asignada
        this.capacidadTotalAsignada = 0;
        for (Contenedor c : contenedoresAsignados) {
            this.capacidadTotalAsignada += c.getCapacidadOcupada();
        }
        
        // Estimar envases (1 kg ≈ 50 envases de plástico)
        this.envasesEstimados = (int) (capacidadTotalAsignada * 50);
        
        // Datos de la planta
        this.capacidadRestantePlanta = planta.getCapacidadDisponible();
        this.porcentajeOcupacionPlanta = planta.getPorcentajeOcupacion();
        
        // Determinar si hay alerta de saturación (>75%)
        this.alertaSaturacion = porcentajeOcupacionPlanta >= 75;
    }
    
    // Getters y Setters
    public PlantaReciclaje getPlanta() {
        return planta;
    }
    
    public void setPlanta(PlantaReciclaje planta) {
        this.planta = planta;
    }
    
    public List<Contenedor> getContenedoresAsignados() {
        return contenedoresAsignados;
    }
    
    public void setContenedoresAsignados(List<Contenedor> contenedoresAsignados) {
        this.contenedoresAsignados = contenedoresAsignados;
    }
    
    public int getTotalContenedores() {
        return totalContenedores;
    }
    
    public void setTotalContenedores(int totalContenedores) {
        this.totalContenedores = totalContenedores;
    }
    
    public float getCapacidadTotalAsignada() {
        return capacidadTotalAsignada;
    }
    
    public void setCapacidadTotalAsignada(float capacidadTotalAsignada) {
        this.capacidadTotalAsignada = capacidadTotalAsignada;
    }
    
    public int getEnvasesEstimados() {
        return envasesEstimados;
    }
    
    public void setEnvasesEstimados(int envasesEstimados) {
        this.envasesEstimados = envasesEstimados;
    }
    
    public int getCapacidadRestantePlanta() {
        return capacidadRestantePlanta;
    }
    
    public void setCapacidadRestantePlanta(int capacidadRestantePlanta) {
        this.capacidadRestantePlanta = capacidadRestantePlanta;
    }
    
    public int getPorcentajeOcupacionPlanta() {
        return porcentajeOcupacionPlanta;
    }
    
    public void setPorcentajeOcupacionPlanta(int porcentajeOcupacionPlanta) {
        this.porcentajeOcupacionPlanta = porcentajeOcupacionPlanta;
    }
    
    public boolean isAlertaSaturacion() {
        return alertaSaturacion;
    }
    
    public void setAlertaSaturacion(boolean alertaSaturacion) {
        this.alertaSaturacion = alertaSaturacion;
    }
    
    public String getNivelAlerta() {
        if (porcentajeOcupacionPlanta >= 90) return "CRÍTICO";
        if (porcentajeOcupacionPlanta >= 75) return "ADVERTENCIA";
        return "NORMAL";
    }
}
