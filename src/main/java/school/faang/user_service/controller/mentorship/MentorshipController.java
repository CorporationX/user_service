package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<UserDto> getMentees(Long userId) {
        validationForNullUserId(userId);
        return mentorshipService.getMentees(userId);
    }

    public List<UserDto> getMentors(Long userId) {
        validationForNullUserId(userId);
        return mentorshipService.getMentors(userId);
    }

    public void removeMentorsMentee(Long mentorId, Long menteeId) {
        validationForNullMentorAndMenteeIds(mentorId, menteeId);
        mentorshipService.removeMentorsMentee(mentorId, menteeId);

    }

    private static void validationForNullUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must be a not null value");
        }
    }

    private static void validationForNullMentorAndMenteeIds(Long mentorId, Long menteeId) {
        if (mentorId == null || menteeId == null) {
            throw new IllegalArgumentException("mentorId and menteeId must be a not null value");
        }
    }
}
