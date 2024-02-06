package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<UserDto> getMentees(long userId) {
        if (isIdValid(userId))
            return mentorshipService.getMentees(userId);
        else
            throw new IllegalArgumentException("Incorrect id entered");
    }

    public List<UserDto> getMentors(long userId) {
        if (isIdValid(userId))
            return mentorshipService.getMentors(userId);
        else
            throw new IllegalArgumentException("Incorrect id entered");
    }

    public void deleteMentee(long menteeId, long mentorId) {
        if (isIdValid(menteeId) && isIdValid(mentorId))
            mentorshipService.deleteMentee(menteeId, mentorId);
        else
            throw new IllegalArgumentException("Incorrect id entered");
    }

    public void deleteMentor(long menteeId, long mentorId) {
        if (isIdValid(menteeId) && isIdValid(mentorId))
            mentorshipService.deleteMentor(menteeId, mentorId);
        else
            throw new IllegalArgumentException("Incorrect id entered");
    }

    public boolean isIdValid(long id) {
        return id >= 1;
    }
}
