package school.faang.user_service.validator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
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
    @Getter
    private final int MAX_DESCRIPTION_LENGTH = 4096;
    @Getter
    private final int MIN_USER_ID = 1;
    @Getter
    private final int MIN_REQUEST_ID = 1;

    private final UserRepository userRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    public void validateMentorshipRequestDto(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto == null) {
            throw new IllegalArgumentException("Mentorship request cannot be null");
        } else if (mentorshipRequestDto.getDescription() == null) {
            throw new IllegalArgumentException("Mentorship request description cannot be null");
        } else if (mentorshipRequestDto.getRequesterId() < MIN_USER_ID) {
            throw new IllegalArgumentException("Mentorship requester id cannot be less than " + MIN_USER_ID);
        } else if (mentorshipRequestDto.getReceiverId() < MIN_USER_ID) {
            throw new IllegalArgumentException("Mentorship receiver id cannot be less than " + MIN_USER_ID);
        }
    }

    public void validateMentorshipRequestFilterDto(MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        if (mentorshipRequestFilterDto == null) {
            throw new IllegalArgumentException("Mentorship request filter cannot be null");
        }
    }

    public void validateMentorshipRejectionDto(RejectionDto rejectionDto) {
        if (rejectionDto == null) {
            throw new IllegalArgumentException("Rejection reason cannot be null");
        }
    }

    public void validateMentorshipRequestId(long requestId) {
        if (requestId < MIN_REQUEST_ID) {
            throw new IllegalArgumentException("Mentorship request id cannot be less than " + MIN_REQUEST_ID);
        }
    }

    public void validateMentorshipRequestDescription(String description) {
        if (description.isBlank()) {
            throw new IllegalArgumentException("Description cannot ve blank");
        } else if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("Description length should be not greater than "
                    + MAX_DESCRIPTION_LENGTH + " characters");
        }
    }

    public void validateMentorshipRequestParticipantsExistence(long requesterId, long receiverId) {
        if (!userRepository.existsById(requesterId)) {
            throw new IllegalArgumentException("MentorshipRequest sender is not registered in Database");
        } else if (!userRepository.existsById(receiverId)) {
            throw new IllegalArgumentException("MentorshipRequest receiver is not registered in Database");
        }
    }

    public void validateRequesterNotEqualToReceiver(long requesterId, long receiverId) {
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

    public void validateRequestStatusIsPending(RequestStatus requestStatus) {
        if (requestStatus == RequestStatus.ACCEPTED) {
            throw new IllegalStateException("Mentorship Request is already accepted");
        } else if (requestStatus == RequestStatus.REJECTED) {
            throw new IllegalStateException("Mentorship Request is already rejected");
        } else if (requestStatus != RequestStatus.PENDING) {
            throw new IllegalStateException("Mentorship Request must be in pending mode");
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
