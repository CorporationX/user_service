package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MentorshipRequestDto {
    private final String description;
    private final Long userId;
    private final Long mentorId;
}
