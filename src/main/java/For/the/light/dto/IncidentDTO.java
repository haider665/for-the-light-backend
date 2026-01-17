package For.the.light.dto;

import For.the.light.entity.IncidentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class IncidentDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    private String description;

    private String status;

    @Valid
    @NotNull(message = "Location is required")
    private LocationDTO location;

    @Size(max = 10, message = "Maximum 10 images allowed")
    private List<String> images = new ArrayList<>();

    // Constructors
    public IncidentDTO() {
    }

    public IncidentDTO(String title, String description, String status, LocationDTO location, List<String> images) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.location = location;
        this.images = images;
    }
}