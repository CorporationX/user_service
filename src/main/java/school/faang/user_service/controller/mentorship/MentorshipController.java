package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.MentorshipService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    public List<User> getMentees(Long userId) {
        return mentorshipService.getMentees(userId);

    }

    public List<User> getMentors(Long userId) {
        return mentorshipService.getMentors(userId);
    }
}
