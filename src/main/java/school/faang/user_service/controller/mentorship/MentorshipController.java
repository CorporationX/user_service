package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    public List<UserDto> getMentees(Long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    public List<UserDto> getMentors(Long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
