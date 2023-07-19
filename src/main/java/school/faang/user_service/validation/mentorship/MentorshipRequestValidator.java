package school.faang.user_service.validation.mentorship;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.mentorship.MentorshipRequestValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;

    public void validate(MentorshipRequestDto mentorshipRequestDto) {
        long requesterId = mentorshipRequestDto.getRequesterId();
        long receiverId = mentorshipRequestDto.getReceiverId();

        if (!userRepository.existsById(requesterId)) {
            throw new EntityNotFoundException("Requester with id " + requesterId + " not found.");
        }
        if (!userRepository.existsById(receiverId)) {
            throw new EntityNotFoundException("Receiver with id " + receiverId + " not found.");
        }
        if (requesterId == receiverId) {
            throw new MentorshipRequestValidationException("Requester and receiver cannot be the same user.");
        }

        mentorshipRequestRepository.findLatestRequest(requesterId, receiverId).ifPresent(
                request -> {
                    if (request.getUpdatedAt().isAfter(LocalDateTime.now().minusMonths(3))) {
                        throw new MentorshipRequestValidationException(
                                "Request has already been sent for the last three months.");
                    }
                }
        );
    }
}
