package school.faang.user_service.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SearchAppearanceEvent {
    private long receiverId;
    private long actorId;
    private LocalDateTime receivedAt;
}
