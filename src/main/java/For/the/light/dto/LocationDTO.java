package For.the.light.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDTO {

    @NotBlank(message = "Division is required")
    private String division;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Upazila is required")
    private String upazila;

    @NotNull(message = "Latitude is required")
    private Double lat;

    @NotNull(message = "Longitude is required")
    private Double lng;

    // Constructors
    public LocationDTO() {}

    public LocationDTO(String division, String district, String upazila, Double lat, Double lng) {
        this.division = division;
        this.district = district;
        this.upazila = upazila;
        this.lat = lat;
        this.lng = lng;
    }
}