package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/{mentorId}/mentees")
    public List<UserDto> getMentees(@PathVariable long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/{menteeId}/mentors")
    public List<UserDto> getMentors(@PathVariable long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("/{mentorId}/mentees/{menteeId}")
    public void deleteMentee(@PathVariable long mentorId, @PathVariable long menteeId) {
        mentorshipService.deleteMentee(mentorId, menteeId);
    }

    @DeleteMapping("/{menteeId}/mentors/{mentorId}")
    public void deleteMentor(@PathVariable long menteeId, @PathVariable long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
