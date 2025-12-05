package DS_06.Ecoembes.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlantaReciclajeDTO {
    private long id;
    private String nombre;
    private int capacidad;
    private int capacidadDisponible;
    private List<ContenedorDTO> listaContenedor;
    
    // Constructor without parameters
    public PlantaReciclajeDTO() {
        super();
        this.listaContenedor = new ArrayList<>();
        this.capacidadDisponible = 0;
    }
    
    // Constructor with parameters
    public PlantaReciclajeDTO(long id, String nombre, int capacidad, List<ContenedorDTO> listaContenedor) {
        super();
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.listaContenedor = listaContenedor != null ? listaContenedor : new ArrayList<>();
    }
    
    // Constructor with all parameters including capacidadDisponible
    public PlantaReciclajeDTO(long id, String nombre, int capacidad, int capacidadDisponible, List<ContenedorDTO> listaContenedor) {
        super();
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.capacidadDisponible = capacidadDisponible;
        this.listaContenedor = listaContenedor != null ? listaContenedor : new ArrayList<>();
    }
    
    // Getters and setters
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
    
    public List<ContenedorDTO> getListaContenedor() {
        return listaContenedor;
    }
    
    public void setListaContenedor(List<ContenedorDTO> listaContenedor) {
        this.listaContenedor = listaContenedor != null ? listaContenedor : new ArrayList<>();
    }
    
    // hashCode and equals
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlantaReciclajeDTO other = (PlantaReciclajeDTO) obj;
        return id == other.id;
    }
    
    // toString for debugging
    @Override
    public String toString() {
        return "PlantaReciclajeDTO [id=" + id + 
               ", nombre=" + nombre + 
               ", capacidad=" + capacidad + 
               ", capacidadDisponible=" + capacidadDisponible + 
               ", numeroContenedores=" + (listaContenedor != null ? listaContenedor.size() : 0) + 
               "]";
    }
}