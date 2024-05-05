package school.faang.user_service.controller.mentorship;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<UserDto> getMentees(@NonNull Long userId) {
        return mentorshipService.getMentees(userId);
    }

    public List<UserDto> getMentors(@NonNull Long userId) {
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(@NonNull Long menteeId, @NonNull Long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(@NonNull Long menteeId, @NonNull Long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
