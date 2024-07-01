package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@Component
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto){

//        mentorshipRequestRepository.create();
    }
}
