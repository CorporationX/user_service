package school.faang.user_service.dto;

import lombok.Data;

@Data
public class EventDto {
    private Long id;
    private String name;
    private String description;
    private String location;

}
