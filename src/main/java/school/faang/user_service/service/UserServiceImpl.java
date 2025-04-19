package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private static final int SINGLE_USER = 1;

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;

    @Override
    public void deactivateUser(Long userId) {
        User user = findUserById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        stopUserGoals(user);
        stopUserEvents(user);
        deleteUserEvents(user);
        mentorshipService.stopMentoringIfMentor(user);

        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public Optional<User> findUserById(long userId) {
        return userRepository.findById(userId);
    }

    private void stopUserGoals(User user) {
        List<Goal> userGoals = goalRepository.findGoalsByUserId(user.getId()).toList();

        for (Goal goal : userGoals) {
            List<User> participants = goalRepository.findUsersByGoalId(goal.getId());

            if (participants.size() == SINGLE_USER && participants.get(0).getId().equals(user.getId())) {
                goalRepository.delete(goal);
            } else {
                participants.removeIf(participant -> participant.getId().equals(user.getId()));
                goal.setUsers(participants);

                if (goal.getMentor() != null && goal.getMentor().getId().equals(user.getId())) {
                    goal.setMentor(null);
                }

                goalRepository.save(goal);
            }
        }
    }

    private void stopUserEvents(User user) {
        eventRepository.findAllByUserId(user.getId()).forEach(event -> {
            event.setStatus(EventStatus.CANCELED);
            eventRepository.save(event);
        });
    }

    private void deleteUserEvents(User user) {
        List<Event> userEvents = eventRepository.findAllByUserId(user.getId());
        eventRepository.deleteAll(userEvents);
    }

    @Override
    public UserDto getUserById(long userId) {
        return userMapper.toDto(
                userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User doesn't exist with id = " + userId))
        );
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return users.stream().map(userMapper::toDto).toList();
    }

    @Override
    public void banUser(String userIdStr) {
        log.info("Start banning intruder with id: {}", userIdStr);
        Long userId = Long.parseLong(userIdStr);
        userRepository.findById(userId).ifPresent(user -> {
            user.setBanned(true);
            userRepository.save(user);
        });
        log.info("The intruder with id: {} has been banned", userId);
    }
  
}
