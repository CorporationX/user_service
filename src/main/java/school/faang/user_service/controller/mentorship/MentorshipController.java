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
        if (isIdValid(userId)) {
            return mentorshipService.getMentees(userId);
        }

        throw new InvalidIdException(ExceptionMessage.INVALID_ID.getMessage());
    }

    public List<Long> getMentors(long userId) {
        if (isIdValid(userId)) {
            return mentorshipService.getMentors(userId);
        }

        throw new InvalidIdException(ExceptionMessage.INVALID_ID.getMessage());
    }

    public void deleteMentee(long menteeId, long mentorId) {
        if (isIdValid(menteeId) && isIdValid(mentorId)) {
            mentorshipService.deleteMentee(menteeId, mentorId);
            return;
        }

        throw new InvalidIdException(ExceptionMessage.INVALID_ID.getMessage());
    }

    public void deleteMentor(long menteeId, long mentorId) {
        if (isIdValid(menteeId) && isIdValid(mentorId)) {
            mentorshipService.deleteMentor(menteeId, mentorId);
            return;
        }

        throw new InvalidIdException(ExceptionMessage.INVALID_ID.getMessage());
    }

    private boolean isIdValid(long id) {
        if (id < 1) {
            log.error(MentorshipMessage.INVALID_ID.getMessage(), id);
            return false;
        }

        return true;
    }
}
