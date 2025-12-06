package DS_06.Ecoembes.dao;

import org.springframework.stereotype.Repository;

import DS_06.Ecoembes.entity.Contenedor;
import DS_06.Ecoembes.entity.PlantaReciclaje;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PlantaReciclajeRepository extends JpaRepository<PlantaReciclaje, Long> {
	List<PlantaReciclaje> findByNombre(String nombre);

}
