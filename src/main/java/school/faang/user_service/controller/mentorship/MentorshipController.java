package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MenteeDto;
import school.faang.user_service.dto.MentorDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship/{userId}")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentees")
    public List<MenteeDto> getMentees(@PathVariable Long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/mentors")
    public List<MentorDto> getMentors(@PathVariable Long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/mentee/{menteeId}")
    public void deleteMentee(@PathVariable Long userId, @PathVariable Long menteeId) {
        mentorshipService.deleteMentee(userId, menteeId);
    }

    @DeleteMapping("/mentor/{mentorId}")
    public void deleteMentor(@PathVariable Long userId, @PathVariable Long mentorId) {
        mentorshipService.deleteMentor(userId, mentorId);
    }
}
