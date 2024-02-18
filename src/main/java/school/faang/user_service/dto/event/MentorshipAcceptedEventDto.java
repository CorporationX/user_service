package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MentorshipAcceptedEventDto {
    private long id;
    private long requesterId;
    private long receiverId;
}