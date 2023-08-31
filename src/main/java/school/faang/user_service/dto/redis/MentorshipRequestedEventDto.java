package school.faang.user_service.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MentorshipRequestedEventDto {
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
}
