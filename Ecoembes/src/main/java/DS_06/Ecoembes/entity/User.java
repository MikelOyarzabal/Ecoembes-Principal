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
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "userAsignacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Contenedor> contenedoresAsignados = new ArrayList<>();

    // Constructor without parameters
    public User() { }
    
    // Constructor with parameters
    public User(String nickname, String email, String password) {
        this.nickname = nickname;		
        this.email = email;
        this.password = password;
    }
    
    // Check if a password is correct
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    // Add a container to the user's assigned containers
    public void agregarContenedor(Contenedor contenedor) {
        if (contenedor != null) {
            if (this.contenedoresAsignados == null) {
                this.contenedoresAsignados = new ArrayList<>();
            }
            contenedor.setUserAsignacion(this);
            this.contenedoresAsignados.add(contenedor);
        }
    }

    // Remove a container from the user's assigned containers
    public void eliminarContenedor(Contenedor contenedor) {
        if (contenedor != null && this.contenedoresAsignados != null) {
            this.contenedoresAsignados.remove(contenedor);
            contenedor.setUserAsignacion(null);
        }
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Contenedor> getContenedoresAsignados() {
        return contenedoresAsignados;
    }

    public void setContenedoresAsignados(List<Contenedor> contenedoresAsignados) {
        // Clear existing containers
        if (this.contenedoresAsignados != null) {
            for (Contenedor contenedor : this.contenedoresAsignados) {
                contenedor.setUserAsignacion(null);
            }
            this.contenedoresAsignados.clear();
        } else {
            this.contenedoresAsignados = new ArrayList<>();
        }
        
        // Add new containers
        if (contenedoresAsignados != null) {
            for (Contenedor contenedor : contenedoresAsignados) {
                agregarContenedor(contenedor);
            }
        }
    }

    // Get the number of assigned containers
    public int getNumeroContenedoresAsignados() {
        return contenedoresAsignados != null ? contenedoresAsignados.size() : 0;
    }

    // Get the number of containers that need emptying
    public int getContenedoresNecesitanVaciado() {
        int count = 0;
        if (contenedoresAsignados != null) {
            for (Contenedor contenedor : contenedoresAsignados) {
                if (contenedor.necesitaVaciado()) {
                    count++;
                }
            }
        }
        return count;
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
        User other = (User) obj;
        return id == other.id;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "User [id=" + id + 
               ", nickname=" + nickname + 
               ", email=" + email + 
               ", contenedoresAsignados=" + getNumeroContenedoresAsignados() + 
               "]";
    }
}