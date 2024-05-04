package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
