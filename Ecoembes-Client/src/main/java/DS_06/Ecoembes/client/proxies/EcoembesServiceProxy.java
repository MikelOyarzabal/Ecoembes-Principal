package DS_06.Ecoembes.client.proxies;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import DS_06.Ecoembes.client.data.Contenedor;
import DS_06.Ecoembes.client.data.Credentials;
import DS_06.Ecoembes.client.data.PlantaReciclaje;

@Service
public class EcoembesServiceProxy implements IEcoembesServiceProxy {
    
    private final RestTemplate restTemplate;
    
    @Value("${ecoembes.server.url:http://localhost:8080}")
    private String serverUrl;
    
    public EcoembesServiceProxy(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    
    @Override
    public String login(String email, String password) {
        try {
            String url = serverUrl + "/auth/login";
            
            Credentials credentials = new Credentials(email, password);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Credentials> request = new HttpEntity<>(credentials, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
            return null;
        } catch (RestClientException e) {
            System.err.println("Error en login: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public boolean logout(String token) {
        try {
            String url = serverUrl + "/auth/logout";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            
            HttpEntity<String> request = new HttpEntity<>(token, headers);
            
            ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            System.err.println("Error en logout: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<Contenedor> getAllContenedores(String token) {
        try {
            String url = serverUrl + "/reciclaje/contenedores?token=" + token;
            
            ResponseEntity<Contenedor[]> response = restTemplate.getForEntity(url, Contenedor[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            }
            return List.of();
        } catch (RestClientException e) {
            System.err.println("Error obteniendo contenedores: " + e.getMessage());
            return List.of();
        }
    }
    
    @Override
    public Contenedor crearContenedor(String token, int codigoPostal, float capacidad) {
        try {
            String url = serverUrl + "/reciclaje/contenedores?token=" + token + 
                        "&codigoPostal=" + codigoPostal + "&capacidad=" + capacidad;
            
            ResponseEntity<Long> response = restTemplate.postForEntity(url, null, Long.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Crear objeto contenedor con el ID devuelto
                Contenedor nuevo = new Contenedor();
                nuevo.setId(response.getBody());
                nuevo.setCodigoPostal(codigoPostal);
                nuevo.setCapacidad(capacidad);
                nuevo.setNivelDeLlenado("VERDE");
                return nuevo;
            }
            return null;
        } catch (RestClientException e) {
            System.err.println("Error creando contenedor: " + e.getMessage());
            return null;
        }
    }
    
 // MÉTODO CON DEBUG - Reemplaza getContenedoresPorZona en EcoembesServiceProxy.java

    @Override
    public List<Contenedor> getContenedoresPorZona(String token, Date fecha, int codigoPostal) {
        try {
            // Intentar con formato más simple
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaStr = sdf.format(fecha);
            
            String url = serverUrl + "/reciclaje/contenedores/zona?token=" + token + 
                        "&date=" + fechaStr + "&codigoPostal=" + codigoPostal;
            
            // DEBUG: Ver qué URL se está llamando
            System.out.println("===== DEBUG CLIENTE =====");
            System.out.println("URL completa: " + url);
            System.out.println("Fecha original: " + fecha);
            System.out.println("Fecha formateada: " + fechaStr);
            System.out.println("========================");
            
            ResponseEntity<Contenedor[]> response = restTemplate.getForEntity(url, Contenedor[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            }
            return List.of();
        } catch (RestClientException e) {
            System.err.println("Error obteniendo contenedores por zona: " + e.getMessage());
            e.printStackTrace(); // ← Ver el stack trace completo
            return List.of();
        }
    }
    
    @Override
    public List<PlantaReciclaje> getAllPlantas(String token) {
        try {
            String url = serverUrl + "/reciclaje/plantasreciclaje?token=" + token;
            
            ResponseEntity<PlantaReciclaje[]> response = restTemplate.getForEntity(url, PlantaReciclaje[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            }
            return List.of();
        } catch (RestClientException e) {
            System.err.println("Error obteniendo plantas: " + e.getMessage());
            return List.of();
        }
    }
    
    @Override
    public Integer getCapacidadPlanta(String token, long plantaId, Date fecha) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaParam = fecha != null ? "&fecha=" + sdf.format(fecha) : "";
            
            String url = serverUrl + "/reciclaje/plantasreciclaje/" + plantaId + 
                        "/capacidad?token=" + token + fechaParam;
            
            ResponseEntity<Integer> response = restTemplate.getForEntity(url, Integer.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
            return null;
        } catch (RestClientException e) {
            System.err.println("Error obteniendo capacidad de planta: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public boolean asignarContenedorAPlanta(String token, long contenedorId, long plantaId) {
        try {
            String url = serverUrl + "/reciclaje/plantasreciclaje/" + plantaId + 
                        "/contenedor/" + contenedorId + "?token=" + token;
            
            ResponseEntity<Void> response = restTemplate.postForEntity(url, null, Void.class);
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            System.err.println("Error asignando contenedor: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean asignarContenedoresAPlanta(String token, List<Long> contenedorIds, long plantaId) {
        try {
            String url = serverUrl + "/reciclaje/plantasreciclaje/" + plantaId + 
                        "/contenedores?token=" + token;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<List<Long>> request = new HttpEntity<>(contenedorIds, headers);
            
            ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            System.err.println("Error asignando contenedores: " + e.getMessage());
            return false;
        }
    }
}
