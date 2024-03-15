package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.userDto.UserDto;


import java.util.List;

@RequiredArgsConstructor
@Controller
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<UserDto> getMentees(long userId) {
        return mentorshipService.getMentees(userId);
    }

    public List<UserDto> getMentors(long userId) {
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
