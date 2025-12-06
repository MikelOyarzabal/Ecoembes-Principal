package DS_06.Ecoembes.facade;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import DS_06.Ecoembes.dto.ContenedorDTO;
import DS_06.Ecoembes.dto.PlantaReciclajeDTO;
import DS_06.Ecoembes.entity.Contenedor;
import DS_06.Ecoembes.entity.Llenado;
import DS_06.Ecoembes.entity.PlantaReciclaje;
import DS_06.Ecoembes.entity.User;
import DS_06.Ecoembes.service.AuthService;
import DS_06.Ecoembes.service.ReciclajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/reciclaje")
@Tag(name = "Reciclaje Controller", description = "Operations related to contenedor and plantareciclaje")
public class ReciclajeController {

    private final ReciclajeService reciclajeService;
    private final AuthService authService;

    public ReciclajeController(ReciclajeService reciclajeService, AuthService authService) {
        this.reciclajeService = reciclajeService;
        this.authService = authService;
    }

    // GET all contenedores
    @Operation(
        summary = "Get all Contenedores",
        description = "Returns a list of all available Contenedores",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK: List of Contenedores retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No Content: No Contenedores found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @GetMapping("/contenedores")
    public ResponseEntity<List<ContenedorDTO>> getAllContenedores(
        @Parameter(name = "token", description = "Token de autenticación", required = false)
        @RequestParam(value = "token", required = false) String token) {  // CAMBIADO
        try {
            List<Contenedor> contenedores = reciclajeService.getContenedores();

            if (contenedores == null || contenedores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            List<ContenedorDTO> dtos = new ArrayList<>();
            for (Contenedor contenedor : contenedores) {
                dtos.add(contenedorToDTO(contenedor));
            }

            return new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("ERROR en getAllContenedores: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
        summary = "Get llenado de un contenedor por fecha",
        description = "Returns the fill level of a specific container for a given date",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK: Llenado retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Not Found: No data for the given date"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @GetMapping("/contenedores/{contenedorId}/llenado")
    public ResponseEntity<Llenado> getLlenadoContenedorByDate(
        @Parameter(name = "contenedorId", description = "ID del contenedor", required = true, example = "123")
        @PathVariable("contenedorId") long contenedorId,
        @Parameter(name = "date", description = "Fecha en formato timestamp", required = true, example = "1704067200000")
        @RequestParam(value = "date") long date,  // CAMBIADO
        @Parameter(name = "token", description = "Token de autenticación", required = false)
        @RequestParam(value = "token", required = false) String token) {  // CAMBIADO
        try {
            Llenado llenado = reciclajeService.getLlenadoContenedorByDate(contenedorId, date);
            return new ResponseEntity<>(llenado, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("No data for the given date")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            System.err.println("ERROR en getLlenadoContenedorByDate: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
        summary = "Get contenedores por código postal y fecha",
        description = "Permite visualizar el estado de los contenedores de una zona específica (identificada por código postal) en una fecha determinada",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK: Lista de contenedores recuperada exitosamente"),
            @ApiResponse(responseCode = "204", description = "No Content: No se encontraron contenedores para los criterios especificados"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @GetMapping("/contenedores/zona")
    public ResponseEntity<List<ContenedorDTO>> getContenedoresByDateAndPostalCode(
        @Parameter(name = "fecha", description = "Fecha en formato timestamp", required = true, example = "0")
        @RequestParam(value = "fecha") long fecha,  // CAMBIADO
        @Parameter(name = "codigoPostal", description = "Código postal de la zona", required = true, example = "28001")
        @RequestParam(value = "codigoPostal") int codigoPostal,  // CAMBIADO
        @Parameter(name = "token", description = "Token de autenticación", required = false)
        @RequestParam(value = "token", required = false) String token) {  // CAMBIADO
        try {
            List<Contenedor> contenedores = reciclajeService.getContenedoresByDateAndPostalCode(fecha, codigoPostal);

            if (contenedores == null || contenedores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            List<ContenedorDTO> dtos = new ArrayList<>();
            for (Contenedor contenedor : contenedores) {
                dtos.add(contenedorToDTO(contenedor));
            }

            return new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("ERROR en getContenedoresByDateAndPostalCode: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET all PlantaReciclaje
    @Operation(
        summary = "Get all Planta Reciclaje",
        description = "Returns a list of all available Planta Reciclaje",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK: List of Planta Reciclaje retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No Content: No Planta Reciclaje found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @GetMapping("/plantasreciclaje")
    public ResponseEntity<List<PlantaReciclajeDTO>> getAllPlantasReciclaje(
        @Parameter(name = "token", description = "Token de autenticación", required = false)
        @RequestParam(value = "token", required = false) String token) {  // CAMBIADO
        try {
            List<PlantaReciclaje> plantas = reciclajeService.getPlantasReciclaje();

            if (plantas == null || plantas.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            List<PlantaReciclajeDTO> dtos = new ArrayList<>();
            for (PlantaReciclaje planta : plantas) {
                dtos.add(plantaReciclajeToDTO(planta));
            }

            return new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("ERROR en getAllPlantasReciclaje: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST asignar Contenedor a PlantaReciclaje
    @Operation(
        summary = "Asignar Contenedor a Planta Reciclaje",
        description = "Permite asignar un contenedor específico a una planta de reciclaje",
        responses = {
            @ApiResponse(responseCode = "204", description = "No Content: Contenedor asignado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Usuario no autenticado"),
            @ApiResponse(responseCode = "404", description = "Not Found: Contenedor o Planta de reciclaje no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflict: Capacidad insuficiente en la planta"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @PostMapping("/plantasreciclaje/{idPlanta}/contenedor/{idContenedor}")
    public ResponseEntity<Void> asignarContenedorAPlanta(
        @Parameter(name = "idPlanta", description = "ID de la planta de reciclaje", required = true, example = "1")        
        @PathVariable("idPlanta") long plantaId,
        @Parameter(name = "idContenedor", description = "ID del contenedor", required = true, example = "1")        
        @PathVariable("idContenedor") long contenedorId,
        @Parameter(name = "token", description = "Token de autenticación", required = true)
        @RequestParam(value = "token") String token) {  // CAMBIADO
        try {    
            User user = authService.getUserByToken(token);

            if (user == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            reciclajeService.asignarContenedorAPlanta(user, contenedorId, plantaId);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            System.err.println("ERROR en asignarContenedorAPlanta: " + errorMessage);
            e.printStackTrace();
            if (errorMessage != null) {
                if (errorMessage.contains("Contenedor no encontrado") || 
                    errorMessage.contains("Planta de reciclaje no encontrada")) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                } else if (errorMessage.contains("Capacidad insuficiente") || 
                           errorMessage.contains("El contenedor ya está asignado")) {
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            System.err.println("ERROR en asignarContenedorAPlanta: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST to make a Contenedor
    @Operation(
        summary = "Make a contendor",
        description = "Allows a user to make a contendor",
        responses = {
            @ApiResponse(responseCode = "204", description = "No Content: Contendor placed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Not Found: Contenedor not found"),
            @ApiResponse(responseCode = "409", description = "Conflict: Contenedor already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )        
    @PostMapping("/contenedores/{contenedorId}")
    public ResponseEntity<Void> makeContenedor(
        @Parameter(name = "contenedorId", description = "ID of the contendor", required = true, example = "1")        
        @PathVariable("contenedorId") long id,
        @Parameter(name = "codigoPostal", description = "codigoPostal del contendor", required = true, example = "486236")
        @RequestParam(value = "codigoPostal") int codigoPostal,  // CAMBIADO
        @Parameter(name = "capacidad", description = "capacidad del contenedor", required = true, example = "10")
        @RequestParam(value = "capacidad") float capacidad,  // CAMBIADO
        @Parameter(name = "token", description = "Token de autenticación", required = true)
        @RequestParam(value = "token") String token) {  // CAMBIADO
        try {    
            User user = authService.getUserByToken(token);

            if (user == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            reciclajeService.makeContenedor(user, id, codigoPostal, capacidad);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            System.err.println("ERROR en makeContenedor: " + e.getMessage());
            e.printStackTrace();
            switch (e.getMessage()) {
            case "Contenedor not found":
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            case "Contenedor already exists":
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            default:
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    // Converts a Contenedor to a ContenedorDTO
    private ContenedorDTO contenedorToDTO(Contenedor contenedor) {
        // Solo incluimos datos básicos, NO relaciones LAZY
        return new ContenedorDTO(
            contenedor.getId(), 
            contenedor.getCodigoPostal(), 
            contenedor.getCapacidad(),
            contenedor.getNivelDeLlenado(),
            contenedor.getFechaVaciado()
        );
    }

    // Converts a PlantaReciclaje to a PlantaReciclajeDTO
    private PlantaReciclajeDTO plantaReciclajeToDTO(PlantaReciclaje planta) {
        // Convertir la lista de Contenedor a lista de ContenedorDTO
        List<ContenedorDTO> contenedoresDTO = new ArrayList<>();
        
        // IMPORTANTE: Como los contenedores son LAZY, solo accedemos si ya están cargados
        // Si no están cargados, dejamos la lista vacía
        try {
            List<Contenedor> contenedores = planta.getContenedores();
            
            if (contenedores != null && !contenedores.isEmpty()) {
                for (Contenedor contenedor : contenedores) {
                    // Solo incluimos datos básicos del contenedor, NO sus relaciones
                    contenedoresDTO.add(new ContenedorDTO(
                        contenedor.getId(),
                        contenedor.getCodigoPostal(),
                        contenedor.getCapacidad(),
                        contenedor.getNivelDeLlenado(),
                        contenedor.getFechaVaciado()
                    ));
                }
            }
        } catch (Exception e) {
            // Si hay error de LazyInitialization, simplemente no incluimos los contenedores
            System.err.println("Advertencia: No se pudieron cargar los contenedores para la planta " + planta.getId() + 
                             ". Error: " + e.getMessage());
        }
        
        // Obtener la capacidad disponible de la entidad
        int capacidadDisponible = planta.getCapacidadDisponible();
        
        // Usar el constructor con 5 parámetros
        return new PlantaReciclajeDTO(
            planta.getId(), 
            planta.getNombre(), 
            planta.getCapacidad(),
            capacidadDisponible,
            contenedoresDTO
        );
    }
}