package For.the.light.controller;

import For.the.light.dto.IncidentDTO;
import For.the.light.dto.IncidentResponseDTO;
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
    public ResponseEntity<List<IncidentResponseDTO>> getAllIncidents() {
        List<IncidentResponseDTO> incidents = incidentService.getAllIncidents();
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<IncidentResponseDTO> getPublicIncidentById(@PathVariable Long id) {
        IncidentResponseDTO incident = incidentService.getPublicIncidentById(id);
        return ResponseEntity.ok(incident);
    }
}