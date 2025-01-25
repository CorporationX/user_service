package school.faang.user_service.repository.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@Component
@RequiredArgsConstructor
public class MentorshipRequestRepositoryAdapter {
    private final MentorshipRequestRepository mentorshipRequestRepository;

    public MentorshipRequest findById(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Mentorship request with identifier \"" + id
                        + "\" does not exist"));
    }
}
