package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Validated
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<UserDto> getMentees(@Positive long userId) {
        return mentorshipService.getMentees(userId);
    }

    public List<UserDto> getMentors(@Positive long userId) {
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(@Positive long menteeId, @Positive long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(@Positive long menteeId, @Positive long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
