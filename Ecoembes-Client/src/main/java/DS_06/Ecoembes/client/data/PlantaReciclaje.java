package DS_06.Ecoembes.client.data;

import java.util.ArrayList;
import java.util.List;

public class PlantaReciclaje {
    private long id;
    private String nombre;
    private int capacidad;
    private int capacidadDisponible;
    private List<Contenedor> listaContenedor;
    
    public PlantaReciclaje() {
        this.listaContenedor = new ArrayList<>();
    }
    
    public PlantaReciclaje(long id, String nombre, int capacidad, int capacidadDisponible, List<Contenedor> listaContenedor) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.capacidadDisponible = capacidadDisponible;
        this.listaContenedor = listaContenedor != null ? listaContenedor : new ArrayList<>();
    }
    
    // Getters y Setters
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
    
    public List<Contenedor> getListaContenedor() {
        return listaContenedor;
    }
    
    public void setListaContenedor(List<Contenedor> listaContenedor) {
        this.listaContenedor = listaContenedor;
    }
    
    public int getPorcentajeOcupacion() {
        if (capacidad == 0) return 0;
        int ocupado = capacidad - capacidadDisponible;
        return (ocupado * 100) / capacidad;
    }
    
    /**
     * Verifica si la planta está saturada (>= umbral%)
     */
    public boolean estaSaturada(int umbral) {
        return getPorcentajeOcupacion() >= umbral;
    }
}
