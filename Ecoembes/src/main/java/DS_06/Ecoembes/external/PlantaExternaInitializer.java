package DS_06.Ecoembes.external;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import DS_06.Ecoembes.dao.PlantaReciclajeRepository;
import DS_06.Ecoembes.entity.PlantaReciclaje;

@Component
public class PlantaExternaInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(PlantaExternaInitializer.class);
    
    @Autowired
    private PlantaReciclajeRepository plantaReciclajeRepository;
    
    @Autowired
    private PlasSBGateway plasSBGateway;  // ← AÑADIR ESTO
    
    /**
     * Registra las plantas de Ecoembes en los servidores externos
     */
    public void registrarPlantasExternas() {
        logger.info("=== Iniciando registro de plantas en servidores externos ===");
        
        List<PlantaReciclaje> plantas = plantaReciclajeRepository.findAll();
        
        for (PlantaReciclaje planta : plantas) {
            if ("PLASSB".equals(planta.getTipoPlanta())) {
                registrarEnPlasSB(planta);
            } else if ("CONTSOCKET".equals(planta.getTipoPlanta())) {
                logger.info("Planta ContSocket ID {} usa ID hardcoded, no requiere registro", planta.getId());
            } else {
                logger.warn("Planta ID {} tiene tipo desconocido: {}", planta.getId(), planta.getTipoPlanta());
            }
        }
        
        logger.info("=== Registro de plantas externas completado ===\n");
    }
    
    /**
     * Registra una planta en el servidor PlasSB
     */
    private void registrarEnPlasSB(PlantaReciclaje planta) {
        try {
            logger.info("Registrando planta '{}' (ID: {}) en PlasSB...", planta.getNombre(), planta.getId());
            
            plasSBGateway.registrarPlanta(
                planta.getId(),
                planta.getNombre(),
                planta.getCapacidad()
            );
            
            logger.info("✓ Planta {} registrada exitosamente en PlasSB", planta.getId());
            
        } catch (Exception e) {
            logger.error("⚠️  No se pudo conectar con PlasSB para registrar planta {}: {}", 
                        planta.getId(), e.getMessage());
        }
    }
}