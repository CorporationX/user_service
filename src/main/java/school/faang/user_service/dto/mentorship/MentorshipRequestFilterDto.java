package school.faang.user_service.dto.mentorship;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MentorshipRequestFilterDto {
    private String description;
    private Long requesterId;
    private Long receiverId;
}
