package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.service.mentorship.MentorshipService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/getMentorshipUser")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/getMentorshipUser/{id}")
    public void getMentees(@PathVariable long id) {
        mentorshipService.getMentees(id);
    }

    @GetMapping("/getMentorshipUser/{id}")
    public void getMentors(@PathVariable long id) {
        mentorshipService.getMentors(id);
    }
}
