package school.faang.user_service.dto.event;

import lombok.Data;

@Data
public class EventFilterDto {
    private String titlePattern;
    private Long ownerId;
}
