package DS_06.Ecoembes.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import DS_06.Ecoembes.entity.Contenedor;

@Repository
public interface ContenedorRepository extends JpaRepository<Contenedor, Long> {
	List<Contenedor> findById(int id);
}
