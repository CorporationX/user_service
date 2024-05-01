package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<User> getMentees(Long userId){
        return mentorshipService.getMentees(userId);
    }
}
