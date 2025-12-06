/**
 * This code is based on solutions provided by ChatGPT 4o and 
 * adapted using GitHub Copilot. It has been thoroughly reviewed 
 * and validated to ensure correctness and that it is free of errors.
 */
package DS_06.Ecoembes.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import DS_06.Ecoembes.dao.ContenedorRepository;
import DS_06.Ecoembes.dao.PlantaReciclajeRepository;
import DS_06.Ecoembes.entity.Contenedor;
import DS_06.Ecoembes.entity.Llenado;
import DS_06.Ecoembes.entity.PlantaReciclaje;
import DS_06.Ecoembes.entity.User;
import DS_06.Ecoembes.external.IPlantaReciclajeGateway;
import DS_06.Ecoembes.external.PlantaReciclajeFactory;

@Service
@Transactional
public class ReciclajeService {

    private final ContenedorRepository contenedorRepository;
    private final PlantaReciclajeRepository plantaReciclajeRepository;
    private final PlantaReciclajeFactory gatewayFactory;

    public ReciclajeService(ContenedorRepository contenedorRepository, 
                           PlantaReciclajeRepository plantaReciclajeRepository,
                           PlantaReciclajeFactory gatewayFactory) {
        this.contenedorRepository = contenedorRepository;
        this.plantaReciclajeRepository = plantaReciclajeRepository;
        this.gatewayFactory = gatewayFactory;
    }

    // Get all contenedores
    @Transactional(readOnly = true)
    public List<Contenedor> getContenedores() {
        return contenedorRepository.findAll();
    }

    // Get all plantasReciclaje - Aseguramos que los contenedores se carguen dentro de la transacción
    @Transactional(readOnly = true)
    public List<PlantaReciclaje> getPlantasReciclaje() {
        List<PlantaReciclaje> plantas = plantaReciclajeRepository.findAll();
        
        // Para cada planta, cargamos la colección LAZY de contenedores dentro de la transacción
        for (PlantaReciclaje planta : plantas) {
            // Inicializamos la colección accediendo a ella (por ejemplo, obteniendo su tamaño)
            planta.getContenedores().size(); // Esto fuerza la carga de los contenedores
        }
        
        return plantas;
    }
    
    public void asignarContenedorAPlanta(User usuario, long contenedorId, long plantaId) {
        // Obtener contenedor
        Contenedor contenedor = contenedorRepository.findById(contenedorId)
            .orElseThrow(() -> new RuntimeException("Contenedor no encontrado"));

        // Obtener planta
        PlantaReciclaje planta = plantaReciclajeRepository.findById(plantaId)
            .orElseThrow(() -> new RuntimeException("Planta de reciclaje no encontrada"));

        // Determinar tipo de planta si no está establecido
        if (planta.getTipoPlanta() == null || "DESCONOCIDO".equals(planta.getTipoPlanta())) {
            planta.determinarTipoPorNombre();
        }

        // Si el tipo es DESCONOCIDO, usar lógica local
        if ("DESCONOCIDO".equals(planta.getTipoPlanta())) {
            asignarContenedorLocal(usuario, contenedor, planta);
            return;
        }

        // Obtener gateway para planta externa
        IPlantaReciclajeGateway gateway;
        try {
            gateway = gatewayFactory.createGateway(planta.getTipoPlanta());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de planta no soportado: " + planta.getTipoPlanta());
        }

        try {
            // 1. Consultar capacidad disponible en planta externa
            int capacidadDisponibleExterna = gateway.consultarCapacidadDisponible(plantaId);
            
            // 2. Verificar capacidad externa
            if (capacidadDisponibleExterna < contenedor.getOcupado()) {
                throw new RuntimeException("Capacidad insuficiente en la planta externa. Disponible: " + capacidadDisponibleExterna);
            }
            
            // 3. Enviar contenedor a planta externa
            boolean exito = gateway.enviarContenedor(plantaId, contenedor);
            if (!exito) {
                throw new RuntimeException("Error al enviar contenedor a la planta externa");
            }
            
            // 4. Actualizar estado localmente
            actualizarAsignacionLocal(usuario, contenedor, planta);
            
        } catch (Exception e) {
            // Si falla la comunicación con la planta externa, intentar asignación local
            logger.warn("Error al comunicar con planta externa {}, intentando asignación local: {}", 
                       planta.getTipoPlanta(), e.getMessage());
            asignarContenedorLocal(usuario, contenedor, planta);
        }
    }

    // Método para asignación local (sin comunicación externa)
    private void asignarContenedorLocal(User usuario, Contenedor contenedor, PlantaReciclaje planta) {
        // Verificar capacidad disponible localmente
        if (planta.getCapacidadDisponible() < contenedor.getOcupado()) {
            throw new RuntimeException("Capacidad insuficiente en la planta. Disponible: " + planta.getCapacidadDisponible());
        }

        // Asignar localmente
        actualizarAsignacionLocal(usuario, contenedor, planta);
        
        logger.info("Contenedor {} asignado localmente a planta {}", 
                   contenedor.getId(), planta.getNombre());
    }

