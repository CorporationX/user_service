package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestValidator mentorshipRequestValidator;

    @Transactional
    public void requestMentorship(MentorshipRequestDto requestDto) {
        long requesterId = requestDto.getRequesterId();
        long receiverId = requestDto.getReceiverId();

        validateExistsUsers(requesterId, receiverId);
        mentorshipRequestValidator.validateUserIds(requesterId, receiverId);
        mentorshipRequestValidator.validateRequestTime(requesterId, receiverId);

        mentorshipRequestRepository.create(requesterId, receiverId, requestDto.getDescription());
    }

    public void validateExistsUsers(long requesterId, long receiverId) {
        if (!userRepository.existsById(requesterId) || !userRepository.existsById(receiverId)) {
            throw new DataValidationException("Нет пользователя с таким айди");
        }
    }
}
