package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import school.faang.user_service.entity.User;

@Getter
@AllArgsConstructor
public class PairMentorshipDto {
    private User requester;
    private User receiver;
}
