package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class MentorshipRequestedEventDto {
    private long requesterId;
    private long receiverId;
    private LocalDateTime createdAt;
}
