/**
 * This code is based on solutions provided by ChatGPT 4o and 
 * adapted using GitHub Copilot. It has been thoroughly reviewed 
 * and validated to ensure correctness and that it is free of errors.
 */
package DS_06.Ecoembes;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import DS_06.Ecoembes.dao.ContenedorRepository;
import DS_06.Ecoembes.dao.PlantaReciclajeRepository;
import DS_06.Ecoembes.dao.UserRepository;
import DS_06.Ecoembes.entity.Contenedor;
import DS_06.Ecoembes.entity.Llenado;
import DS_06.Ecoembes.entity.PlantaReciclaje;
import DS_06.Ecoembes.entity.User;
import DS_06.Ecoembes.external.PlantaExternaInitializer;
import DS_06.Ecoembes.service.AuthService;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Bean
    @Transactional
    CommandLineRunner initData(UserRepository userRepository, 
                               ContenedorRepository contenedorRepository, 
                               PlantaReciclajeRepository plantaReciclajeRepository,
                               AuthService authService,
                               PlantaExternaInitializer plantaExternaInitializer) {  // NUEVO
        return args -> {
            // Database is already initialized
            if (userRepository.count() > 0) {                
                logger.info("Database already initialized. Skipping data initialization.");
                return;
            }			
            
            logger.info("Initializing database with sample data...");
            
            // Create some users
            User reciclador1 = new User("EcoJuan", "juan.reciclaje@eco.com", "EcoJ@n123!");
            User reciclador2 = new User("VerdeMaria", "maria.verde@eco.com", "V3rd3M@r1@!");
            User reciclador3 = new User("BioCarlos", "carlos.bio@eco.com", "B1oC@rlos!");
            User reciclador4 = new User("EcoSofia", "sofia.eco@eco.com", "3coS0f1@!");
            User reciclador5 = new User("RecicladorLuis", "luis.recicla@eco.com", "Lui5R3c1cl@!");
            User reciclador6 = new User("AmbientalistaAna", "ana.ambiente@eco.com", "4n4Amb1ent3!");
            User recicladorAdmin = new User("admin", "admin@ecoembes.com", "admin");

            // Save users using authService
            authService.addUser(reciclador1);
            authService.addUser(reciclador2);
            authService.addUser(reciclador3);
            authService.addUser(reciclador4);
            authService.addUser(reciclador5);
            authService.addUser(reciclador6);
            authService.addUser(recicladorAdmin);
            
            logger.info("{} users saved!", userRepository.count());
            
            // Create containers with future emptying dates (30 days from now)
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 30);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);			
            
            Date emptyingDate = calendar.getTime();
            
            // Create containers (DO NOT SAVE THEM YET)
            Contenedor contenedor1 = new Contenedor(28001, 1000.0f, Llenado.AMARILLO, emptyingDate);
            Contenedor contenedor2 = new Contenedor(28002, 800.0f, Llenado.ROJO, emptyingDate);
            Contenedor contenedor3 = new Contenedor(28003, 1200.0f, Llenado.VERDE, emptyingDate);
            Contenedor contenedor4 = new Contenedor(28004, 900.0f, Llenado.AMARILLO, emptyingDate);
            Contenedor contenedor5 = new Contenedor(28005, 750.0f, Llenado.ROJO, emptyingDate);
            Contenedor contenedor6 = new Contenedor(28006, 1100.0f, Llenado.AMARILLO, emptyingDate);
            Contenedor contenedor7 = new Contenedor(28007, 950.0f, Llenado.VERDE, emptyingDate);
            Contenedor contenedor8 = new Contenedor(28008, 850.0f, Llenado.ROJO, emptyingDate);
            Contenedor contenedor9 = new Contenedor(28009, 1000.0f, Llenado.AMARILLO, emptyingDate);
            Contenedor contenedor10 = new Contenedor(28010, 700.0f, Llenado.VERDE, emptyingDate);
            Contenedor contenedor11 = new Contenedor(28011, 1300.0f, Llenado.ROJO, emptyingDate);
            Contenedor contenedor12 = new Contenedor(28012, 600.0f, Llenado.AMARILLO, emptyingDate);
            
            // Create recycling plants with specific types
            PlantaReciclaje plantaNorte = new PlantaReciclaje("PlasSB Ltd.", 40000, null);
            plantaNorte.setTipoPlanta("PLASSB");
            
            PlantaReciclaje plantaSur = new PlantaReciclaje("ContSocket Ltd.", 10000, null);  // CAMBIADO: capacidad ajustada
            plantaSur.setTipoPlanta("CONTSOCKET");
            
            PlantaReciclaje plantaEste = new PlantaReciclaje("EcoRecicla S.A.", 45000, null);
            plantaEste.setTipoPlanta("DESCONOCIDO");
            
            // Add containers to recycling plants
            plantaNorte.agregarContenedor(contenedor1);
            plantaNorte.agregarContenedor(contenedor2);
            plantaNorte.agregarContenedor(contenedor5);
            plantaNorte.agregarContenedor(contenedor8);
            
            plantaSur.agregarContenedor(contenedor3);
            plantaSur.agregarContenedor(contenedor4);
            plantaSur.agregarContenedor(contenedor6);
            plantaSur.agregarContenedor(contenedor9);
            plantaSur.agregarContenedor(contenedor10);
            
            plantaEste.agregarContenedor(contenedor7);
            plantaEste.agregarContenedor(contenedor11);
            plantaEste.agregarContenedor(contenedor12);
            
            // Save recycling plants (containers will be saved due to cascade)
            plantaReciclajeRepository.save(plantaNorte);
            plantaReciclajeRepository.save(plantaSur);
            plantaReciclajeRepository.save(plantaEste);
            
            // Assign users to containers
            reciclador1.agregarContenedor(contenedor1);
            reciclador2.agregarContenedor(contenedor2);
            reciclador3.agregarContenedor(contenedor3);
            reciclador4.agregarContenedor(contenedor4);
            reciclador5.agregarContenedor(contenedor5);
            reciclador6.agregarContenedor(contenedor6);
            reciclador1.agregarContenedor(contenedor7);
            reciclador2.agregarContenedor(contenedor8);
            reciclador3.agregarContenedor(contenedor9);
            reciclador4.agregarContenedor(contenedor10);
            reciclador5.agregarContenedor(contenedor11);
            reciclador6.agregarContenedor(contenedor12);
            
            // Update users in database
            userRepository.saveAll(List.of(reciclador1, reciclador2, reciclador3, reciclador4,
                                           reciclador5, reciclador6, recicladorAdmin));
            
            logger.info("Database initialization completed!");
            logger.info("Created {} recycling plants", plantaReciclajeRepository.count());
            logger.info("Created {} containers", contenedorRepository.count());
            logger.info("Plant types: PlasSB={}, ContSocket={}, Desconocido={}", 
                plantaNorte.getTipoPlanta(), plantaSur.getTipoPlanta(), plantaEste.getTipoPlanta());
            
            // NUEVO: Registrar plantas en servidores externos
            logger.info("\n");
            plantaExternaInitializer.registrarPlantasExternas();
        };
    }
}