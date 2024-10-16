package school.faang.user_service.service.publisher.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchAppearanceEvent extends AnalyticsEvent {
    public SearchAppearanceEvent(Long id, long receiverId, long actorId, LocalDateTime receivedAt) {
        super(id, receiverId, actorId, receivedAt);
    }
}