    // Actualizar asignación local en la base de datos
    private void actualizarAsignacionLocal(User usuario, Contenedor contenedor, PlantaReciclaje planta) {
        // Actualizar los campos de auditoría en el contenedor
        contenedor.setUserAsignacion(usuario);
        contenedor.setFechaAsignacion(new Date());
        contenedor.setPlantaReciclaje(planta);

        // Si no está ya en la lista, agregarlo
        if (!planta.getContenedores().contains(contenedor)) {
            planta.agregarContenedor(contenedor);
        }

        // Guardar cambios
        contenedorRepository.save(contenedor);
        plantaReciclajeRepository.save(planta);
    }

    // Get plantasReciclaje based on capacity
    @Transactional(readOnly = true)
    public PlantaReciclaje getPlantasReciclajeByCapacity(int capacity) {
        List<PlantaReciclaje> plantas = plantaReciclajeRepository.findAll();
        
        // Cargamos los contenedores para cada planta dentro de la transacción
        for (PlantaReciclaje planta : plantas) {
            planta.getContenedores().size();
        }
        
        return plantas.stream()
            .filter(planta -> planta.getCapacidad() == capacity)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Planta no encontrada con capacidad: " + capacity));
    }

    // Get el llenado de un contenedor por fecha
    @Transactional(readOnly = true)
    public Llenado getLlenadoContenedorByDate(long contenedorId, long date) {
        Contenedor contenedor = contenedorRepository.findById(contenedorId)
            .orElseThrow(() -> new RuntimeException("Contenedor no encontrado"));
        
        Date fechaConsulta = new Date(date);
        Date fechaVaciado = contenedor.getFechaVaciado();
        
        if (fechaVaciado != null && fechaVaciado.getTime() == fechaConsulta.getTime()) {
            return contenedor.getNivelDeLlenado();
        } else {
            throw new RuntimeException("No data for the given date");
        }
    }

    // Consulta del estado de los contenedores de una zona en una determinada fecha
    @Transactional(readOnly = true)
    public List<Contenedor> getContenedoresByDateAndPostalCode(long date, int postalCode) {
        Date fechaConsulta = new Date(date);
        
        List<Contenedor> contenedores = contenedorRepository.findAll();
        
        // Filtramos y devolvemos
        return contenedores.stream()
                .filter(contenedor -> 
                    contenedor.getCodigoPostal() == postalCode && 
                    contenedor.getFechaVaciado() != null &&
                    contenedor.getFechaVaciado().getTime() == fechaConsulta.getTime())
                .toList();
    }
    
    // Method to add a new Contenedor
    public void addContenedor(Contenedor contenedor) {
        if (contenedor != null) {
            contenedorRepository.save(contenedor);
        }
    }
    
    // Method to make contenedor (crear nuevo contenedor)
    public void makeContenedor(User user, long contenedorId, int codigoPostal, float capacidad) {
        // Validar que no exista un contenedor con ese ID
        if (contenedorRepository.existsById(contenedorId)) {
            throw new RuntimeException("Contenedor already exists");
        }
        
        // Validaciones
        if (codigoPostal <= 0 || capacidad <= 0) {
            throw new RuntimeException("Invalid parameters");
        }

        // Crear nuevo contenedor (ID será autogenerado, ignoramos el contenedorId del parámetro)
        Contenedor cont = new Contenedor(codigoPostal, capacidad, Llenado.VERDE, new Date());
        cont.setUserAsignacion(user);
        
        contenedorRepository.save(cont);
    }

    // Method to add a new PlantaReciclaje
    public void addPlantaReciclaje(PlantaReciclaje planta) {
        if (planta != null) {
            plantaReciclajeRepository.save(planta);
        }
    }
    
    // Consultar estado de una planta (usando gateway si es externa)
    @Transactional(readOnly = true)
    public String consultarEstadoPlanta(long plantaId) {
        PlantaReciclaje planta = plantaReciclajeRepository.findById(plantaId)
            .orElseThrow(() -> new RuntimeException("Planta no encontrada"));
        
        // Cargar contenedores para la planta actual dentro de la transacción
        planta.getContenedores().size();
        
        // Si es planta local
        if ("DESCONOCIDO".equals(planta.getTipoPlanta()) || planta.getTipoPlanta() == null) {
            int capacidadDisponible = planta.getCapacidadDisponible();
            return String.format("Local - Capacidad disponible: %d/%d", 
                               capacidadDisponible, planta.getCapacidad());
        }
        
        // Si es planta externa, usar gateway
        try {
            IPlantaReciclajeGateway gateway = gatewayFactory.createGateway(planta.getTipoPlanta());
            return gateway.obtenerEstado(plantaId);
        } catch (Exception e) {
            return "ERROR_CONEXION - " + e.getMessage();
        }
    }
    
    // Método para probar conexión con plantas externas
    @Transactional(readOnly = true)
    public boolean probarConexionPlanta(long plantaId) {
        PlantaReciclaje planta = plantaReciclajeRepository.findById(plantaId)
            .orElseThrow(() -> new RuntimeException("Planta no encontrada"));
        
        if ("DESCONOCIDO".equals(planta.getTipoPlanta()) || planta.getTipoPlanta() == null) {
            return true; // Planta local siempre "conectada"
        }
        
        try {
            IPlantaReciclajeGateway gateway = gatewayFactory.createGateway(planta.getTipoPlanta());
            gateway.consultarCapacidadDisponible(plantaId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Para logging
    private static final org.slf4j.Logger logger = 
        org.slf4j.LoggerFactory.getLogger(ReciclajeService.class);
}