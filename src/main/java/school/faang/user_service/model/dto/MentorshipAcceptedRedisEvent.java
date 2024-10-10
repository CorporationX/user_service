package school.faang.user_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorshipAcceptedRedisEvent {
    Long requestId;
    Long requesterId;
    Long mentorId;
}
