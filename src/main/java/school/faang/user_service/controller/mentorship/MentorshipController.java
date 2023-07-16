package school.faang.user_service.controller.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.mentorship.MentorshipService;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@Slf4j
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/{mentorId}/mentees")
    public ResponseEntity<?> getMentees(@PathVariable long mentorId) {
        log.debug("Got new request to get mentees for mentor with id:{}", mentorId);
        try {
            List<UserDto> mentees = mentorshipService.getMentees(mentorId);
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
}
