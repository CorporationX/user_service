package school.faang.user_service.validator.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.handler.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private final UserRepository userRepository;

    public void requestMentorshipValidation(long requesterId, long receiverId) {
        if (receiverId == requesterId) {
            throw new IllegalArgumentException("The requester and recipient user must not be the same user");
        }
        getUser(requesterId);
        getUser(receiverId);
    }

    private void getUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("There is no user with this ID in the database"));
    }
}
