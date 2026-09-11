package For.the.light.iot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateIotDeviceRequestDto {

    @NotBlank(message = "deviceId is required")
    private String deviceId;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "apiKey is required")
    private String apiKey;
}
