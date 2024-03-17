package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validation.user.UserValidator;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MentorshipService mentorshipService;
    private final EventService eventService;
    private final GoalService goalService;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final UserValidator userValidator;

    public UserDto create(UserDto userDto) {
        userValidator.validatePassword(userDto);
        userDto.setActive(true);
        User createdUser = userRepository.save(userMapper.toEntity(userDto));
        return userMapper.toDto(createdUser);
    }

    public UserDto getUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User doesn't exist by ID: " + userId));
        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        if (users.isEmpty()) {
            throw new EntityNotFoundException("Users with given IDs don't exist");
        }
        return userMapper.toDto(users);
    }

    public List<UserDto> getPremiumUsers(UserFilterDto filters) {
        List<User> premiumUsers = userRepository.findPremiumUsers().toList();
        if (!userFilters.isEmpty()) {
            userFilters.stream()
                    .filter(userFilter -> userFilter.isApplicable(filters))
                    .forEach(userFilter -> userFilter.apply(premiumUsers, filters));
        }
        return userMapper.toDto(premiumUsers);
    }

    public UserDto deactivateUser(long userId) {
        User userToDeactivate = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User doesn't exist by id: " + userId));

        if (userToDeactivate.getGoals() != null && !userToDeactivate.getGoals().isEmpty()) {
            List<Long> goalsToDeleteIds = userToDeactivate.getGoals().stream()
                    .filter(goal -> !GoalStatus.COMPLETED.equals(goal.getStatus()))
                    .peek(goal -> goal.getUsers().removeIf(user -> user.getId() == userId))
                    .filter(goal -> goal.getUsers().isEmpty())
                    .map(Goal::getId)
                    .toList();
            userToDeactivate.setGoals(Collections.emptyList());
            goalsToDeleteIds.forEach(goalService::deleteGoal);
        }

        if (userToDeactivate.getOwnedEvents() != null && !userToDeactivate.getOwnedEvents().isEmpty()) {
            List<Long> eventsToDeleteIds = userToDeactivate.getOwnedEvents().stream()
                    .filter(event -> EventStatus.PLANNED.equals(event.getStatus()))
                    .map(Event::getId)
                    .toList();
            userToDeactivate.setOwnedEvents(Collections.emptyList());
            eventsToDeleteIds.forEach(eventService::deleteEvent);
        }

        userToDeactivate.setActive(false);

        if (userToDeactivate.getMentees() != null && !userToDeactivate.getMentees().isEmpty()) {
            mentorshipService.deleteMentorForAllHisMentees(userId, userToDeactivate.getMentees());
            userToDeactivate.setMentees(Collections.emptyList());
        }
        return userMapper.toDto(userRepository.save(userToDeactivate));
    }
}

