package DS_06.Ecoembes.entity;

import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity
public class Contenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "codigo_postal", nullable = false)
    private int codigoPostal;

    @Column(nullable = false)
    private float capacidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_llenado")
    private Llenado nivelDeLlenado;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_vaciado")
    private Date fechaVaciado;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_asignacion")
    private Date fechaAsignacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userAsignacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planta_reciclaje_id")
    private PlantaReciclaje plantaReciclaje;

    // Constructor without parameters
    public Contenedor() {
        this.nivelDeLlenado = Llenado.VERDE;
    }

    // Constructor with parameters
    // Note: id is auto-generated, so it should not be included in the constructor
    public Contenedor(int codigoPostal, float capacidad, Llenado nivelDeLlenado, Date fechaVaciado) {
        this.codigoPostal = codigoPostal;
        this.capacidad = capacidad;
        this.nivelDeLlenado = nivelDeLlenado;
        this.fechaVaciado = fechaVaciado;
        if (this.nivelDeLlenado == null) {
            this.nivelDeLlenado = Llenado.VERDE;
        }
    }

    // Get the current ocupation percentage of the container
    @Transient
    public int getOcupado() {
        if (nivelDeLlenado == null || capacidad == 0) {
            return 0;
        }
        return (int) (this.getCapacidad() * (100 - nivelDeLlenado.getValor()) / 100.0f);
    }

    // Get the current occupancy percentage (opposite of ocupado)
    @Transient
    public int getPorcentajeOcupacion() {
        if (nivelDeLlenado == null) {
            return 0;
        }
        return nivelDeLlenado.getValor();
    }

    // Check if container needs emptying based on fill level
    @Transient
    public boolean necesitaVaciado() {
        return nivelDeLlenado == Llenado.ROJO || nivelDeLlenado == Llenado.LLENO;
    }

    // Getters and setters
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

    public Llenado getNivelDeLlenado() {
        return nivelDeLlenado;
    }

    public void setNivelDeLlenado(Llenado nivelDeLlenado) {
        this.nivelDeLlenado = nivelDeLlenado;
    }

    public Date getFechaVaciado() {
        return fechaVaciado;
    }

    public void setFechaVaciado(Date fechaVaciado) {
        this.fechaVaciado = fechaVaciado;
    }

    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public User getUserAsignacion() {
        return userAsignacion;
    }

    public void setUserAsignacion(User userAsignacion) {
        this.userAsignacion = userAsignacion;
    }

    public PlantaReciclaje getPlantaReciclaje() {
        return plantaReciclaje;
    }

    public void setPlantaReciclaje(PlantaReciclaje plantaReciclaje) {
        this.plantaReciclaje = plantaReciclaje;
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
        Contenedor other = (Contenedor) obj;
        return id == other.id;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Contenedor [id=" + id + 
               ", codigoPostal=" + codigoPostal + 
               ", capacidad=" + capacidad + 
               ", nivelDeLlenado=" + nivelDeLlenado + 
               ", ocupado=" + getOcupado() + "%" + 
               ", plantaReciclaje=" + (plantaReciclaje != null ? plantaReciclaje.getNombre() : "No asignada") + 
               ", userAsignacion=" + (userAsignacion != null ? userAsignacion.getNickname() : "No asignado") + 
               "]";
    }
}