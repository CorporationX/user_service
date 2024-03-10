package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.userService.UserService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class MentorshipController {
    private final MentorshipService mentorshipService;
    private final UserService userService;

    public List<User> getMentees(long userId) {
        return mentorshipService.getMentees(userId);
    }

    public List<User> getMentors(long userId) {
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        userService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        userService.deleteMentor(menteeId, mentorId);
    }
}
