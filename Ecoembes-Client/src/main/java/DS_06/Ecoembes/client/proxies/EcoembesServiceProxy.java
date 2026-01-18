package DS_06.Ecoembes.client.proxies;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    
    // DTO interno para deserialización JSON de Contenedor
    private static class ContenedorDTO {
        public long id;
        public int codigoPostal;
        public float capacidad;
        public String nivelDeLlenado;
        public Date fechaVaciado;
        
        public Contenedor toRecord() {
            return new Contenedor(id, codigoPostal, capacidad, nivelDeLlenado, fechaVaciado);
        }
    }
    
    // DTO interno para deserialización JSON de PlantaReciclaje
    private static class PlantaReciclajeDTO {
        public long id;
        public String nombre;
        public int capacidad;
        public int capacidadDisponible;
        public List<ContenedorDTO> listaContenedor;
        
        public PlantaReciclaje toRecord() {
            List<Contenedor> contenedores = new ArrayList<>();
            if (listaContenedor != null) {
                for (ContenedorDTO c : listaContenedor) {
                    contenedores.add(c.toRecord());
                }
            }
            return new PlantaReciclaje(id, nombre, capacidad, capacidadDisponible, contenedores);
        }
    }
    
    // DTO para enviar credenciales
    private static class CredentialsDTO {
        public String email;
        public String password;
        
        public CredentialsDTO(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
    
    @Override
    public String login(String email, String password) {
        try {
            String url = serverUrl + "/auth/login";
            
            CredentialsDTO credentials = new CredentialsDTO(email, password);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<CredentialsDTO> request = new HttpEntity<>(credentials, headers);
            
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
            
            ResponseEntity<ContenedorDTO[]> response = restTemplate.getForEntity(url, ContenedorDTO[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Contenedor> result = new ArrayList<>();
                for (ContenedorDTO dto : response.getBody()) {
                    result.add(dto.toRecord());
                }
                return result;
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
                return new Contenedor(response.getBody(), codigoPostal, capacidad, "VERDE", null);
            }
            return null;
        } catch (RestClientException e) {
            System.err.println("Error creando contenedor: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public Contenedor actualizarContenedor(String token, long contenedorId, int codigoPostal, 
                                           float capacidad, String nivelLlenado) {
        try {
            String url = serverUrl + "/reciclaje/contenedores/" + contenedorId + 
                        "?token=" + token + 
                        "&codigoPostal=" + codigoPostal + 
                        "&capacidad=" + capacidad +
                        "&nivelLlenado=" + nivelLlenado;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<ContenedorDTO> response = restTemplate.exchange(
                url, HttpMethod.PUT, request, ContenedorDTO.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().toRecord();
            }
            
            // Si el servidor no tiene el endpoint PUT, simular la respuesta
            return new Contenedor(contenedorId, codigoPostal, capacidad, nivelLlenado, null);
            
        } catch (RestClientException e) {
            System.err.println("Error actualizando contenedor: " + e.getMessage());
            System.out.println("Nota: Actualización simulada localmente. " +
                             "Para actualización real, usar Swagger/Postman en el servidor.");
            return new Contenedor(contenedorId, codigoPostal, capacidad, nivelLlenado, null);
        }
    }
    
    @Override
    public Contenedor getContenedorById(String token, long contenedorId) {
        try {
            List<Contenedor> todos = getAllContenedores(token);
            return todos.stream()
                       .filter(c -> c.id() == contenedorId)
                       .findFirst()
                       .orElse(null);
        } catch (Exception e) {
            System.err.println("Error obteniendo contenedor: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public List<Contenedor> getContenedoresPorZona(String token, Date fecha, int codigoPostal) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaStr = sdf.format(fecha);
            
            String url = serverUrl + "/reciclaje/contenedores/zona?token=" + token + 
                        "&date=" + fechaStr + "&codigoPostal=" + codigoPostal;
            
            System.out.println("===== DEBUG CLIENTE =====");
            System.out.println("URL completa: " + url);
            System.out.println("Fecha original: " + fecha);
            System.out.println("Fecha formateada: " + fechaStr);
            System.out.println("========================");
            
            ResponseEntity<ContenedorDTO[]> response = restTemplate.getForEntity(url, ContenedorDTO[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Contenedor> result = new ArrayList<>();
                for (ContenedorDTO dto : response.getBody()) {
                    result.add(dto.toRecord());
                }
                return result;
            }
            return List.of();
        } catch (RestClientException e) {
            System.err.println("Error obteniendo contenedores por zona: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public List<PlantaReciclaje> getAllPlantas(String token) {
        try {
            String url = serverUrl + "/reciclaje/plantasreciclaje?token=" + token;
            
            ResponseEntity<PlantaReciclajeDTO[]> response = restTemplate.getForEntity(url, PlantaReciclajeDTO[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<PlantaReciclaje> result = new ArrayList<>();
                for (PlantaReciclajeDTO dto : response.getBody()) {
                    result.add(dto.toRecord());
                }
                return result;
            }
            return List.of();
        } catch (RestClientException e) {
            System.err.println("Error obteniendo plantas: " + e.getMessage());
            return List.of();
        }
    }
    
    @Override
    public PlantaReciclaje getPlantaById(String token, long plantaId) {
        try {
            List<PlantaReciclaje> todas = getAllPlantas(token);
            return todas.stream()
                       .filter(p -> p.id() == plantaId)
                       .findFirst()
                       .orElse(null);
        } catch (Exception e) {
            System.err.println("Error obteniendo planta: " + e.getMessage());
            return null;
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
