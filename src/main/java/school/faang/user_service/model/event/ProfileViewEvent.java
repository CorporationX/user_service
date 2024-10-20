package school.faang.user_service.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileViewEvent {
    private Long receiverId;
    private Long actorId;
    private LocalDateTime receivedAt;
}