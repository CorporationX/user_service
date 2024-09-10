package school.faang.user_service.controller.mentorship;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MentorshipController {
    private final MentorshipService mentorshipService;


    @GetMapping("/mentors/{mentorId}/mentees")
    public ResponseEntity<List<UserDTO>> getMentees(
            @PathVariable("mentorId") @Min(1) long mentorId) {
        List<UserDTO> mentees = mentorshipService.getMentees(mentorId);
        return ResponseEntity.ok(mentees);
    }

    @DeleteMapping("/mentors/{mentorId}/mentees/{menteeId}")
    public ResponseEntity<Void> deleteMentee(
            @PathVariable("mentorId") @Min(1) long mentorId,
            @PathVariable("menteeId") @Min(1) long menteeId) {
        mentorshipService.deleteMenteeOfMentor(mentorId, menteeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mentees/{menteeId}/mentors")
    public ResponseEntity<List<UserDTO>> getMentors(
            @PathVariable("menteeId") @Min(1) long menteeId) {
        List<UserDTO> mentors = mentorshipService.getMentors(menteeId);
        return ResponseEntity.ok(mentors);
    }

    @DeleteMapping("/mentees/{menteeId}/mentors/{mentorId}")
    public ResponseEntity<List<UserDTO>> deleteMentor(
            @PathVariable("mentorId") @Min(1) long mentorId,
            @PathVariable("menteeId") @Min(1) long menteeId) {
        mentorshipService.deleteMentorOfMentee(mentorId, menteeId);
        return ResponseEntity.noContent().build();
    }


}
