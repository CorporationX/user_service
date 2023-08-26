package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<UserDto> getMentees(Long userId) {
        return mentorshipService.getMentees(userId);
    }

    public List<UserDto> getMentors(Long userId) {
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(Long mentorId, Long menteeId) {
        mentorshipService.deleteMentor(mentorId, menteeId);
    }
}
