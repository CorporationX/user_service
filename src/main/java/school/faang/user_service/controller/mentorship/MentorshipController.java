package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RequestMapping("/mentorship")
@RequiredArgsConstructor
@RestController
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/{userId}/mentees")
    public List<Long> getMentees(@PathVariable Long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/{userId}/mentors")
    public List<Long> getMentors(@PathVariable Long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/delete-mentee")
    public ResponseEntity<String> deleteMentee(@RequestParam Long mentorId, @RequestParam Long menteeId) {
        mentorshipService.deleteMentee(mentorId, menteeId);
        return ResponseEntity.ok().body("The mentee was successfully deleted");
    }

    @DeleteMapping("/delete-mentor")
    public ResponseEntity<String> deleteMentor(@RequestParam Long mentorId, @RequestParam Long menteeId) {
        mentorshipService.deleteMentor(mentorId, menteeId);
        return ResponseEntity.ok().body("The mentor was successfully deleted");
    }
}
