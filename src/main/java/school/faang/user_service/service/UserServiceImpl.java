package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.GoalCompletedEvent;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final GoalCompletedEventPublisher goalCompletedEventPublisher;

    @Override
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + id));
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids)
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public void notifyGoalCompleted(Long userId, Long goalId) {
        GoalCompletedEvent event = new GoalCompletedEvent(userId, goalId);
        goalCompletedEventPublisher.publishGoalCompletedEvent(event);
    }
}
