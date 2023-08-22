package school.faang.user_service.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchAppearanceEventDto {
    private long actorId;
    private long receiverId;
    private LocalDateTime receivedAt;


}
