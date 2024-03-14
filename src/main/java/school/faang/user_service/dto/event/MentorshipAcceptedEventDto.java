package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class MentorshipAcceptedEventDto {
    private long requesterId;
    private long receiverId;
    private LocalDateTime createdAt;
}