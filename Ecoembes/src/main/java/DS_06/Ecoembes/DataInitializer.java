package DS_06.Ecoembes;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import DS_06.Ecoembes.entity.Contenedor;
import DS_06.Ecoembes.entity.Llenado;
import DS_06.Ecoembes.entity.PlantaReciclaje;
import DS_06.Ecoembes.entity.User;
import DS_06.Ecoembes.service.AuthService;
import DS_06.Ecoembes.service.ReciclajeService;

@Configuration
public class DataInitializer {



	private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);


    @Bean
    CommandLineRunner initData(ReciclajeService reciclajeService, AuthService authService) {
		return args -> {
			// Create some users
			User reciclador1 = new User("EcoJuan", "juan.reciclaje@eco.com", "EcoJ@n123!");
			User reciclador2 = new User("VerdeMaria", "maria.verde@eco.com", "V3rd3M@r1@!");
			User reciclador3 = new User("BioCarlos", "carlos.bio@eco.com", "B1oC@rlos!");
			User reciclador4 = new User("EcoSofia", "sofia.eco@eco.com", "3coS0f1@!");
			User reciclador5 = new User("RecicladorLuis", "luis.recicla@eco.com", "Lui5R3c1cl@!");
			User reciclador6 = new User("AmbientalistaAna", "ana.ambiente@eco.com", "4n4Amb1ent3!");

			authService.addUser(reciclador1);
			authService.addUser(reciclador2);
			authService.addUser(reciclador3);
			authService.addUser(reciclador4);
			authService.addUser(reciclador5);
			authService.addUser(reciclador6);

			logger.info("Users saved!");
			long emptyingDate = System.currentTimeMillis();
			Contenedor contenedor1 = new Contenedor(0, 28001, 1000.0f, Llenado.NARANJA, 0);
			Contenedor contenedor2 = new Contenedor(1, 28002, 800.0f, Llenado.ROJO, emptyingDate);
			Contenedor contenedor3 = new Contenedor(2, 28003, 1200.0f, Llenado.VERDE, emptyingDate);
			Contenedor contenedor4 = new Contenedor(3, 28004, 900.0f, Llenado.NARANJA, emptyingDate);

			Contenedor contenedor5 = new Contenedor(4, 28005, 750.0f, Llenado.ROJO, emptyingDate);
			Contenedor contenedor6 = new Contenedor(5, 28006, 1100.0f, Llenado.NARANJA, emptyingDate);
			Contenedor contenedor7 = new Contenedor(6, 28007, 950.0f, Llenado.VERDE, emptyingDate);
			
			Contenedor contenedor8 = new Contenedor(7, 28008, 850.0f, Llenado.ROJO, emptyingDate);
			Contenedor contenedor9 = new Contenedor(8, 28009, 1000.0f, Llenado.NARANJA, emptyingDate);
			Contenedor contenedor10 = new Contenedor(9, 28010, 700.0f, Llenado.VERDE, emptyingDate);
			Contenedor contenedor11 = new Contenedor(10, 28011, 1300.0f, Llenado.ROJO, emptyingDate);
			Contenedor contenedor12 = new Contenedor(11, 28012, 600.0f, Llenado.NARANJA, emptyingDate);
			
			ArrayList<Contenedor> listaContenedoresNorte = new ArrayList<>();
			listaContenedoresNorte.add(contenedor1);
			listaContenedoresNorte.add(contenedor2);
			listaContenedoresNorte.add(contenedor5);
			listaContenedoresNorte.add(contenedor8);

			ArrayList<Contenedor> listaContenedoresSur = new ArrayList<>();
			listaContenedoresSur.add(contenedor3);
			listaContenedoresSur.add(contenedor4);
			listaContenedoresSur.add(contenedor6);
			listaContenedoresSur.add(contenedor9);
			listaContenedoresSur.add(contenedor10);

			ArrayList<Contenedor> listaContenedoresEste = new ArrayList<>();
			listaContenedoresEste.add(contenedor7);
			listaContenedoresEste.add(contenedor11);
			listaContenedoresEste.add(contenedor12);
			
			
			PlantaReciclaje plantaNorte = new PlantaReciclaje(1, "PlasSB Ltd.", listaContenedoresNorte);
			PlantaReciclaje plantaSur = new PlantaReciclaje(2, "ContSocket Ltd.", listaContenedoresSur);
			// Add containers to recycling service
			reciclajeService.addContenedor(contenedor1);
			reciclajeService.addContenedor(contenedor2);
			reciclajeService.addContenedor(contenedor3);
			reciclajeService.addContenedor(contenedor4);
			reciclajeService.addContenedor(contenedor5);
			reciclajeService.addContenedor(contenedor6);
			reciclajeService.addContenedor(contenedor7);
			reciclajeService.addContenedor(contenedor8);
			reciclajeService.addContenedor(contenedor9);
			reciclajeService.addContenedor(contenedor10);
			reciclajeService.addContenedor(contenedor11);
			reciclajeService.addContenedor(contenedor12);

			// Add recycling plants to service
			reciclajeService.addPlantaReciclaje(plantaNorte);
			reciclajeService.addPlantaReciclaje(plantaSur);

			logger.info("Containers and recycling plants saved!");
		};

		}
    }
