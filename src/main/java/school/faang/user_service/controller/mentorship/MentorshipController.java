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

    public List<UserDto> getMentees(Long userId) {
        if (userId == null) {
            throw new NullPointerException("User id " + userId + " is null");
        }
        return mentorshipService.getMentees(userId);
    }

    public List<UserDto> getMentors(Long userId) {
        if (userId == null) {
            throw new NullPointerException("User id " + userId + " is null");
        }
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        if (menteeId == null) {
            throw new NullPointerException("Mentee id: " + menteeId + " is null");
        }
        if (mentorId == null) {
            throw new NullPointerException("Mentor id: " + mentorId + " is null");
        }
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        if (menteeId == null) {
            throw new NullPointerException("Mentee id: " + menteeId + " is null");
        }
        if (mentorId == null) {
            throw new NullPointerException("Mentor id: " + mentorId + " is null");
        }
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
