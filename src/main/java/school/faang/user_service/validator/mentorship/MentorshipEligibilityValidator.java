package school.faang.user_service.validator.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MentorshipEligibilityValidator implements MentorshipValidator {

    private final MentorshipRequestRepository mentorshipRequestRepository;

    @Override
    public void validate(MentorshipRequestDto requestDto) {
        mentorshipRequestRepository.findLatestRequest(requestDto.getRequesterId(), requestDto.getReceiverId())
                .map(MentorshipRequest::getCreatedAt)
                .ifPresent(creationDate -> {
                    if (creationDate.isAfter(LocalDateTime.now().minusMonths(3))) {
                        throw new IllegalStateException(ExceptionMessages.MENTORSHIP_FREQUENCY);
                    }
                });
    }
}
