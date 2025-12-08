package com.plassb.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plassb.dto.CapacidadResponse;
import com.plassb.dto.RecepcionContenedorRequest;
import com.plassb.dto.RecepcionContenedorResponse;
import com.plassb.model.ContenedorExterno;
import com.plassb.model.Planta;
import com.plassb.repository.ContenedorExternoRepository;
import com.plassb.repository.PlantaRepository;

@Service
@Transactional
public class PlantaReciclajeService {
    
    private final PlantaRepository plantaRepository;
    private final ContenedorExternoRepository contenedorRepository; // ← AÑADIR
    
    // ← MODIFICAR CONSTRUCTOR
    public PlantaReciclajeService(PlantaRepository plantaRepository, 
                                  ContenedorExternoRepository contenedorRepository) {
        this.plantaRepository = plantaRepository;
        this.contenedorRepository = contenedorRepository; // ← AÑADIR
        inicializarDatos();
    }
    
    private void inicializarDatos() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  PlasSB Service Iniciado               ║");
        System.out.println("║  Esperando registro de plantas...     ║");
        System.out.println("╚════════════════════════════════════════╝\n");
    }
    
    public void registrarPlanta(Long id, String nombre, int capacidadTotal) {
        if (plantaRepository.existsById(id)) {
            System.out.println(" ⚠️  Planta " + id + " ya existe, actualizando...");
            Planta planta = plantaRepository.findById(id).get();
            planta.setNombre(nombre);
            planta.setCapacidadTotal(capacidadTotal);
            planta.setCapacidadDisponible(capacidadTotal);
            plantaRepository.save(planta);
        } else {
            Planta planta = new Planta(nombre, capacidadTotal, capacidadTotal);
            planta.setId(id);
            plantaRepository.save(planta);
            
            System.out.println(" ✓ Planta registrada:");
            System.out.println("    - ID: " + id);
            System.out.println("    - Nombre: " + nombre);
            System.out.println("    - Capacidad: " + capacidadTotal + " kg\n");
        }
    }
    
    public CapacidadResponse consultarCapacidadDisponible(long plantaId) {
        Optional<Planta> plantaOpt = plantaRepository.findById(plantaId);
        
        if (plantaOpt.isPresent()) {
            Planta planta = plantaOpt.get();
            String estado = determinarEstado(planta);
            
            System.out.println(" → Consulta capacidad planta " + plantaId + ": " + 
                             planta.getCapacidadDisponible() + " kg (" + estado + ")");
            
            return new CapacidadResponse(
                planta.getCapacidadDisponible(),
                planta.getCapacidadTotal(),
                estado
            );
        } else {
            System.err.println(" ✗ Planta " + plantaId + " no encontrada");
            return new CapacidadResponse(0, 0, "PLANTA_NO_ENCONTRADA");
        }
    }
    
    public RecepcionContenedorResponse recibirContenedor(long plantaId, RecepcionContenedorRequest request) {
        Optional<Planta> plantaOpt = plantaRepository.findById(plantaId);
        
        if (plantaOpt.isEmpty()) {
            System.err.println(" ✗ Planta " + plantaId + " no encontrada");
            return new RecepcionContenedorResponse(false, "Planta no encontrada", 0);
        }
        
        Planta planta = plantaOpt.get();
        int capacidadOcupada = (int) Math.ceil(request.getCapacidad());
        
        if (planta.getCapacidadDisponible() >= capacidadOcupada) {
            boolean reducido = planta.reducirCapacidad(capacidadOcupada);
            if (reducido) {
                plantaRepository.save(planta);
                
                // GUARDAR EL CONTENEDOR EN LA BD
                ContenedorExterno contenedor = new ContenedorExterno();
                contenedor.setIdExterno(request.getId());
                contenedor.setCodigoPostal(request.getCodigoPostal());
                contenedor.setCapacidad(request.getCapacidad());
                contenedor.setNivelLlenado(request.getNivelLlenado());
                contenedor.setFechaVaciado(request.getFechaVaciado());
                contenedor.setFechaRecepcion(new Date());
                contenedor.setPlanta(planta);
                
                contenedorRepository.save(contenedor);
                System.out.println(" 💾 Contenedor guardado en BD (ID externo: " + request.getId() + ")");
                
                System.out.println(" 📦 Contenedor recibido en planta " + plantaId);
                System.out.println("    - Contenedor ID: " + request.getId());
                System.out.println("    - Capacidad ocupada: " + capacidadOcupada + " kg");
                System.out.println("    - Capacidad restante: " + planta.getCapacidadDisponible() + " kg");
                
                return new RecepcionContenedorResponse(
                    true, 
                    "Contenedor recibido exitosamente", 
                    planta.getCapacidadDisponible()
                );
            } else {
                System.err.println(" ✗ Error al reducir capacidad");
                return new RecepcionContenedorResponse(
                    false, 
                    "Error al reducir capacidad", 
                    planta.getCapacidadDisponible()
                );
            }
        } else {
            System.out.println(" ⚠️  Contenedor rechazado: capacidad insuficiente");
            System.out.println("    - Necesario: " + capacidadOcupada + " kg");
            System.out.println("    - Disponible: " + planta.getCapacidadDisponible() + " kg");
            
            return new RecepcionContenedorResponse(
                false, 
                "Capacidad insuficiente en la planta", 
                planta.getCapacidadDisponible()
            );
        }
    }
    
    public String obtenerEstado(long plantaId) {
        Optional<Planta> plantaOpt = plantaRepository.findById(plantaId);
        
        if (plantaOpt.isPresent()) {
            Planta planta = plantaOpt.get();
            String estado = determinarEstado(planta);
            
            int capacidadOcupada = planta.getCapacidadTotal() - planta.getCapacidadDisponible();
            int porcentaje = (capacidadOcupada * 100) / planta.getCapacidadTotal();
            
            System.out.println(" → Estado planta " + plantaId + ": " + estado);
            System.out.println("    - Ocupación: " + capacidadOcupada + "/" + planta.getCapacidadTotal() + 
                             " kg (" + porcentaje + "%)");
            
            return estado;
        } else {
            return "NO_ENCONTRADA";
        }
    }
    
    private String determinarEstado(Planta planta) {
        if (planta.getCapacidadDisponible() <= 0) {
            return "SIN_CAPACIDAD";
        }
        
        int porcentaje = (int) ((planta.getCapacidadDisponible() * 100.0) / planta.getCapacidadTotal());
        
        if (porcentaje >= 75) {
            return "OPERATIVA_ALTA_CAPACIDAD";
        } else if (porcentaje >= 50) {
            return "OPERATIVA_CAPACIDAD_MEDIA";
        } else if (porcentaje >= 25) {
            return "OPERATIVA_BAJA_CAPACIDAD";
        } else {
            return "OPERATIVA_CAPACIDAD_CRITICA";
        }
    }
    
    public void resetearCapacidad(long plantaId) {
        Optional<Planta> plantaOpt = plantaRepository.findById(plantaId);
        if (plantaOpt.isPresent()) {
            Planta planta = plantaOpt.get();
            planta.setCapacidadDisponible(planta.getCapacidadTotal());
            plantaRepository.save(planta);
            System.out.println(" ✓ Capacidad reseteada para planta " + plantaId);
        }
    }
}