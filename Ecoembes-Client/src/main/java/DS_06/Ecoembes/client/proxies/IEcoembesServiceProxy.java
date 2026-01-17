package DS_06.Ecoembes.client.proxies;

import java.util.Date;
import java.util.List;

import DS_06.Ecoembes.client.data.Contenedor;
import DS_06.Ecoembes.client.data.PlantaReciclaje;

public interface IEcoembesServiceProxy {
    
    // Autenticación
    String login(String email, String password);
    boolean logout(String token);
    
    // Contenedores
    List<Contenedor> getAllContenedores(String token);
    Contenedor crearContenedor(String token, int codigoPostal, float capacidad);
    List<Contenedor> getContenedoresPorZona(String token, Date fecha, int codigoPostal);
    
    // Plantas de Reciclaje
    List<PlantaReciclaje> getAllPlantas(String token);
    Integer getCapacidadPlanta(String token, long plantaId, Date fecha);
    
    // Asignaciones
    boolean asignarContenedorAPlanta(String token, long contenedorId, long plantaId);
    boolean asignarContenedoresAPlanta(String token, List<Long> contenedorIds, long plantaId);
}
