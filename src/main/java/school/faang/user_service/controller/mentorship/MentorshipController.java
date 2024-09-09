package school.faang.user_service.controller.mentorship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Controller
@RequestMapping("/mentorship")
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @Autowired
    public MentorshipController(MentorshipService mentorshipService) {
        this.mentorshipService = mentorshipService;
    }

    @GetMapping("/mentors/{mentorId}/mentees")
    public ResponseEntity<List<UserDTO>> getMentees(
            @PathVariable("mentorId") long mentorId) {
        List<UserDTO> mentees = mentorshipService.getMentees(mentorId);
        return ResponseEntity.ok(mentees);
    }

    @DeleteMapping("/mentors/{mentorId}/mentees/{menteeId}")
    public ResponseEntity<Void> deleteMentee(
            @PathVariable("mentorId") long mentorId,
            @PathVariable("menteeId") long menteeId) {
        mentorshipService.deleteMenteeOfMentor(mentorId, menteeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mentees/{menteeId}/mentors")
    public ResponseEntity<List<UserDTO>> getMentors(
            @PathVariable("menteeId") long menteeId) {
        List<UserDTO> mentors = mentorshipService.getMentors(menteeId);
        return ResponseEntity.ok(mentors);
    }

    @DeleteMapping("/mentees/{menteeId}/mentors/{mentorId}")
    public ResponseEntity<List<UserDTO>> getMentors(
            @PathVariable("mentorId") long mentorId,
            @PathVariable("menteeId") long menteeId) {
        mentorshipService.deleteMentorOfMentee(mentorId, menteeId);
        return ResponseEntity.noContent().build();
    }


}
