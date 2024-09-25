package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/mentees/{mentorId}")
    public List<UserDto> getMentees(@PathVariable long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/mentors/{menteeId}")
    public List<UserDto> getMentors(@PathVariable long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("/mentee/{mentorId}/{menteeId}")
    public void deleteMentee(@PathVariable long mentorId, @PathVariable long menteeId) {
        mentorshipService.deleteMentee(mentorId, menteeId);
    }

    @DeleteMapping("/mentor/{menteeId}/{mentorId}")
    public void deleteMentor(@PathVariable long menteeId, @PathVariable long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}