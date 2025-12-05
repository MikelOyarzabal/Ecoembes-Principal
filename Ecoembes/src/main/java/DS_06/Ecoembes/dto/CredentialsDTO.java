package DS_06.Ecoembes.dto;

import java.util.Objects;

public class CredentialsDTO {
    
    private String email;
    private String password;
    
    // Constructor without parameters
    public CredentialsDTO() {
        super();
    }
    
    // Constructor with parameters
    public CredentialsDTO(String email, String password) {
        super();
        this.email = email;
        this.password = password;
    }
    
    // Getters and setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    // hashCode and equals
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CredentialsDTO other = (CredentialsDTO) obj;
        return Objects.equals(email, other.email);
    }
    
    // toString for debugging
    @Override
    public String toString() {
        return "CredentialsDTO [email=" + email + ", password=" + (password != null ? "[PROTECTED]" : "null") + "]";
    }
}