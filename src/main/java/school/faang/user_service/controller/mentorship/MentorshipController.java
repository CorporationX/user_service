package school.faang.user_service.controller.mentorship;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/mentorships")
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;
    private final UserContext userContext;

    @PostMapping
    public void addMentorship(
            @RequestParam @Min(1) long mentorId,
            @RequestParam @Min(1) long menteeId
    ) {
        ensureUserIsPartOfMentorship(userContext.getUserId(), mentorId, menteeId);
        ensureUserIsNotSelfMentorOrMentee(mentorId, menteeId);

        mentorshipService.addMentorship(mentorId, menteeId);
    }

    @GetMapping("/mentor/{mentorId}/mentees")
    public List<UserDto> getMentees(@PathVariable @Min(1) long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/mentee/{menteeId}/mentors")
    public List<UserDto> getMentors(@PathVariable @Min(1) long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping
    public void deleteMentorship(
            @RequestParam @Min(1) long menteeId,
            @RequestParam @Min(1) long mentorId
    ) {
        ensureUserIsPartOfMentorship(userContext.getUserId(), mentorId, menteeId);
        ensureUserIsNotSelfMentorOrMentee(mentorId, menteeId);

        mentorshipService.deleteMentorship(menteeId, mentorId);
    }

    private void ensureUserIsPartOfMentorship(long userId, long mentorId, long menteeId) {
        if (userId != mentorId && userId != menteeId) {
            throw new ForbiddenException("Пользователь не является частью этой связи...");
        }
    }

    private void ensureUserIsNotSelfMentorOrMentee(long mentorId, long menteeId) {
        if (mentorId == menteeId) {
            throw new DataValidationException("Пользователь не может быть ментором/менти для самого себя...");
        }
    }
}