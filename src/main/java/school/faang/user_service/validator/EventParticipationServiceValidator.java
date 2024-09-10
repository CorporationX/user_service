package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationServiceValidator {
    public void validateUserRegister(List<User> users,  long userId) throws IllegalArgumentException {
        if (isUserRegistered(users, userId)) {
            throw new IllegalArgumentException("cant register user: user already" + userId + " register to event");
        }
    }

    public void validateUserUnregister(List<User> users,  long userId) throws IllegalArgumentException {
        if (!isUserRegistered(users, userId)) {
            throw new IllegalArgumentException("cant unregister user: user do not" + userId + " register to event");
        }
    }

    public boolean isUserRegistered(List<User> users,  long userId) {
        if (users == null) {
            return false;
        }
        return users.stream()
                .map(User::getId)
                .anyMatch(id -> id == userId);
    }
}
