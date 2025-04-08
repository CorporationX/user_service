package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipUserDto;
import school.faang.user_service.exception.mentorship.InvalidIdException;
import school.faang.user_service.message.mentorship.ExceptionMessage;
import school.faang.user_service.message.mentorship.MentorshipMessage;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/{userId}/mentees")
    public List<MentorshipUserDto> getMentees(@PathVariable long userId) {
        validateId(userId);

        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/{userId}/mentors")
    public List<MentorshipUserDto> getMentors(@PathVariable long userId) {
        validateId(userId);

        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/{mentorId}/mentees/{menteeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentee(@PathVariable long menteeId, @PathVariable long mentorId) {
        validateId(menteeId);
        validateId(mentorId);

        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/{menteeId}/mentors/{mentorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentor(@PathVariable long menteeId, @PathVariable long mentorId) {
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
