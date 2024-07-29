package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
        private final UserMapper userMapper;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final MentorshipService mentorshipService;


    public UserDto findUserById(long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }

    public List<UserDto> findUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public UserDto deactivateUserById(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        stopUserGoals(user);
        deleteUserEvents(user);
        user.setActive(false);
        mentorshipService.stopUserMentorship(user.getId());

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    private void stopUserGoals(User user) {
        for (Goal goal : user.getGoals()) {
            var userList = goal.getUsers();
            if (userList != null) {
                if (userList.size() == 1 && userList.get(0).getId() == user.getId()) {
                    goalRepository.delete(goal);
                } else {
                    userList.remove(user);
                    goalRepository.save(goal);
                }
            }
        }
    }

    private void deleteUserEvents(User user) {
        eventRepository.deleteAll(user.getParticipatedEvents());
    }
}
