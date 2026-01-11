package DS_06.Ecoembes.external;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import DS_06.Ecoembes.entity.Contenedor;

@Service
public class PlasSBGateway implements IPlantaReciclajeGateway {
    
    private final RestTemplate restTemplate;
    
    @Value("${planta.external.plassb.url:http://localhost:8081/api}")
    private String baseUrl;
    
    // Constructor with RestTemplateBuilder
    public PlasSBGateway(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    
    /**
     * Registra una planta en PlasSB durante la inicialización
     */
    public void registrarPlanta(Long id, String nombre, int capacidad) {
        String url = baseUrl + "/plassb/plantas/registrar";
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> requestBody = Map.of(
                "id", id,
                "nombre", nombre,
                "capacidadTotal", capacidad
            );
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Error al registrar planta: " + response.getBody());
            }
            
        } catch (RestClientException e) {
            throw new RuntimeException("Error comunicando con PlasSB: " + e.getMessage(), e);
        }
    }
    
    @Override
    public int consultarCapacidadDisponible(long plantaId) {
        String url = baseUrl + "/plassb/plantas/" + plantaId + "/capacidad";
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object capacidadObj = response.getBody().get("capacidadDisponible");
                if (capacidadObj instanceof Number) {
                    return ((Number) capacidadObj).intValue();
                }
            }
            throw new RuntimeException("Respuesta inválida de PlasSB");
        } catch (RestClientException e) {
            throw new RuntimeException("Error conectando con PlasSB: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean enviarContenedor(long plantaId, Contenedor contenedor) {
        String url = baseUrl + "/plassb/plantas/" + plantaId + "/contenedores";
        try {
            // Crear headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Crear request body
            Map<String, Object> requestBody = Map.of(
                "id", contenedor.getId(),
                "codigoPostal", contenedor.getCodigoPostal(),
                "capacidad", contenedor.getCapacidad(),
                "nivelLlenado", contenedor.getNivelDeLlenado().name(),
                "fechaVaciado", contenedor.getFechaVaciado()
            );
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, request, Void.class);
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            throw new RuntimeException("Error enviando contenedor a PlasSB: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String obtenerEstado(long plantaId) {
        String url = baseUrl + "/plassb/plantas/" + plantaId + "/estado";
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object estadoObj = response.getBody().get("estado");
                return estadoObj != null ? estadoObj.toString() : "DESCONOCIDO";
            }
            return "ERROR_CONSULTA";
        } catch (RestClientException e) {
            return "ERROR_CONEXION";
        }
    }
}