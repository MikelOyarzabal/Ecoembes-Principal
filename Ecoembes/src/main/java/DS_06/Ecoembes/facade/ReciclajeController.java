
package DS_06.Ecoembes.facade;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import DS_06.Ecoembes.dto.ContenedorDTO;
import DS_06.Ecoembes.dto.PlantaReciclajeDTO;
import DS_06.Ecoembes.entity.Contenedor;
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

	// GET all categories
	@Operation(
		summary = "Get all Contenedores",
		description = "Returns a list of all available Contenedores",
		responses = {
			@ApiResponse(responseCode = "200", description = "OK: List of Contenedores retrieved successfully"),
			@ApiResponse(responseCode = "204", description = "No Content: No Contendores found"),
			@ApiResponse(responseCode = "500", description = "Internal server error")
		}
	)
	@GetMapping("/contenedores")
	public ResponseEntity<List<ContenedorDTO>> getAllContendores() {
		try {
			List<Contenedor> contenedores = reciclajeService.getContenedores();
			
			if (contenedores.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			List<ContenedorDTO> dtos = new ArrayList<>();
			contenedores.forEach(contenedor -> dtos.add(contenedorToDTO(contenedor)));
			
			return new ResponseEntity<>(dtos, HttpStatus.OK);
		} catch (Exception e) {
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
	public ResponseEntity<List<PlantaReciclajeDTO>> getAllPlantasReciclaje() {
		try {
			List<PlantaReciclaje> plantas = reciclajeService.getPlantasReciclaje();
			
			if (plantas.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			List<PlantaReciclajeDTO> dtos = new ArrayList<>();
			plantas.forEach(planta -> dtos.add(plantaReciclajeToDTO(planta)));
			
			return new ResponseEntity<>(dtos, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// GET PlantaReciclaje by capacity
	@Operation(
		summary = "Get Planta Reciclaje by capacity",
		description = "Returns a Planta Reciclaje for a given capacity",
		responses = {
			@ApiResponse(responseCode = "200", description = "OK: Planta Reciclaje retrieved successfully"),
			@ApiResponse(responseCode = "404", description = "Not Found: Planta Reciclaje not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error")
		}
	)
	
	@GetMapping("/plantasreciclaje/capacity/{capacity}")
	public ResponseEntity<PlantaReciclajeDTO> getPlantaReciclajeByCapacity(
			@Parameter(name = "capacity", description = "Capacity of the Planta Reciclaje", required = true, example = "5000")
			@PathVariable("capacity") int capacity) {
		try {
			PlantaReciclaje planta = reciclajeService.getPlantasReciclajeByCapacity(capacity);
						
			if (planta != null) {
				PlantaReciclajeDTO dto = plantaReciclajeToDTO(planta);
				return new ResponseEntity<>(dto, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// GET llenado of a contenedor by date
	
	
	// POST to make a Contenedor
	@Operation(
			summary = "Make a contendor",
		    description = "Allows a user to make a contendor",
		    responses = {
		        @ApiResponse(responseCode = "204", description = "No Content: Contendor placed successfully"),
		        @ApiResponse(responseCode = "401", description = "Unauthorized: User not authenticated"),
		        @ApiResponse(responseCode = "500", description = "Internal server error")
		    }
		)		
	@PostMapping("/contenedor")
	public ResponseEntity<Void> makeContenedor(
			@Parameter(name = "contenedorId", description = "ID of the contendor", required = true, example = "1")		
			@PathVariable("contenedorId") long id,
			@Parameter(name = "codigoPostal", description = "codigoPostal del contendor", required = true, example = "486236")
    		@RequestParam("codigoPostal") int codigoPostal,
    		@Parameter(name = "capacidad", description = "capacidad del contenedor", required = true, example = "10")
			@RequestParam("capacidad") float capacidad,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Authorization token in plain text", required = true)
    		@RequestBody String token) { 
	    try {	
	    	User user = authService.getUserByToken(token);
	    	
	    	if (user == null) {
	    		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	    	}
	        reciclajeService.makeContenedor(user, id, codigoPostal, capacidad);
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

	    } catch (Exception e) {
	        switch (e.getMessage()) {
            case "Article not found":
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            case "Bid amount must be greater than the current price":
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            default:
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	    }
	
	
	
	// GET articles by category name
//	@Operation(
//		summary = "Get Planta Reciclaje by category capacidad",
//		description = "Returns a list of all articles for a given category",
//		responses = {
//			@ApiResponse(responseCode = "200", description = "OK: List of articles retrieved successfully"),
//			@ApiResponse(responseCode = "204", description = "No Content: Category has no articles"),
//			@ApiResponse(responseCode = "400", description = "Bad Request: Currency not supported"),
//			@ApiResponse(responseCode = "404", description = "Not Found: Category not found"),
//			@ApiResponse(responseCode = "500", description = "Internal server error")
//		}
//	)
/*	 
	@GetMapping("/categories/{categoryName}/articles")
	public ResponseEntity<List<ArticleDTO>> getArticlesByCategory(
			@Parameter(name = "categoryName", description = "Name of the category", required = true, example = "Electronics")
			@PathVariable("categoryName") String category,
			@Parameter(name = "currency", description = "Currency", required = true, example = "GBP")
			@RequestParam("currency") String currentCurrency) {
		try {
			List<Article> articles = auctionsService.getPlantasByCategoryName(category);
						
			if (articles.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			Optional<Float> exchangeRate = currencyService.getExchangeRate(currentCurrency);
			
			if (!exchangeRate.isPresent()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
			
			List<ArticleDTO> dtos = new ArrayList<>();
			articles.forEach(article -> dtos.add(articleToDTO(article, exchangeRate.get(), currentCurrency)));
			
			return new ResponseEntity<>(dtos, HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// GET details of an article by ID
	@Operation(
		summary = "Get the details of an article by its ID",
		description = "Returns the details of the article with the specified ID",
		responses = {
			@ApiResponse(responseCode = "200", description = "OK: Article details retrieved successfully"),
			@ApiResponse(responseCode = "400", description = "Bad Request: Currency not supported"),
			@ApiResponse(responseCode = "404", description = "Not Found: Article not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error")
		}
	)
	 
	@GetMapping("/articles/{articleId}/details")
	public ResponseEntity<ArticleDTO> getArticleDetails(
			@Parameter(name = "articleId", description = "Id of the article", required = true, example = "1")
			@PathVariable("articleId") long id,
			@Parameter(name = "currency", description = "Currency", required = true, example = "EUR")
			@RequestParam("currency") String currentCurrency) {
		try {
			Article article = auctionsService.getArticleById(id);			
			
			if (article != null) {				
				Optional<Float> exchangeRate = currencyService.getExchangeRate(currentCurrency);
				
				if (!exchangeRate.isPresent()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
				
				ArticleDTO dto = articleToDTO(article, exchangeRate.get(), currentCurrency);
				
				return new ResponseEntity<>(dto, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// POST to make a bid on an article
	@Operation(
	    summary = "Make a bid on an article",
	    description = "Allows a user to make a bid on a specified article within a category",
	    responses = {
	        @ApiResponse(responseCode = "204", description = "No Content: Bid placed successfully"),
			@ApiResponse(responseCode = "400", description = "Bad Request: Currency not supported"),
	        @ApiResponse(responseCode = "401", description = "Unauthorized: User not authenticated"),
	        @ApiResponse(responseCode = "404", description = "Not Found: Article not found"),
	        @ApiResponse(responseCode = "409", description = "Conflict: Bid amount must be greater than the current price"),
	        @ApiResponse(responseCode = "500", description = "Internal server error")
	    }
	)		
	@PostMapping("/articles/{articleId}/bid")
	public ResponseEntity<Void> makeBid(
			@Parameter(name = "articleId", description = "ID of the article to bid on", required = true, example = "1")		
			@PathVariable("articleId") long id,
			@Parameter(name = "amount", description = "Bid amount", required = true, example = "1001")
    		@RequestParam("amount") float price,
    		@Parameter(name = "currency", description = "Currency", required = true, example = "EUR")
			@RequestParam("currency") String currentCurrency,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Authorization token in plain text", required = true)
    		@RequestBody String token) { 
	    try {	    	
	    	User user = authService.getUserByToken(token);
	    	
	    	if (user == null) {
	    		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	    	}
	    	
			Optional<Float> exchangeRate = currencyService.getExchangeRate(currentCurrency);
			
			if (!exchangeRate.isPresent()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
	    	
			// If the currency is not EUR, convert the amount to EUR
			if (!currentCurrency.equals("EUR")) {			    
				price /= exchangeRate.get(); // Inverting the exchange rate
			}
			
	        auctionsService.makeBid(user, id, price);
	        
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    } catch (Exception e) {
	        switch (e.getMessage()) {
	            case "Article not found":
	                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	            case "Bid amount must be greater than the current price":
	                return new ResponseEntity<>(HttpStatus.CONFLICT);
	            default:
	                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	}
*/
	// Converts a Contenedor to a Contenedor
	private ContenedorDTO contenedorToDTO(Contenedor contenedor) {
		return new ContenedorDTO(contenedor.getId(), 
				                 contenedor.getCodigoPostal(), 
				                 contenedor.getCapacidad(),
				                 contenedor.getNivelDeLlenado(),
				                 contenedor.getFechaVaciado());
	}
	
	// Converts an Article to an ArticleDTO
	private PlantaReciclajeDTO plantaReciclajeToDTO(PlantaReciclaje planta) {
		return new PlantaReciclajeDTO(planta.getId(), 
				                 planta.getNombre(), 
				                 planta.getCapacidad(),
				                 planta.getCapacidadDisponible(),
				                 planta.getListaContenedor()
				                 );
		
	}
}