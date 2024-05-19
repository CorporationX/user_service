package school.faang.user_service.validator.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.handler.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private final MentorshipRequestRepository mentorshipRequestRepository;

    public void requestMentorshipValidationUserIds(long requesterId, long receiverId) {
        if (receiverId == requesterId) {
            throw new IllegalArgumentException("The requester and recipient user must not be the same user");
        }

    }

    public void requestMentorshipValidationLatestRequest(long requesterId, long receiverId) {
        mentorshipRequestRepository.findLatestRequest(requesterId, receiverId).ifPresent(request -> {
            if (LocalDateTime.now().minusMonths(3).isBefore(request.getCreatedAt())) {
                throw new DataValidationException("A request for mentorship can only be made once every 3 months");
            }
        });
    }
}

