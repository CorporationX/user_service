package school.faang.user_service.dto.redis;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchAppearanceEvent extends AnalyticsEvent {
    public SearchAppearanceEvent(long receiverId, long actorId, LocalDateTime receivedAt) {
        super(receiverId, actorId, receivedAt);
    }
}
