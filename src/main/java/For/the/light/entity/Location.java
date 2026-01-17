package For.the.light.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Location {

    private String division;
    private String district;
    private String upazila;
    private Double lat;
    private Double lng;

    public Location() {}

    public Location(String division, String district, String upazila, Double lat, Double lng) {
        this.division = division;
        this.district = district;
        this.upazila = upazila;
        this.lat = lat;
        this.lng = lng;
    }
}