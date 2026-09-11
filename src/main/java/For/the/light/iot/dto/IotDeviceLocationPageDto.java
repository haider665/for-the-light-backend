package For.the.light.iot.dto;

import lombok.Data;

import java.util.List;

@Data
public class IotDeviceLocationPageDto {
    private String deviceId;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private List<IotDeviceLocationResponseDto> items;
}
