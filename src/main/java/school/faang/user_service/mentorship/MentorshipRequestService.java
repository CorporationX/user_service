package school.faang.user_service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@Component
@RequiredArgsConstructor
public class MentorshipRequestService {

    private MentorshipRequestRepository mentorshipRequestRepository;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {

    }
}
