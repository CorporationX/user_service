package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private UserRepository userRepository;
    private MentorshipRequestRepository mentorshipRequestRepository;

    private static final String SELF_REQUEST = "Requester and receiver are the same";
    private static final String TOO_EARLY = "А request can be made at three months";
    private static final String ALREADY_MENTOR = "The receiver is already a mentor for the requester";

    public void validateRequest(MentorshipRequest mentorshipRequest) {
        Long requesterId = mentorshipRequest.getRequester().getId();
        Long receiverId = mentorshipRequest.getReceiver().getId();

        if (requesterId.equals(receiverId)) {
            throw new DataValidationException(SELF_REQUEST);
        }

        mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId)
                .map(request -> request.getCreatedAt().plusMonths(3))
                .filter(creationDate -> LocalDateTime.now().isBefore(creationDate))
                .ifPresent(creationDate -> {
                    throw new DataValidationException(TOO_EARLY);
                });

        if (mentorshipRequest.getRequester().getMentors().stream()
                .anyMatch(user -> user.equals(mentorshipRequest.getReceiver()))
        ) {
            throw new DataValidationException(ALREADY_MENTOR);
        }
    }
}
