package school.faang.user_service.repository.mentorship.config_mentorship_request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @Autowired
    public MentorshipRequestController(MentorshipRequestService mentorshipRequestService) {
        this.mentorshipRequestService = mentorshipRequestService;
    }

    public static void requestMentorship() {
        MentorshipRequestService.requestMentorship();
    }
}
