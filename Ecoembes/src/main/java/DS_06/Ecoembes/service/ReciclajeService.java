package DS_06.Ecoembes.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import DS_06.Ecoembes.dao.ContenedorRepository;
import DS_06.Ecoembes.dao.PlantaReciclajeRepository;
import DS_06.Ecoembes.entity.Contenedor;
import DS_06.Ecoembes.entity.Llenado;
import DS_06.Ecoembes.entity.PlantaReciclaje;
import DS_06.Ecoembes.entity.User;

@Service
@Transactional
public class ReciclajeService {

    private final ContenedorRepository contenedorRepository;
    private final PlantaReciclajeRepository plantaReciclajeRepository;

    public ReciclajeService(ContenedorRepository contenedorRepository, 
                           PlantaReciclajeRepository plantaReciclajeRepository) {
        this.contenedorRepository = contenedorRepository;
        this.plantaReciclajeRepository = plantaReciclajeRepository;
    }

    // Get all contenedores
    public List<Contenedor> getContenedores() {
        return contenedorRepository.findAll();
    }

    // Get all plantasReciclaje
    public List<PlantaReciclaje> getPlantasReciclaje() {
        return plantaReciclajeRepository.findAll();
    }
    
    public void asignarContenedorAPlanta(User usuario, long contenedorId, long plantaId) {
        Optional<Contenedor> contenedorOpt = contenedorRepository.findById(contenedorId);
        if (contenedorOpt.isEmpty()) {
            throw new RuntimeException("Contenedor no encontrado");
        }
        Contenedor contenedor = contenedorOpt.get();

        Optional<PlantaReciclaje> plantaOpt = plantaReciclajeRepository.findById(plantaId);
        if (plantaOpt.isEmpty()) {
            throw new RuntimeException("Planta de reciclaje no encontrada");
        }
        PlantaReciclaje planta = plantaOpt.get();

        // Verificar capacidad disponible
        if (planta.getCapacidadDisponible() < contenedor.getOcupado()) {
            throw new RuntimeException("Capacidad insuficiente en la planta. Disponible: " + planta.getCapacidadDisponible());
        }

        // Actualizar los campos de auditoría en el contenedor
        contenedor.setUserAsignacion(usuario);
        contenedor.setFechaAsignacion(new Date());

        // Asignar el contenedor a la planta (si no está ya asignado)
        if (!planta.getListaContenedor().contains(contenedor)) {
            planta.agregarContenedor(contenedor);
        }

        // Actualizar el contenedor y la planta en la base de datos
        contenedorRepository.save(contenedor);
        plantaReciclajeRepository.save(planta);
    }

    // Get plantasReciclaje based on capacity (este método ya no es muy útil, pero lo mantenemos)
    public PlantaReciclaje getPlantasReciclajeByCapacity(int capacity) {
        List<PlantaReciclaje> plantas = plantaReciclajeRepository.findAll();
        for (PlantaReciclaje planta : plantas) {
            if (planta.getCapacidad() == capacity) {
                return planta;
            }
        }
        throw new RuntimeException("Planta no encontrada con capacidad: " + capacity);
    }

    // Get el llenado de un contenedor por fecha
    public Llenado getLlenadoContenedorByDate(long contenedorId, long date) {
        Optional<Contenedor> contenedorOpt = contenedorRepository.findById(contenedorId);
        if (contenedorOpt.isEmpty()) {
            throw new RuntimeException("Contenedor no encontrado");
        }
        Contenedor contenedor = contenedorOpt.get();
        
        // Convertir long a Date para comparar
        Date fechaConsulta = new Date(date);
        Date fechaVaciado = contenedor.getFechaVaciado();
        
        // Comparar fechas (podrías comparar solo el día, ignorando la hora, dependiendo de tu lógica)
        if (fechaVaciado != null && 
            fechaVaciado.getTime() == fechaConsulta.getTime()) { // Comparación exacta
            return contenedor.getNivelDeLlenado();
        } else {
            throw new RuntimeException("No data for the given date");
        }
    }

    // Consulta del estado de los contenedores de una zona en una determinada fecha
    public List<Contenedor> getContenedoresByDateAndPostalCode(long date, int postalCode) {
        Date fechaConsulta = new Date(date);
        List<Contenedor> todosContenedores = contenedorRepository.findAll();
        
        return todosContenedores.stream()
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
        // Verificar si ya existe un contenedor con ese ID
        if (contenedorRepository.existsById(contenedorId)) {
            throw new RuntimeException("Contenedor already exists");
        }
        
        // Validaciones
        if (codigoPostal <= 0 || capacidad <= 0) {
            throw new RuntimeException("Invalid parameters");
        }

        // Crear nuevo contenedor - ID será autogenerado por JPA, no usamos el parámetro contenedorId
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
}