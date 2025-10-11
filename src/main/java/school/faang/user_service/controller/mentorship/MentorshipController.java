package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;
    private final UserContext userContext;

    @PostMapping("/{mentorId}/{menteeId}")
    public ResponseEntity<Void> addMentorship(@PathVariable long mentorId, @PathVariable long menteeId) {

        long currentUserId = userContext.getUserId();

        if (currentUserId != mentorId && currentUserId != menteeId) {
            log.warn("Access denied for userId={} trying to add mentorship between mentorId={} and menteeId={}",
                    currentUserId, mentorId, menteeId);
            throw new ForbiddenException("Доступ запрещен");
        }

        if (mentorId == menteeId) {
            log.warn("Invalid mentorship request: mentorId equals menteeId={}", mentorId);
            throw new DataValidationException("Вы не можете выбрать себя");
        }

        mentorshipService.addMentorship(mentorId, menteeId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{mentorId}/{menteeId}")
    public ResponseEntity<Void> deleteMentorship(@PathVariable long mentorId, @PathVariable long menteeId) {

        long currentUserId = userContext.getUserId();

        if (currentUserId != mentorId && currentUserId != menteeId) {
            log.warn("Access denied for userId={} trying to delete mentorship between mentorId={} and menteeId={}",
                    currentUserId, mentorId, menteeId);
            throw new ForbiddenException("Доступ запрещен");
        }

        if (mentorId == menteeId) {
            log.warn("Invalid delete mentorship request: mentorId equals menteeId={}", mentorId);
            throw new DataValidationException("Вы не можете выбрать себя");
        }

        mentorshipService.deleteMentorship(mentorId, menteeId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mentee/{userId}")
    public List<UserDto> getMentees(@PathVariable long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/mentor/{userId}")
    public List<UserDto> getMentors(@PathVariable long userId) {
        return mentorshipService.getMentors(userId);
    }
}