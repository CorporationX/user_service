package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.exception.mentorship.InvalidIdException;
import school.faang.user_service.message.mentorship.MentorshipMessage;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MentorshipController {
    private static final String INVALID_ID_MESSAGE = "Invalid ID: ID not be less than 1";

    private final MentorshipService mentorshipService;

    public List<MenteeDto> getMentees(long userId) {
        if (isIdValid(userId)) {
            return mentorshipService.getMentees(userId);
        }
        throw new InvalidIdException(INVALID_ID_MESSAGE);
    }

    public List<MentorDto> getMentors(long userId) {
        if (isIdValid(userId)) {
            return mentorshipService.getMentors(userId);
        }
        throw new InvalidIdException(INVALID_ID_MESSAGE);
    }

    private boolean isIdValid(long id) {
        if (id < 1) {
            log.error(MentorshipMessage.INVALID_ID.getMessage(), id);
            return false;
        }
        return true;
    }
}
