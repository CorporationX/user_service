package school.faang.user_service.controller.mentorship;

import lombok.NonNull;
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
    public List<UserDto> getMentees(@PathVariable @NonNull Long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/{menteeId}/mentors")
    public List<UserDto> getMentors(@PathVariable  @NonNull Long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("/{mentorId}/mentees/{menteeId}")
    public void deleteMentee(@PathVariable  @NonNull Long menteeId, @PathVariable  @NonNull Long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/{menteeId}/mentors/{mentorId}")
    public void deleteMentor(@PathVariable  @NonNull Long menteeId, @PathVariable  @NonNull Long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
