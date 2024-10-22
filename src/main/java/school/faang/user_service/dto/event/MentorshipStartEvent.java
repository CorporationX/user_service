package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Builder
@ToString
public class MentorshipStartEvent implements Serializable {
    private Long mentorId;
    private Long menteeId;
}
