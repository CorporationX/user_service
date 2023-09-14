package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final GoalService goalService;
    @Lazy
    private final EventService eventService;

    public User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DataValidationException("User was not found"));
    }

    public boolean areOwnedSkills(long userId, List<Long> skillIds) {
        if (skillIds.isEmpty()) {
            return true;
        }
        return userRepository.countOwnedSkills(userId, skillIds) == skillIds.size();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
  
    public UserDto getUser(long id) {
        return userMapper.toDto(findUserById(id));
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = StreamSupport.stream(userRepository.findAllById(ids).spliterator(), false).toList();
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public void deactivateUser(long userId) {
        User user = getUserById(userId);

        if (!user.isActive()) {
            throw new DataValidationException("User with id: " + userId + " is already deactivated");
        }

        holdActiveGoalsWithOneUser(user);
        discardActiveGoals(user);
        cancelOwnedPlannedEvents(user);
        discardPlannedEvents(user);

        userMapper.obfuscateUser(user);
        discardMentees(user);
    }

    private void holdActiveGoalsWithOneUser(User user) {
        for (Goal goal : user.getGoals()) {
            if ((goal.getStatus() == GoalStatus.ACTIVE) && (goal.getUsers().size() == 1)) {
                goalService.setGoalStatusOnHold(goal.getId());
            }
        }
    }

    private void discardActiveGoals(User user) {
        user.getGoals().removeIf(goal -> goal.getStatus() == GoalStatus.ACTIVE);
    }

    private void cancelOwnedPlannedEvents(User user) {
        for (Event event : user.getOwnedEvents()) {
            if (event.getStatus() == EventStatus.PLANNED
                    || event.getStatus() == EventStatus.IN_PROGRESS) {
                eventService.cancelEvent(event.getId());
            }
        }
    }

    private void discardPlannedEvents(User user) {
        user.getParticipatedEvents()
                .removeIf(
                        event -> event.getStatus() == EventStatus.PLANNED
                                || event.getStatus() == EventStatus.IN_PROGRESS
                );
    }

    private void discardMentees(User user) {
        user.setMentees(new ArrayList<>());
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User by id: " + userId + " not found"));
    }
}
