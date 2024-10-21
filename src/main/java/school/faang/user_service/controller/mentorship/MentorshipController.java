package school.faang.user_service.controller.mentorship;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentors/{mentorId}/mentees")
    public List<UserDto> getMentees(
            @PathVariable("mentorId") @Min(1) long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/mentors/{mentorId}/mentees/{menteeId}")
    public void deleteMentee(
            @PathVariable("mentorId") @Min(1) long mentorId,
            @PathVariable("menteeId") @Min(1) long menteeId) {
        if (mentorId == menteeId) {
            throw new IllegalArgumentException("Mentor and mentee cannot be the same");
        }
        mentorshipService.deleteMenteeOfMentor(mentorId, menteeId);

    }

    @GetMapping("/mentees/{menteeId}/mentors")
    public List<UserDto> getMentors(
            @PathVariable("menteeId") @Min(1) long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/mentees/{menteeId}/mentors/{mentorId}")
    public void deleteMentor(
            @PathVariable("mentorId") @Min(1) long mentorId,
            @PathVariable("menteeId") @Min(1) long menteeId) {
        if (mentorId == menteeId) {
            throw new IllegalArgumentException("Mentor and mentee cannot be the same");
        }
        mentorshipService.deleteMentorOfMentee(mentorId, menteeId);
    }


}
