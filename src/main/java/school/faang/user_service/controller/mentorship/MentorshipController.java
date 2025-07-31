package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

/**
 * MentorshipController контроллер для связей менторов и подопечных.
 * <p>
 * Предоставляет эндпоинты для создания, получения и удаления связей менторов и подопечных.
 * </p>*
 *
 * @author fomchenkoandrey
 */
@RestController
@RequestMapping("/mentorships")
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService service;

    @PostMapping("/{mentorId}")
    public ResponseEntity<Void> addMentorship(@PathVariable Long mentorId, @PathVariable Long menteeId) {
        service.addMentorship(mentorId, menteeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mentees/{userId}")
    public ResponseEntity<List<UserDto>> getMentees(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getMentees(userId));
    }

    @GetMapping("/mentors/{userId}")
    public ResponseEntity<List<UserDto>> getMentors(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getMentors(userId));
    }

    @DeleteMapping("/{menteeId}")
    public ResponseEntity<Void> deleteMentor(@PathVariable Long menteeId, @PathVariable Long mentorId) {
        service.deleteMentorship(menteeId, mentorId);
        return ResponseEntity.ok().build();
    }
}
