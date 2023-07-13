package school.faang.user_service.util.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.util.exception.SameMentorAndMenteeException;
import school.faang.user_service.util.exception.TimeHasNotPassedException;
import school.faang.user_service.util.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class MentorshipRequestValidator {

    public void validate(Optional<User> requester,
                         Optional<User> receiver,
                         Optional<MentorshipRequest> lastTimeRequest) {

        if (requester.isEmpty() || receiver.isEmpty()) {
            throw new UserNotFoundException();
        }
        if (requester.get().getId() == receiver.get().getId()) {
            throw new SameMentorAndMenteeException();
        }
        if (LocalDateTime.now().getMonth().getValue() - lastTimeRequest.get().getCreatedAt().getMonth().getValue() < 3) {
            throw new TimeHasNotPassedException();
        }
    }
}