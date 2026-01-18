package DS_06.Ecoembes.client.data;

public record Credentials(String email, String password) {
    public Credentials() {
        this(null, null);
    }
    
    // Métodos getter para compatibilidad con serialización JSON
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
