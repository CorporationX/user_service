package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    public void validateRequestTime(long requesterId, long receiverId) {
        mentorshipRequestRepository.findLatestRequest(requesterId, receiverId)
                .ifPresent(request -> {
                    if (request.getCreatedAt().plusMonths(3).isAfter(LocalDateTime.now())) {
                        throw new DataValidationException("Запрос можно отправлять раз в три месяца");
                    }
                });
    }

    public void validateUserIds(long requesterId, long receiverId) {
        if (requesterId == receiverId) {
            throw new DataValidationException("requesterId и receiverId не могут совпадать");
        }
    }

    public void validateDescription(MentorshipRequestDto requestDto) {
        if (requestDto.getDescription() == null || requestDto.getDescription().isEmpty()) {
            throw new DataValidationException("Нет описания");
        }
    }
}
