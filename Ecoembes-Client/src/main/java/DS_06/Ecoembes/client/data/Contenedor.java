package DS_06.Ecoembes.client.data;

import java.util.Date;

public record Contenedor(
    long id,
    int codigoPostal,
    float capacidad,
    String nivelDeLlenado,
    Date fechaVaciado
) {
    public Contenedor() {
        this(0, 0, 0, "VERDE", null);
    }
    
    /**
     * Calcula el porcentaje de ocupación basado en el nivel de llenado
     */
    public int getPorcentajeOcupacion() {
        if (nivelDeLlenado == null) return 0;
        return switch (nivelDeLlenado.toUpperCase()) {
            case "VERDE" -> 25;
            case "AMARILLO" -> 50;
            case "ROJO" -> 75;
            case "LLENO" -> 100;
            default -> 0;
        };
    }
    
    /**
     * Calcula la capacidad ocupada en kg
     */
    public float getCapacidadOcupada() {
        return capacidad * getPorcentajeOcupacion() / 100.0f;
    }
    
    // Métodos getter para compatibilidad con Thymeleaf
    public long getId() { return id; }
    public int getCodigoPostal() { return codigoPostal; }
    public float getCapacidad() { return capacidad; }
    public String getNivelDeLlenado() { return nivelDeLlenado; }
    public Date getFechaVaciado() { return fechaVaciado; }
}
