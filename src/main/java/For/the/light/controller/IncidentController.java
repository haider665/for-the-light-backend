package For.the.light.controller;

import For.the.light.dto.IncidentDTO;
import For.the.light.dto.IncidentResponseDTO;
import For.the.light.dto.IncidentStatusUpdateDto;
import For.the.light.service.IncidentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/incident")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> createIncident(
            @Valid @RequestBody IncidentDTO dto,
            Authentication authentication) {

        log.info("New Incident: {}", dto);

        // JWT flow: principal is UserDetails with username=email
        String email = authentication.getName();
        incidentService.createIncident(dto, email);

        log.info("Incident created");

        return ResponseEntity.status(HttpStatus.CREATED).body("Success");
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<IncidentResponseDTO>> getUserIncidents(
            Authentication authentication) {

        // JWT flow: principal is UserDetails with username=email
        String email = authentication.getName();

        List<IncidentResponseDTO> incidents = incidentService.getUserIncidents(email);
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<IncidentResponseDTO> getIncidentById(
            @PathVariable Long id,
            Authentication authentication) {

        // JWT flow: principal is UserDetails with username=email
        String email = authentication.getName();

        IncidentResponseDTO incident = incidentService.getIncidentById(id, email);
        return ResponseEntity.ok(incident);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<List<IncidentResponseDTO>> getAllIncidents() {
        List<IncidentResponseDTO> incidents = incidentService.getAllIncidents();
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/available")
    public ResponseEntity<List<IncidentResponseDTO>> getAvailableIncidents() {
        List<IncidentResponseDTO> incidents = incidentService.getAllAvailableIncidents();
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<IncidentResponseDTO> getPublicIncidentById(@PathVariable Long id) {
        IncidentResponseDTO incident = incidentService.getPublicIncidentById(id);
        return ResponseEntity.ok(incident);
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<IncidentResponseDTO> updateIncidentStatus(
            @PathVariable Long id,
            @RequestBody IncidentStatusUpdateDto request) {
        log.info("Request to update incident {} status to {}", id, request.getStatus());
        IncidentResponseDTO updatedIncident = incidentService.updateIncidentStatus(id, request.getStatus());
        return ResponseEntity.ok(updatedIncident);
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<IncidentResponseDTO> updateIncident(
            @PathVariable Long id,
            @Valid @RequestBody IncidentDTO dto,
            Authentication authentication) {
        String email = authentication.getName();
        log.info("Request to update incident {} by user {}", id, email);
        IncidentResponseDTO updatedIncident = incidentService.updateIncident(id, dto, email);
        return ResponseEntity.ok(updatedIncident);
    }

    @PostMapping("/{id}/my-status")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<IncidentResponseDTO> updateIncidentStatusByUser(
            @PathVariable Long id,
            @RequestBody IncidentStatusUpdateDto request,
            Authentication authentication) {
        String email = authentication.getName();
        log.info("Request to update incident {} status to {} by user {}", id, request.getStatus(), email);
        IncidentResponseDTO updatedIncident = incidentService.updateIncidentStatusByUser(id, request.getStatus(),
                email);
        return ResponseEntity.ok(updatedIncident);
    }
}