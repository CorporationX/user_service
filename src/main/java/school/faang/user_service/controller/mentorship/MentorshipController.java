package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.mentorship.MentorshipService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/getMentorshipUser")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @PostMapping("/getMentorshipUser/deleteMentee")
    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @PostMapping("/getMentorshipUser/deleteMentor")
    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
