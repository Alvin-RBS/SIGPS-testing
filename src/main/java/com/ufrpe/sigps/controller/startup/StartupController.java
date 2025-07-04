// src/main/java/com/ufrpe/sigps/controller/startup/StartupController.java
package com.ufrpe.sigps.controller.startup;

import com.ufrpe.sigps.dto.StartupDto;
import com.ufrpe.sigps.service.startup.StartupService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/startups")
public class StartupController {

    private final StartupService startupService;

    public StartupController(StartupService startupService) {
        this.startupService = startupService;
    }

    /**
     * Endpoint para criar uma nova Startup.
     * Recebe um StartupDto no corpo da requisição.
     * POST /api/startups
    */
    @PostMapping
    public ResponseEntity<StartupDto> createStartup(@Valid @RequestBody StartupDto startupDto) {
        StartupDto createdStartup = startupService.createStartup(startupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStartup);
    }

    /**
     * Endpoint para buscar uma Startup pelo ID.
     * GET /api/startups/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<StartupDto> getStartupById(@PathVariable Long id) {
        try {
            StartupDto startupDto = startupService.getStartupById(id);
            return ResponseEntity.ok(startupDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Endpoint para listar todas as Startups.
     * GET /api/startups
     */
    @GetMapping
    public ResponseEntity<List<StartupDto>> getAllStartups() {
        List<StartupDto> startups = startupService.getAllStartups();
        return ResponseEntity.ok(startups);
    }

    /**
     * Endpoint para atualizar uma Startup existente.
     * PUT /api/startups/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<StartupDto> updateStartup(
            @PathVariable Long id,
            @Valid @RequestBody StartupDto startupDto) {
        try {
            StartupDto updatedStartup = startupService.updateStartup(id, startupDto);
            return ResponseEntity.ok(updatedStartup);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Endpoint para deletar uma Startup pelo ID.
     * DELETE /api/startups/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStartup(@PathVariable Long id) {
        try {
            startupService.deleteStartup(id);
            return ResponseEntity.noContent().build(); // Status: 204 No Content
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}