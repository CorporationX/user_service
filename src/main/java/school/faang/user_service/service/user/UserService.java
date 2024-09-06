package school.faang.user_service.service.user;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void addGoalToUserGoals(Long userId, Goal goal) {
        User user = userRepository.findByIdWithGoals(userId)
                .orElseThrow(() -> new ValidationException("User with id " + userId + " not exist"));
        if (user.getGoals() == null) {
            user.setGoals(new ArrayList<>());
        }

        user.getGoals().add(goal);
        userRepository.save(user);
    }

}
