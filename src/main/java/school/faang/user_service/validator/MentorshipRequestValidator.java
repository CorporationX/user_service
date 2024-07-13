package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private final UserRepository userRepository;

    public void existsById(Long id, String message) {
        if (!userRepository.existsById(id)) {
            throw new DataValidationException(message);
        }
    }

    public void isIdsEqual(Long firstId, Long secondId) {
        if (firstId.equals(secondId)) {
            throw new DataValidationException("You cannot send request to yourself!");
        }
    }
}
