package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.exception.mentorship.InvalidIdException;
import school.faang.user_service.message.mentorship.ExceptionMessage;
import school.faang.user_service.message.mentorship.MentorshipMessage;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<Long> getMentees(long userId) {
        validateId(userId);

        return mentorshipService.getMentees(userId);
    }

    public List<Long> getMentors(long userId) {
        validateId(userId);

        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        validateId(menteeId);
        validateId(mentorId);

        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        validateId(menteeId);
        validateId(mentorId);

        mentorshipService.deleteMentor(menteeId, mentorId);
    }

    private void validateId(long id) {
        if (id < 1) {
            log.error(MentorshipMessage.INVALID_ID.getMessage(), id);
            throw new InvalidIdException(ExceptionMessage.INVALID_ID.getMessage());
        }
    }
}
