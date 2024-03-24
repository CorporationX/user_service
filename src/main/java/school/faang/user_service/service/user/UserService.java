package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.handler.exception.DataValidationException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto deactivationUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new DataValidationException("Пользователь не найден"));
        user.getGoals().removeIf(goal -> goal.getUsers().size() == 1 && goal.getUsers().contains(user));
        user.getOwnedEvents().stream().forEach(event -> event.setStatus(EventStatus.CANCELED));
        user.getOwnedEvents().removeIf(event -> EventStatus.CANCELED.equals(event.getStatus()));
        user.setActive(false);
        for (User mentee : user.getMentees()) {
            for (Goal goal : mentee.getSetGoals()) {
                if (goal.getMentor().getId() == user.getId()) {
                    mentee.getSetGoals().remove(goal);
                    mentee.getGoals().add(goal);
                }
            }
        }
        User userSave = userRepository.save(user);
        return userMapper.toDto(userSave);
    }
}
