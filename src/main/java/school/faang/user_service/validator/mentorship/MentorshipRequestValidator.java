package school.faang.user_service.validator.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private final int MONTHS_REQUEST = 3;
    private final UserRepository userRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    public void validateMentorshipRequest(MentorshipRequestDto mentorshipRequest) {
        if (!userRepository.existsById(mentorshipRequest.getRequesterId()) &&
            !userRepository.existsById(mentorshipRequest.getReceiverId())) {
            throw new EntityNotFoundException("Requester or Receiver not found");
        }

        if (Objects.equals(mentorshipRequest.getRequesterId(), mentorshipRequest.getReceiverId())) {
            throw new DataValidationException("You can't make request to yourself");
        }

        if (mentorshipRequest.getDescription().isBlank()) {
            throw new DataValidationException("Description is blank, please enter a description");
        }
    }

    public void validateDateCreateRequest(long requesterId, long receiverId) {
        mentorshipRequestRepository.findLatestRequest(requesterId, receiverId).ifPresent(mentorshipRequest -> {
            if (ChronoUnit.MONTHS.between(mentorshipRequest.getCreatedAt(), LocalDateTime.now()) < MONTHS_REQUEST) {
                throw new DataValidationException("Must be %d months from last mentoring request".formatted(MONTHS_REQUEST));
            }
        });
    }
}
