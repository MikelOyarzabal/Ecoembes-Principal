package DS_06.Ecoembes.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlantaReciclajeFactory {
    
    private final PlasSBGateway plasSBGateway;
    private final ContSocketGateway contSocketGateway;
    
    @Autowired
    public PlantaReciclajeFactory(PlasSBGateway plasSBGateway, ContSocketGateway contSocketGateway) {
        this.plasSBGateway = plasSBGateway;
        this.contSocketGateway = contSocketGateway;
    }
    
    public IPlantaReciclajeGateway createGateway(String tipoPlanta) {
        if (tipoPlanta == null || tipoPlanta.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de planta no puede ser nulo o vacío");
        }
        
        String tipoNormalizado = tipoPlanta.trim().toUpperCase();
        switch(tipoNormalizado) {
            case "PLASSB":
                return plasSBGateway;
            case "CONTSOCKET":
                return contSocketGateway;
            default:
                throw new IllegalArgumentException("Tipo de planta no soportado: " + tipoPlanta);
        }
    }
}