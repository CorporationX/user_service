package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/getMentorshipUser")
public class MentorshipController {
    private final MentorshipService mentorshipService;
    private final MentorshipValidator mentorshipValidator;

    @PostMapping("/deleteMentee")
    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipValidator.idValidator(menteeId);
        mentorshipValidator.idValidator(mentorId);

        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @PostMapping("/deleteMentor")
    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipValidator.idValidator(menteeId);
        mentorshipValidator.idValidator(mentorId);

        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
