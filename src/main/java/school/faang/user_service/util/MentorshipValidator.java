package school.faang.user_service.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class MentorshipValidator {

    public static final String REQUEST_MSG_ERR = "Request description can't be empty";
    public static final String SAME_USER_ERR = "Requester can't be the same as a receiver";

    private final UserRepository userRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    @Value("${req.period.min}")
    private int MIN_REQ_PERIOD;

    public void validateRequest(MentorshipRequestDto dto) {
        if (dto.getDescription().isBlank()) {
            log.error(REQUEST_MSG_ERR);
            throw new DataValidationException(REQUEST_MSG_ERR);
        }
    }

    public void validateEqualsId(long requesterId, long receiverId) {
        if (requesterId == receiverId) {
            log.error(SAME_USER_ERR);
            throw new DataValidationException(SAME_USER_ERR);
        }
    }

    public void validateForEmptyRequester(long requesterId) {
        Optional<User> requester = userRepository.findById(requesterId);
        if (requester.isEmpty()) {
            String errMessage = "Requester with ID: " + requesterId + " doesn't exist";
            log.error(errMessage);
            throw new DataValidationException(errMessage);
        }
    }

    public void validateForEmptyReceiver(long receiverId) {
        Optional<User> receiver = userRepository.findById(receiverId);
        if (receiver.isEmpty()) {
            String errMessage = "Receiver with ID: " + receiverId + " doesn't exist";
            log.error(errMessage);
            throw new DataValidationException(errMessage);
        }
    }

    public void validateLastRequest(MentorshipRequest lastRequest) {
        if (!lastRequest.getCreatedAt().isBefore(LocalDateTime.now().minusMonths(MIN_REQ_PERIOD))) {
            String errMessage = "A request for mentorship can only be made once every " + MIN_REQ_PERIOD + " months";
            log.error(errMessage);
            throw new DataValidationException(errMessage);
        }
    }

    public void validateExistMentorInRequesterList(User requester, User receiver) {
        if (requester.getMentors().contains(receiver)) {
            String errMessage = "Mentor " + receiver.getUsername() + " already assign to " + requester.getUsername();
            log.error(errMessage);
            throw new DataValidationException(errMessage);
        }
    }

    public void validateRejectionDto(RejectionDto rejection) {
        if (rejection.getReason().isEmpty() || rejection.getReason().isBlank()) {
            String errMessage = "Rejection reason can't be empty";
            log.error(errMessage);
            throw new DataValidationException(errMessage);
        }
    }
}
