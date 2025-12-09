package com.plassb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.plassb.entity.ContenedorExterno;

@Repository
public interface ContenedorExternoRepository extends JpaRepository<ContenedorExterno, Long> {
    ContenedorExterno findByIdExterno(Long idExterno);
}