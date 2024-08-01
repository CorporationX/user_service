package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipStartEvent {

    private Long menteeId;

    private Long mentorId;
}
