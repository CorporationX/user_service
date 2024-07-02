package school.faang.user_service.validator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    @Getter
    private final int MENTORSHIP_REQUEST_FREQUENCY_IN_DAYS = 90;

    private final UserRepository userRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    public void validateMentorshipRequestDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("There should be description");
        }
    }

    public void validateMentorshipRequestReceiverAndRequesterExistence(long requesterId, long receiverId) {
        if (!userRepository.existsById(requesterId)) {
            throw new IllegalArgumentException("MentorshipRequest sender is not registered in Data Base");
        } else if (!userRepository.existsById(receiverId)) {
            throw new IllegalArgumentException("MentorshipRequest receiver is not registered in Data Base");
        }
    }

    public void validateReflection(long requesterId, long receiverId) {
        if (Objects.equals(requesterId, receiverId)) {
            throw new IllegalArgumentException("MentorshipRequest sender cannot be equal to receiver");
        }
    }

    public void validateMentorshipRequestFrequency(long requesterId, long receiverId,
                                                   LocalDateTime mentorshipCreationDate) {
        Optional<MentorshipRequest> latestRequest =
                mentorshipRequestRepository.findLatestRequest(requesterId, receiverId);
        if (latestRequest.isPresent() && latestRequest.get().getCreatedAt()
                .plusDays(MENTORSHIP_REQUEST_FREQUENCY_IN_DAYS)
                .isAfter(mentorshipCreationDate)) {
            throw new IllegalArgumentException("MentorshipRequest can be sent once in "
                    + MENTORSHIP_REQUEST_FREQUENCY_IN_DAYS + " days");
        }
    }
}
