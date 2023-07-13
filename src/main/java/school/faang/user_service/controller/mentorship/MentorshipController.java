package school.faang.user_service.controller.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.service.mentorship.MentorshipService;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/{mentorId}/mentees")
    public ResponseEntity<?> getMentees(@PathVariable long mentorId) {
        log.debug("Got new request to get mentees for mentor with id:{}", mentorId);
        try {
            List<MenteeDto> mentees = mentorshipService.getMentees(mentorId);
            log.debug("Successfully got mentees for mentor with id:{}", mentorId);
            return ResponseEntity.ok(mentees);
        } catch (RuntimeException e) {
            log.warn("Failed to get mentees for mentor with id:{}\nException:{}", mentorId, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to get mentees for mentor with id:{}\nException:{}", mentorId, e);
            return ResponseEntity.internalServerError().body("Server error");
        }
    }

    @GetMapping("/{userId}/mentors")
    public ResponseEntity<?> getMentors(@PathVariable long userId) {
        log.debug("Got new request to get mentors for user with id:{}", userId);
        try {
            List<MentorDto> mentors = mentorshipService.getMentors(userId);
            log.debug("Successfully got mentors for user with id:{}", userId);
            return ResponseEntity.ok(mentors);
        } catch (RuntimeException e) {
            log.warn("Failed to get mentors for user with id:{}\nException:{}", userId, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to get mentors for user with id:{}\nException:{}", userId, e);
            return ResponseEntity.internalServerError().body("Server error");
        }
    }

    @DeleteMapping("/{mentorId}/mentors/{menteeId}/delete")
    public ResponseEntity<?> deleteMentee(@PathVariable long mentorId, @PathVariable long menteeId) {
        log.debug("Got new request to delete mentee from mentor with id:{}", mentorId);
        try {
            mentorshipService.deleteMentee(mentorId, menteeId);
            log.debug("Successfully deleted mentee with id:{} from mentor with id:{}", menteeId, mentorId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.warn("Failed to delete mentee from mentor with id:{}\nException:{}", mentorId, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to delete mentee from mentor with id:{}\nException:{}", mentorId, e);
            return ResponseEntity.internalServerError().body("Server error");
        }
    }
}
