package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MentorshipRequestDto {
    private long id;
    private String description;
    private long requesterId;
    private long receiverId;
}
