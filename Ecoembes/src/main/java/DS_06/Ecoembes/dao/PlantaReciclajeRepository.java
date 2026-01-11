package DS_06.Ecoembes.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import DS_06.Ecoembes.entity.PlantaReciclaje;

@Repository
public interface PlantaReciclajeRepository extends JpaRepository<PlantaReciclaje, Long> {
	List<PlantaReciclaje> findByNombre(String nombre);

}
