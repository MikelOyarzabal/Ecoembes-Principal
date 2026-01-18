package DS_06.Ecoembes.client.data;

import java.util.ArrayList;
import java.util.List;

public record PlantaReciclaje(
    long id,
    String nombre,
    int capacidad,
    int capacidadDisponible,
    List<Contenedor> listaContenedor) {
    public PlantaReciclaje() {
        this(0, null, 0, 0, new ArrayList<>());
    }
    
    public PlantaReciclaje(long id, String nombre, int capacidad, int capacidadDisponible) {
        this(id, nombre, capacidad, capacidadDisponible, new ArrayList<>());
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
    
    // Métodos getter para compatibilidad con Thymeleaf
    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public int getCapacidad() { return capacidad; }
    public int getCapacidadDisponible() { return capacidadDisponible; }
    public List<Contenedor> getListaContenedor() { return listaContenedor; }
}
