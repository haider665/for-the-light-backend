package For.the.light.service;

import For.the.light.dto.IncidentDTO;
import For.the.light.dto.IncidentResponseDTO;
import For.the.light.dto.LocationDTO;
import For.the.light.entity.Incident;
import For.the.light.entity.IncidentStatus;
import For.the.light.entity.Location;
import For.the.light.entity.User;
import For.the.light.repository.IncidentRepository;
import For.the.light.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;

    public IncidentService(IncidentRepository incidentRepository, UserRepository userRepository) {
        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createIncident(IncidentDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Incident incident = new Incident();
        incident.setTitle(dto.getTitle());
        incident.setDescription(dto.getDescription());
        incident.setStatus(IncidentStatus.DRAFT);
        incident.setUser(user);

        if (dto.getLocation() != null) {
            Location location = new Location(
                    dto.getLocation().getDivision(),
                    dto.getLocation().getDistrict(),
                    dto.getLocation().getUpazila(),
                    dto.getLocation().getLat(),
                    dto.getLocation().getLng());
            incident.setLocation(location);
        }

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            incident.setImages(dto.getImages());
        }

        incidentRepository.save(incident);
    }

    @Transactional(readOnly = true)
    public List<IncidentResponseDTO> getUserIncidents(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Incident> incidents = incidentRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        return incidents.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IncidentResponseDTO getIncidentById(Long id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        // Verify the incident belongs to the user
        if (!incident.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return convertToResponseDTO(incident);
    }

    @Transactional(readOnly = true)
    public List<IncidentResponseDTO> getAllIncidents() {
        List<Incident> incidents = incidentRepository.findAllByOrderByCreatedAtDesc();

        return incidents.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncidentResponseDTO> getAllAvailableIncidents() {
        List<Incident> incidents = incidentRepository.findAllByStatusNotIn(
                List.of(IncidentStatus.DRAFT, IncidentStatus.REJECTED)
        );

        return incidents.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IncidentResponseDTO getPublicIncidentById(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        return convertToResponseDTO(incident);
    }

    @Transactional
    public IncidentResponseDTO updateIncidentStatus(Long id, IncidentStatus status) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        incident.setStatus(status);
        incidentRepository.save(incident);

        return convertToResponseDTO(incident);
    }

    @Transactional
    public IncidentResponseDTO updateIncident(Long id, IncidentDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        if (!incident.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        incident.setTitle(dto.getTitle());
        incident.setDescription(dto.getDescription());

        if (dto.getLocation() != null) {
            Location location = new Location(
                    dto.getLocation().getDivision(),
                    dto.getLocation().getDistrict(),
                    dto.getLocation().getUpazila(),
                    dto.getLocation().getLat(),
                    dto.getLocation().getLng());
            incident.setLocation(location);
        }

        if (dto.getImages() != null) {
            incident.setImages(dto.getImages());
        }

        incidentRepository.save(incident);

        return convertToResponseDTO(incident);
    }

    @Transactional
    public IncidentResponseDTO updateIncidentStatusByUser(Long id, IncidentStatus status, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        if (!incident.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        incident.setStatus(status);
        incidentRepository.save(incident);

        return convertToResponseDTO(incident);
    }

    private IncidentResponseDTO convertToResponseDTO(Incident incident) {
        IncidentResponseDTO dto = new IncidentResponseDTO();
        dto.setId(incident.getId());
        dto.setTitle(incident.getTitle());
        dto.setDescription(incident.getDescription());
        dto.setStatus(incident.getStatus());
        dto.setImages(incident.getImages());
        dto.setUserId(incident.getUser().getId());
        dto.setUserName(incident.getUser().getName());
        dto.setCreatedAt(incident.getCreatedAt());
        dto.setUpdatedAt(incident.getUpdatedAt());

        if (incident.getLocation() != null) {
            LocationDTO locationDTO = new LocationDTO(
                    incident.getLocation().getDivision(),
                    incident.getLocation().getDistrict(),
                    incident.getLocation().getUpazila(),
                    incident.getLocation().getLat(),
                    incident.getLocation().getLng());
            dto.setLocation(locationDTO);
        }

        return dto;
    }
}