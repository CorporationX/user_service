package school.faang.user_service.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.service.user.UserService;

import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
public class ProjectSubscriptionValidator {
    private final UserService userService;

    public void validateProjectSubscription(long userId) {
        userService.existsById(userId);
    }
}