package school.faang.user_service.validator.mentorship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@Component
public class MentorshipRequestValidator {
    private final UserRepository userRepository;

    @Autowired
    public MentorshipRequestValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean validateRequestMentorshipDescription(MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().isEmpty();
    }

    public void validateRequestMentorship(MentorshipRequestDto mentorshipRequestDto) throws Exception {
        Long requesterId = mentorshipRequestDto.getRequesterId();
        Long receiverId = mentorshipRequestDto.getReceiverId();

        if (requesterId.equals(receiverId)) {
            throw new Exception("Нельзя назначить себя ментором!");
        }

        if (!userRepository.existsById(requesterId) || !userRepository.existsById(receiverId)) {
            throw new Exception("Пользователь не найден");
        }
    }

    public void validateAcceptRequest(Optional<MentorshipRequest> requestOptional) {
        if (requestOptional.isEmpty()) {
            throw new RuntimeException("Запрос не найден");
        }

        MentorshipRequest request = requestOptional.get();

        if (request.getStatus() == RequestStatus.ACCEPTED) {
            throw new RuntimeException("Запрос уже получен");
        }
    }

    public void validateRejectRequest(Optional<MentorshipRequest> requestOptional) {
        if (requestOptional.isEmpty()) {
            throw new RuntimeException("Запрос не найден");
        }

        MentorshipRequest request = requestOptional.get();

        if (request.getStatus() == RequestStatus.REJECTED) {
            throw new RuntimeException("Запрос уже отклонен");
        }
    }
}
