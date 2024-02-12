package school.faang.user_service.controller.mentorship;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/mentorship")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentor/{id}")
    public List<UserDto> getMentees(@PathVariable @Positive(message = "Id должен быть положительным") long id) {
        return mentorshipService.getMentees(id);
    }

    @GetMapping("/mentee/{id}")
    public List<UserDto> getMentors(@PathVariable @Positive(message = "Id должен быть положительным") long id) {
        return mentorshipService.getMentors(id);
    }

    @DeleteMapping("/mentor/{mentorId}/mentee/{menteeId}")
    public void deleteMentee(@PathVariable @Positive(message = "Id должен быть положительным") long menteeId,
                             @PathVariable @Positive(message = "Id должен быть положительным") long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/mentee/{menteeId}/mentor/{mentorId}")
    public void deleteMentor(@PathVariable @Positive(message = "Id должен быть положительным") long menteeId,
                             @PathVariable @Positive(message = "Id должен быть положительным") long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
