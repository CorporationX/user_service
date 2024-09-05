package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mentorship")
@RequiredArgsConstructor
public class MentorshipController {
    public final MentorshipService mentorshipService;

    @GetMapping("mentees/{id}")
    public List<MentorshipDto> getMentees(@PathVariable("id") long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/mentors/{id}")
    public List<MentorshipDto> getMentors(@PathVariable("id") long id) {
        return mentorshipService.getMentors(id);
    }
}
