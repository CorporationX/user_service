package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<UserDto> getMentees(long userId) {
        validateIdCorrect(userId);
        return mentorshipService.getMentees(userId);
    }

    public List<UserDto> getMentors(long userId) {
        validateIdCorrect(userId);
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        validateIdCorrect(menteeId);
        validateIdCorrect(mentorId);
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long mentorId, long menteeId) {
        validateIdCorrect(menteeId);
        validateIdCorrect(mentorId);
        mentorshipService.deleteMentor(mentorId, menteeId);
    }

    private void validateIdCorrect(long id) {
        if (id <= 0) {
            throw new DataValidationException("Incorrect id");
        }
    }
}
