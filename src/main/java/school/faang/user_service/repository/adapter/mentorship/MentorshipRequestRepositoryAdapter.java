package school.faang.user_service.repository.adapter.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@Component
@RequiredArgsConstructor
public class MentorshipRequestRepositoryAdapter {
    private final MentorshipRequestRepository mentorshipRequestRepository;

    public MentorshipRequest getMentorshipRequest(Long id) {
        return mentorshipRequestRepository
                .findById(id)
                .orElseThrow(() -> new DataValidationException(String.format("There is no request with id %d.", id)));
    }
}
