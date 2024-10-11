package school.faang.user_service.service.publisher;

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
    private Long id;
    private long receiverId;
    private long actorId;
    private LocalDateTime receivedAt;
}
