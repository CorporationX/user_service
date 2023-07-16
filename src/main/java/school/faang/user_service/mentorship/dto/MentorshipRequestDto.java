package school.faang.user_service.mentorship.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import school.faang.user_service.entity.User;

@Getter
@AllArgsConstructor
public class MentorshipRequestDto {
    private long id;
    private String description;
    private User requester;
    private User receiver;
}
