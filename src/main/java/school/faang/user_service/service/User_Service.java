package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.UserValidator;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class User_Service {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;

    public UserDto deactivate(Long userId) {
        userValidator.validateUserId(userId);
        User user = userRepository.findById(userId).get();
        removeEvents(userId);
        removeGoals(userId);
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        return userMapper.toUserDto(userRepository.save(user));
    }

    public UserDto getUserDtoById(Long userId) {
        userValidator.validateUserId(userId);
        return userMapper.toUserDto(userRepository.findById(userId).get());
    }

    public User getUserById(Long userId) {
        userValidator.validateUserId(userId);
        return userRepository.findById(userId).get();
    }

    private void removeGoals(Long userId) {
        User user = userRepository.findById(userId).get();
        if (!user.getGoals().isEmpty()) {
            user.getGoals().forEach(goal -> {
                goal.getUsers().remove(user);
                if (goal.getUsers().isEmpty()) {
                    goalRepository.delete(goal);
                }
            });
        }
    }

    private void removeEvents(Long userId) {
        User user = userRepository.findById(userId).get();
        if (!user.getOwnedEvents().isEmpty()) {
            user.getOwnedEvents().forEach(event -> {
                event.setStatus(EventStatus.CANCELED);
                eventRepository.save(event);
            });
        }
    }

}
