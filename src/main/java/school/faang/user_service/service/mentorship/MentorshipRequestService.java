package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.MentorshipStartEvent;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.publisher.MentorshipStartEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository repository;
    private final MentorshipStartEventPublisher publisher;

    @Transactional
    public void acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = getById(id);
        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();

        if (!requester.getMentors().contains(receiver)) {
            requester.getMentors().add(receiver);
            mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
            publishEvent(mentorshipRequest);
        } else {
            throw new IllegalArgumentException("This user already your mentor");
        }
    }

    private MentorshipRequest getById(long requestId) {
        return repository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));
    }

    private void publishEvent(MentorshipRequest mentorshipRequest) {
        MentorshipStartEvent event = MentorshipStartEvent.builder()
                .menteeId(mentorshipRequest.getRequester().getId())
                .mentorId(mentorshipRequest.getReceiver().getId())
                .build();

        publisher.sendEvent(event);
    }
}
