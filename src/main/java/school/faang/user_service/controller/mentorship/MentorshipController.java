package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.mentorship.MentorshipUserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

/**
 * @author Evgenii Malkov
 */
@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@Validated
public class MentorshipController {
    private static final String MENTEE = "/mentee";
    private static final String MENTOR = "/mentor";
    private final UserContext userContext;
    private final MentorshipService mentorshipService;

    @GetMapping(MENTEE)
    public List<MentorshipUserDto> getMentees(@RequestParam long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping(MENTOR)
    public List<MentorshipUserDto> getMentors(@RequestParam long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping(MENTEE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentee(@RequestParam long menteeId) {
        mentorshipService.deleteMentorshipRelations(
                userContext.getUserId(), menteeId);
    }

    @DeleteMapping(MENTOR)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentor(@RequestParam long mentorId) {
        mentorshipService.deleteMentorshipRelations(
                mentorId, userContext.getUserId());
    }

}
