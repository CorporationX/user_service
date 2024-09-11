package school.faang.user_service.controller.mentorship;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentors/{mentorId}/mentees")
    public ResponseEntity<List<UserDto>> getMentees(
            @PathVariable("mentorId") @Min(1) long mentorId) {
        List<UserDto> mentees = mentorshipService.getMentees(mentorId);
        return ResponseEntity.ok(mentees);
    }

    @ResponseStatus
    @DeleteMapping("/mentors/{mentorId}/mentees/{menteeId}")
    public void deleteMentee(
            @PathVariable("mentorId") @Min(1) long mentorId,
            @PathVariable("menteeId") @Min(1) long menteeId) {
        mentorshipService.deleteMenteeOfMentor(mentorId, menteeId);

    }

    @GetMapping("/mentees/{menteeId}/mentors")
    public ResponseEntity<List<UserDto>> getMentors(
            @PathVariable("menteeId") @Min(1) long menteeId) {
        List<UserDto> mentors = mentorshipService.getMentors(menteeId);
        return ResponseEntity.ok(mentors);
    }

    @ResponseStatus
    @DeleteMapping("/mentees/{menteeId}/mentors/{mentorId}")
    public void deleteMentor(
            @PathVariable("mentorId") @Min(1) long mentorId,
            @PathVariable("menteeId") @Min(1) long menteeId) {
        mentorshipService.deleteMentorOfMentee(mentorId, menteeId);
    }


}
