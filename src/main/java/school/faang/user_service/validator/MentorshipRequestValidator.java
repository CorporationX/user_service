package school.faang.user_service.validator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    @Getter
    private final int MENTORSHIP_REQUEST_FREQUENCY_IN_DAYS = 90;
    @Getter
    private final int MAX_DESCRIPTION_LENGTH = 4096;

    private final UserRepository userRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    public void validateMentorshipRequestParticipantsExistence(long requesterId, long receiverId) {
        if (!userRepository.existsById(requesterId)) {
            String errorMessage = String.format("MentorshipRequest sender with ID: %d not registered in Database",
                    requesterId);
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        } else if (!userRepository.existsById(receiverId)) {
            String errorMessage = String.format("MentorshipRequest receiver with ID: %d not registered in Database",
                    receiverId);
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public void validateRequesterNotEqualToReceiver(long requesterId, long receiverId) {
        if (requesterId == receiverId) {
            String errorMessage = String
                    .format("MentorshipRequest sender with id: %d should not be equal to receiver", requesterId);
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
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

    public void validateRequestStatusIsPending(RequestStatus requestStatus) {
        if (requestStatus == RequestStatus.ACCEPTED) {
            throw new IllegalStateException("Mentorship Request is already accepted");
        } else if (requestStatus == RequestStatus.REJECTED) {
            throw new IllegalStateException("Mentorship Request is already rejected");
        } else if (requestStatus != RequestStatus.PENDING) {
            throw new IllegalStateException("Mentorship Request must be in pending mode");
        }
    }

    public void validateReceiverIsNotMentorOfRequester(User requester, User receiver) {
        if (requester.getMentors().contains(receiver)) {
            String errorMessage = String.format("User with ID: %d is already the mentor of User with ID: %d",
                    receiver.getId(), requester.getId());
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public void validateParticipantsAndRequestFrequency(long requesterId,
                                                        long receiverId,
                                                        LocalDateTime mentorshipCreationDate) {
        validateRequesterNotEqualToReceiver(requesterId, receiverId);
        validateMentorshipRequestParticipantsExistence(requesterId, receiverId);
        validateMentorshipRequestFrequency(requesterId, receiverId, mentorshipCreationDate);
    }
}
