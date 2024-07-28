package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/mentees/{userId}")
    List<UserDto> getMentees(@PathVariable long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/mentors/{userId}")
    List<UserDto>  getMentors(@PathVariable long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/mentors/{mentorId}/mentee/{menteeId}")
    UserDto deleteMentee(@PathVariable long menteeId, @PathVariable long mentorId) {
        return mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/mentees/{menteeId}/mentor/{mentorId}")
    UserDto deleteMentor(@PathVariable long menteeId, @PathVariable long mentorId) {
        return mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
