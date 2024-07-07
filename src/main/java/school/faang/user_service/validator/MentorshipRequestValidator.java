package school.faang.user_service.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static school.faang.user_service.exception.message.ExceptionMessage.*;

@Component
@AllArgsConstructor
public class MentorshipRequestValidator {

    private UserRepository userRepository;

    private MentorshipRequestRepository mentorshipRequestRepository;

    private static final long MONTHS = 3;

    public void validateMentorshipRequest(MentorshipRequestDto mentorshipRequestDto) {
        long requesterId = mentorshipRequestDto.getRequesterId();
        long receiverId = mentorshipRequestDto.getReceiverId();

        checkUserInDB(requesterId);
        checkUserInDB(receiverId);
        checkRequesterIsNotEqualsReceiver(requesterId, receiverId);
        checkRequestStatus(mentorshipRequestDto.getStatus());
        Optional<MentorshipRequest> latestRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId);
        latestRequest.ifPresent(this::validateDate);
    }

    private void checkUserInDB(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException(NO_SUCH_USER_EXCEPTION.getMessage());
        }
    }

    private void checkRequesterIsNotEqualsReceiver(long requesterId, long receiverId) {
        if (requesterId == receiverId) {
            throw new DataValidationException(REQUESTER_ID_EQUALS_RECEIVER_ID.getMessage());
        }
    }

    private void checkRequestStatus(RequestStatus status) {
        if (status != null) {
            throw new DataValidationException(REQUEST_ALREADY_HAS_STATUS.getMessage());
        }
    }

    private void validateDate(MentorshipRequest mentorshipRequest) {
        LocalDateTime thresholdDate = LocalDateTime.now().minusMonths(MONTHS);
        if (mentorshipRequest.getCreatedAt().isAfter(thresholdDate)) {
            throw new DataValidationException(TOO_MUCH_REQUESTS.getMessage());
        }
    }
}
