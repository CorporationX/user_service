package school.faang.user_service.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EventFilterDto {
    private String titlePattern;
    private Long ownerId;
}
