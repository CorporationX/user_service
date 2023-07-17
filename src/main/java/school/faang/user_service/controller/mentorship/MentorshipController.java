package school.faang.user_service.controller.mentorship;

import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@Slf4j
@Validated
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/{mentorId}/mentees")
    public ResponseEntity<?> getMentees(@PathVariable @Min(1L) long mentorId) {
        log.debug("Received new request to get mentees for mentor with id:{}", mentorId);
        try {
            List<UserDto> mentees = mentorshipService.getMentees(mentorId);
            log.debug("Successfully got mentees for mentor with id:{}", mentorId);
            return ResponseEntity.ok(mentees);
        } catch (RuntimeException e) {
            log.warn("Failed to get mentees for mentor with id:{}\nException:{}", mentorId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to get mentees for mentor with id:{}\nException:{}", mentorId, e.getMessage());
            return ResponseEntity.internalServerError().body("Server error");
        }
    }

    @GetMapping("/{menteeId}/mentors")
    public ResponseEntity<?> getMentors(@PathVariable @Min(1L) long menteeId) {
        log.debug("Received new request to get mentors for mentee with id:{}", menteeId);
        try {
            List<UserDto> mentors = mentorshipService.getMentors(menteeId);
            log.debug("Successfully got mentors for mentee with id:{}", menteeId);
            return ResponseEntity.ok(mentors);
        } catch (RuntimeException e) {
            log.warn("Failed to get mentors for mentee with id:{}\nException:{}", menteeId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to get mentors for mentee with id:{}\nException:{}", menteeId, e.getMessage());
            return ResponseEntity.internalServerError().body("Server error");
        }
    }

    @DeleteMapping("/{mentorId}/mentees/{menteeId}/delete")
    public ResponseEntity<?> deleteMentee(@PathVariable @Min(1L) long mentorId, @PathVariable @Min(1L) long menteeId) {
        log.debug("Received new request to delete mentee with id:{} from mentor with id:{}", menteeId, mentorId);
        try {
            mentorshipService.deleteMentee(mentorId, menteeId);
            log.debug("Successfully deleted mentee with id:{} from mentor with id:{}", menteeId, mentorId);
            return ResponseEntity.ok().body("Mentee was successfully deleted");
        } catch (RuntimeException e) {
            log.warn("Failed to delete mentee with id:{} from mentor with id:{}\nException:{}",
                    menteeId, mentorId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to delete mentee with id:{} from mentor with id:{}\nException:{}",
                    menteeId, mentorId, e.getMessage());
            return ResponseEntity.internalServerError().body("Server error");
        }
    }

    @DeleteMapping("/{menteeId}/mentors/{mentorId}/delete")
    public ResponseEntity<?> deleteMentor(@PathVariable @Min(1L) long menteeId, @PathVariable @Min(1L) long mentorId) {
        log.debug("Received new request to delete mentor with id:{} from mentee with id:{}", mentorId, menteeId);
        try {
            mentorshipService.deleteMentor(menteeId, mentorId);
            log.debug("Successfully deleted mentor with id:{} from mentee with id:{}", mentorId, menteeId);
            return ResponseEntity.ok().body("Mentor was successfully deleted");
        } catch (RuntimeException e) {
            log.warn("Failed to delete mentor with id:{} from mentee with id:{}\nException:{}",
                    mentorId, menteeId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to delete mentor with id:{} from mentee with id:{}\nException:{}",
                    mentorId, menteeId, e.getMessage());
            return ResponseEntity.internalServerError().body("Server error");
        }
    }
}
