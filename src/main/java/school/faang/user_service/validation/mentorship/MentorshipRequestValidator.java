package school.faang.user_service.validation.mentorship;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.user.UserService;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private static final int REQUEST_TIME_LIMIT = 3;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserService userService;

    public void validate(MentorshipRequestDto mentorshipRequestDto) {
        long requesterId = mentorshipRequestDto.getRequesterId();
        long receiverId = mentorshipRequestDto.getReceiverId();

        userService.validateUsers(requesterId, receiverId);

        if (requesterId == receiverId) {
            throw new DataValidationException("Requester and receiver cannot be the same user.");
        }

        mentorshipRequestRepository.findLatestRequest(requesterId, receiverId).ifPresent(
                request -> {
                    if (request.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(REQUEST_TIME_LIMIT))) {
                        throw new DataValidationException(
                                "Request has already been sent for the last three months.");
                    }
                }
        );
    }
}
