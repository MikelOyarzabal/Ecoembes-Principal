
package DS_06.Ecoembes.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import DS_06.Ecoembes.entity.Contenedor;
import DS_06.Ecoembes.entity.Llenado;
import DS_06.Ecoembes.entity.PlantaReciclaje;
import DS_06.Ecoembes.entity.User;


@Service
public class ReciclajeService {

	// Simulating category and article repositories
	private static Map<Long, PlantaReciclaje> plantaReciclajeRepository = new HashMap<>();
	private static Map<Long, Contenedor> contenedorRepository = new HashMap<>();

	// Get all contenedores
	public List<Contenedor> getContenedores() {
		return contenedorRepository.values().stream().toList();
	}

	// Get all plantasReciclaje
	public List<PlantaReciclaje> getPlantasReciclaje() {
		return plantaReciclajeRepository.values().stream().toList();
	}

	// Get plantasReciclaje based on capacity
	public PlantaReciclaje getPlantasReciclajeByCapacity(int capacity) {
		for (PlantaReciclaje planta : plantaReciclajeRepository.values()) {
			if (planta.getCapacidad() == capacity) {
				return planta;
			}
		}
		throw new RuntimeException("Contenedor not found");
	}

	//Get el llenado de un contenedor por fecha
	public Llenado getLlenadoContenedorByDate(long contenedorId, long date) {
		Contenedor contenedor = contenedorRepository.get(contenedorId);
		if(contenedor.getFechaVaciado()==date) {
			return contenedor.getNivelDeLlenado();
		} else {
			throw new RuntimeException("No data for the given date");
		}
	}

	//Consulta del estado de los contenedores de una zona en una determinada fecha
	public List<Contenedor> getContenedoresByDateAndPostalCode(long date, int postalCode) {
		return contenedorRepository.values().stream()
				.filter(contenedor -> contenedor.getCodigoPostal() == postalCode && contenedor.getFechaVaciado() == date)
				.toList();
	}
	
	
	
	

	//	public List<Contenedor> getContenedoresByCapacity(int capacity) {
	//		Contenedor contenedor = contenedorRepository.get(capacity);
	//		if (contenedor == null) {
	//			throw new RuntimeException("Contenedor not found");
	//		}
	//		
	//		
	//	}
	//	public List<Cont> getArticlesByCategoryName(String categoryName) {
	//		Category category = categoryRepository.get(categoryName);
	//
	//		if (category == null) {
	//			throw new RuntimeException("Category not found");
	//		}
	//
	//		return category.getArticles();
	//	}
	//	
	//	// Get article by id
	//	public Contenedor getArticleById(long id) {
	//		return contenedorRepository.get(articleId);
	//	}

	//	// Make a bid on an article
	//	public void makeBid(User user, long articleId, float amount) {
	//		// Retrieve the article by ID
	//		Article article = articleRepository.get(articleId);
	//
	//		if (article == null) {
	//			throw new RuntimeException("Article not found");
	//		}
	//
	//		if (amount <= article.getCurrentPrice()) {
	//			throw new RuntimeException("Bid amount must be greater than the current price");
	//		}
	//		
	//		// Create a new bid and associate it with the article
	//		Bid bid = new Bid(System.currentTimeMillis(), amount, article, user);
	//		article.getBids().add(bid);
	//	}


	// Method to add a new Contenedor
	public void addContenedor(Contenedor contenedor) {
		if (contenedor != null) {
			contenedorRepository.put(contenedor.getId(), contenedor);
		}
	}
	// Methodo make contenedor
	public void makeContenedor(User user, long contenedorId, int codigoPostal, float capacidad) {
	    Contenedor contenedor = contenedorRepository.get(contenedorId);

	    if (contenedor != null) {
	        throw new RuntimeException("Contenedor already exists");
	    }
	    
	    // Aquí puedes agregar más validaciones y lanzar excepciones con los mensajes correspondientes
	    if (codigoPostal <= 0 || capacidad <= 0) {
	        throw new RuntimeException("Invalid parameters");
	    }

	    Contenedor cont = new Contenedor(contenedorId, codigoPostal, capacidad, Llenado.VERDE, System.currentTimeMillis());
	    contenedorRepository.put(cont.getId(), cont);
	}

	// Method to add a new PlantaReciclaje
	public void addPlantaReciclaje(PlantaReciclaje planta) {
		if (planta != null) {
			plantaReciclajeRepository.put(planta.getId(), planta);
		}
	}
}