package school.faang.user_service.validator.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.repository.UserRepository;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class MentorshipParticipantsExistenceValidator implements MentorshipValidator {

    private final UserRepository userRepository;

    @Override
    public void validate(MentorshipRequestDto requestDto) {
        if (!userRepository.existsById(requestDto.getReceiverId())) {
            throw new NoSuchElementException(ExceptionMessages.RECEIVER_NOT_FOUND);
        }
        if (!userRepository.existsById(requestDto.getRequesterId())) {
            throw new NoSuchElementException(ExceptionMessages.REQUESTER_NOT_FOUND);
        }
    }
}
