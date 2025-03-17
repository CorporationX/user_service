package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.dto.mentorship.MentorshipDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentors/{mentorId}/mentees")
    public ResponseEntity<List<MentorshipDto>> getMentees(@PathVariable("mentorId") long mentorId) {
        List<MentorshipDto> mentees = mentorshipService.getMentees(mentorId);
        return ResponseEntity.ok(mentees);
    }

    @GetMapping("/mentees/{menteeId}/mentors")
    public ResponseEntity<List<MentorshipDto>> getMentors(@PathVariable("menteeId") long menteeId) {
        List<MentorshipDto> mentors = mentorshipService.getMentors(menteeId);
        return ResponseEntity.ok(mentors);
    }

    @DeleteMapping("/mentors/{mentorId}/mentees/{menteeId}")
    public ResponseEntity<Void> deleteMentee(@PathVariable("menteeId") long menteeId, @PathVariable("mentorId") long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/mentees/{menteeId}/mentors/{mentorId}")
    public ResponseEntity<Void> deleteMentor(@PathVariable("menteeId") long menteeId, @PathVariable("mentorId") long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
        return ResponseEntity.noContent().build();
    }
}
