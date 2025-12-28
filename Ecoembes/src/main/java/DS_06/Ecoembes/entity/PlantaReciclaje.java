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
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
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

    @Column(name = "tipo_planta")
    private String tipoPlanta; // "PLASSB" o "CONTSOCKET"

    @OneToMany(mappedBy = "plantaReciclaje", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Contenedor> contenedores = new ArrayList<>();  // CAMBIADO: Usa 'contenedores' consistentemente

    // Constructor sin parámetros
    public PlantaReciclaje() {
        super();
        this.contenedores = new ArrayList<>();  // CAMBIADO: Inicializa 'contenedores'
    }

    // Constructor con parámetros (sin id)
    public PlantaReciclaje(String nombre, int capacidad, List<Contenedor> contenedores) {
        super();
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.contenedores = contenedores != null ? contenedores : new ArrayList<>();  // CAMBIADO
        determinarTipoPorNombre();
        this.calcularCapacidades();
    }

    // Calcular capacidad disponible basada en contenedores
    public void calcularCapacidades() {
        int capacidadOcupada = 0;
        
        if (this.contenedores != null && !this.contenedores.isEmpty()) {
            for (Contenedor contenedor : this.contenedores) {
                capacidadOcupada += contenedor.getOcupado();
            }
        }
        
        this.capacidadDisponible = this.capacidad - capacidadOcupada;
    }
    
    // Determinar tipo de planta por nombre
    @PostLoad
    @PostPersist
    @PostUpdate
    public void determinarTipoPorNombre() {
        if (this.nombre == null) {
            this.tipoPlanta = "DESCONOCIDO";
            return;
        }
        
        String nombreUpper = this.nombre.toUpperCase();
        if (nombreUpper.contains("PLASSB")) {
            this.tipoPlanta = "PLASSB";
        } else if (nombreUpper.contains("CONTSOCKET")) {
            this.tipoPlanta = "CONTSOCKET";
        } else {
            this.tipoPlanta = "DESCONOCIDO";
        }
    }
    
    // Obtener capacidad disponible (calculada, no persistida)
    public int getCapacidadDisponible() {
        calcularCapacidades();
        // Asegurar que nunca retorne un valor negativo
        return Math.max(0, capacidadDisponible);
    }

    // Añadir un contenedor a la lista
    public void agregarContenedor(Contenedor contenedor) {
        if (contenedor != null) {
            if (this.contenedores == null) {  // CAMBIADO
                this.contenedores = new ArrayList<>();
            }
            contenedor.setPlantaReciclaje(this);
            this.contenedores.add(contenedor);  // CAMBIADO
            this.calcularCapacidades();
        }
    }

    // Eliminar un contenedor de la lista
    public void eliminarContenedor(Contenedor contenedor) {
        if (contenedor != null && this.contenedores != null) {  // CAMBIADO
            this.contenedores.remove(contenedor);  // CAMBIADO
            contenedor.setPlantaReciclaje(null);
            this.calcularCapacidades();
        }
    }

    // Getters y setters
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
        determinarTipoPorNombre();
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
        this.calcularCapacidades();
    }

    public String getTipoPlanta() {
        return tipoPlanta;
    }

    public void setTipoPlanta(String tipoPlanta) {
        this.tipoPlanta = tipoPlanta;
    }

    public List<Contenedor> getContenedores() {  // CAMBIADO: getContenedores
        return contenedores;
    }

    public void setContenedores(List<Contenedor> contenedores) {  // CAMBIADO: setContenedores
        // Limpiar contenedores existentes
        if (this.contenedores != null) {
            for (Contenedor contenedor : this.contenedores) {
                contenedor.setPlantaReciclaje(null);
            }
            this.contenedores.clear();
        } else {
            this.contenedores = new ArrayList<>();
        }
        
        // Añadir nuevos contenedores
        if (contenedores != null) {
            for (Contenedor contenedor : contenedores) {
                agregarContenedor(contenedor);
            }
        }
        this.calcularCapacidades();
    }

    // hashCode y equals
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
    
    // toString para depuración
    @Override
    public String toString() {
        return "PlantaReciclaje [id=" + id + 
               ", nombre=" + nombre + 
               ", tipoPlanta=" + tipoPlanta +
               ", capacidad=" + capacidad + 
               ", capacidadDisponible=" + getCapacidadDisponible() + 
               ", contenedores=" + (contenedores != null ? contenedores.size() : 0) + "]";
    }
    
    // Campo transiente para capacidad calculada
    @Transient
    private int capacidadDisponible;
}