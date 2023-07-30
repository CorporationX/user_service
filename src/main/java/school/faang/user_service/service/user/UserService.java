package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public UserDto deactivateUser(UserDto userDto) {
        validate(userDto.getId());
        deleteGoals(userDto);
        return userDto;
    }

    private void deleteGoals(UserDto userDto) {
        List<Goal> userGoals = goalRepository.findGoalsByUserId(userDto.getId()).toList();
        for (Goal goal : userGoals) {
            List<User> users = goal.getUsers();
            if (users.size() == 1 && users.get(0).getId() == userDto.getId()) {
                goalRepository.deleteById(goal.getId());
            }
        }
    }

    private void validate(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException(MessageFormat.format("User {0} does not exist!", userId));
        }
    }
}
