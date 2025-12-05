package DS_06.Ecoembes.dao;

import org.springframework.stereotype.Repository;

import DS_06.Ecoembes.entity.PlantaReciclaje;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PlantaReciclajeRepository extends JpaRepository<PlantaReciclaje, Long> {
	
}
