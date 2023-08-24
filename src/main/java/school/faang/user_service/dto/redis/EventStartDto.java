package school.faang.user_service.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EventStartDto {
    private Long eventId;
    private List<Long> userIds;
}
