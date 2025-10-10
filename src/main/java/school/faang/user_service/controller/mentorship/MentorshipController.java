package school.faang.user_service.controller.mentorship;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/api/mentorships")
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @PostMapping("/add")
    public void addMentorship(
            @RequestParam @Min(1) long mentorId,
            @RequestParam @Min(1) long menteeId
    ) {
        mentorshipService.addMentorship(mentorId, menteeId);
    }

    @GetMapping("/mentor/{mentorId}/mentees")
    public List<UserDto> getMentees(@PathVariable @Min(1) long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/mentee/{menteeId}/mentors")
    public List<UserDto> getMentors(@PathVariable @Min(1) long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("/delete")
    public void deleteMentorship(
            @RequestParam @Min(1) long menteeId,
            @RequestParam @Min(1) long mentorId
    ) {
        mentorshipService.deleteMentorship(menteeId, mentorId);
    }
}