package DS_06.Ecoembes.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

@Entity
public class PlantaReciclaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private int capacidad;

    @OneToMany(mappedBy = "plantaReciclaje", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Contenedor> listaContenedor = new ArrayList<>();

    // Constructor without parameters
    public PlantaReciclaje() {
        super();
        this.listaContenedor = new ArrayList<>();
    }

    // Constructor with parameters
    // Note: id is auto-generated, so it should not be included in the constructor
    public PlantaReciclaje(String nombre, int capacidad, List<Contenedor> listaContenedor) {
        super();
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.listaContenedor = listaContenedor != null ? listaContenedor : new ArrayList<>();
        this.calcularCapacidades();
    }

    // Calculate available capacity based on containers
    public void calcularCapacidades() {
        int capacidadOcupada = 0;
        
        if (this.listaContenedor != null && !this.listaContenedor.isEmpty()) {
            for (Contenedor contenedor : this.listaContenedor) {
                // Occupied capacity: based on fill level
                float factorOcupacion = calcularFactorOcupacion(contenedor.getNivelDeLlenado());
                capacidadOcupada += contenedor.getCapacidad() * factorOcupacion;
            }
        }
        
        this.capacidadDisponible = this.capacidad - capacidadOcupada;
    }

    // Helper method to calculate occupancy factor based on fill level
    private float calcularFactorOcupacion(Llenado nivelLlenado) {
        if (nivelLlenado == null) return 0.0f;
        
        // Use enum values: VERDE=25, AMARILLO=50, ROJO=75, LLENO=100
        // Occupancy factor is: valor_llenado / 100
        return nivelLlenado.getValor() / 100.0f;
    }
    
    // Get the available capacity (calculated, not persisted)
    public int getCapacidadDisponible() {
        calcularCapacidades();
        return capacidadDisponible;
    }

    // Add a container to the list
    public void agregarContenedor(Contenedor contenedor) {
        if (contenedor != null) {
            if (this.listaContenedor == null) {
                this.listaContenedor = new ArrayList<>();
            }
            contenedor.setPlantaReciclaje(this); // Set the bidirectional relationship
            this.listaContenedor.add(contenedor);
            this.calcularCapacidades();
        }
    }

    // Remove a container from the list
    public void eliminarContenedor(Contenedor contenedor) {
        if (contenedor != null && this.listaContenedor != null) {
            this.listaContenedor.remove(contenedor);
            contenedor.setPlantaReciclaje(null); // Clear the bidirectional relationship
            this.calcularCapacidades();
        }
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
        this.calcularCapacidades();
    }

    public List<Contenedor> getListaContenedor() {
        return listaContenedor;
    }

    public void setListaContenedor(List<Contenedor> listaContenedor) {
        // Clear existing containers
        if (this.listaContenedor != null) {
            for (Contenedor contenedor : this.listaContenedor) {
                contenedor.setPlantaReciclaje(null);
            }
            this.listaContenedor.clear();
        } else {
            this.listaContenedor = new ArrayList<>();
        }
        
        // Add new containers
        if (listaContenedor != null) {
            for (Contenedor contenedor : listaContenedor) {
                agregarContenedor(contenedor);
            }
        }
        this.calcularCapacidades();
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
        PlantaReciclaje other = (PlantaReciclaje) obj;
        return id == other.id;
    }
    
    // toString method for debugging
    @Override
    public String toString() {
        return "PlantaReciclaje [id=" + id + ", nombre=" + nombre + ", capacidad=" + capacidad + 
               ", capacidadDisponible=" + getCapacidadDisponible() + ", contenedores=" + 
               (listaContenedor != null ? listaContenedor.size() : 0) + "]";
    }
    
    // Transient field for calculated capacity
    @Transient
    private int capacidadDisponible;
}