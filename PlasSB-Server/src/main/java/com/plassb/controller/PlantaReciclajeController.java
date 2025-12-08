package com.plassb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.plassb.dto.CapacidadResponse;
import com.plassb.dto.RecepcionContenedorRequest;
import com.plassb.dto.RecepcionContenedorResponse;
import com.plassb.dto.RegistroPlantaRequest;
import com.plassb.service.PlantaReciclajeService;

@RestController
@RequestMapping("/api/plassb")
public class PlantaReciclajeController {
    
    private final PlantaReciclajeService plantaService;
    
    public PlantaReciclajeController(PlantaReciclajeService plantaService) {
        this.plantaService = plantaService;
    }
    
    @PostMapping("/plantas/registrar")
    public ResponseEntity<String> registrarPlanta(@RequestBody RegistroPlantaRequest request) {
        try {
            plantaService.registrarPlanta(
                request.getId(), 
                request.getNombre(), 
                request.getCapacidadTotal()
            );
            return ResponseEntity.ok("{\"mensaje\":\"Planta registrada exitosamente\",\"id\":" + request.getId() + "}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"Error registrando planta: " + e.getMessage() + "\"}");
        }
    }
    
    @GetMapping("/plantas/{plantaId}/capacidad")
    public ResponseEntity<CapacidadResponse> consultarCapacidad(@PathVariable("plantaId") Long plantaId) {

        try {
            CapacidadResponse capacidad = plantaService.consultarCapacidadDisponible(plantaId);
            return ResponseEntity.ok(capacidad);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/plantas/{plantaId}/contenedores")
    public ResponseEntity<RecepcionContenedorResponse> recibirContenedor(
            @PathVariable("plantaId") Long plantaId,  // ← Añade esto
            @RequestBody RecepcionContenedorRequest request) {
        try {
            RecepcionContenedorResponse response = plantaService.recibirContenedor(plantaId, request);
            if (response.isAceptado()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/plantas/{plantaId}/estado")
    public ResponseEntity<String> obtenerEstado(@PathVariable Long plantaId) {
        try {
            String estado = plantaService.obtenerEstado(plantaId);
            return ResponseEntity.ok("{\"estado\":\"" + estado + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Endpoint para pruebas - resetear capacidad
    @PostMapping("/plantas/{plantaId}/reset")
    public ResponseEntity<String> resetearCapacidad(@PathVariable Long plantaId) {
        try {
            plantaService.resetearCapacidad(plantaId);
            return ResponseEntity.ok("{\"mensaje\":\"Capacidad reseteada\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Endpoint de salud
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{\"status\":\"OK\",\"service\":\"PlasSB\",\"timestamp\":\"" + 
                new java.util.Date() + "\"}");
    }
}