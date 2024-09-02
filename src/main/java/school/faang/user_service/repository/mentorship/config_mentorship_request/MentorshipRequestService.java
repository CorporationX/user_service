package school.faang.user_service.repository.mentorship.config_mentorship_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@Component
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;

    public MentorshipRequestService(MentorshipRequestRepository mentorshipRequestRepository) {
        this.mentorshipRequestRepository = mentorshipRequestRepository;
    }

    public static void requestMentorship() {

    }
}
