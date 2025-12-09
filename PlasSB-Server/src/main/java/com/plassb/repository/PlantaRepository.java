package com.plassb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.plassb.entity.Planta;

@Repository
public interface PlantaRepository extends JpaRepository<Planta, Long> {
    // Métodos personalizados si son necesarios
    Planta findByNombre(String nombre);
}