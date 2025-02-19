package school.faang.user_service.service.user;


import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.DeactivatedUserDto;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.DeactivatedUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.BadRequestException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.mapper.DeactivatedUserMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.adapter.EventParticipationRepositoryAdapter;
import school.faang.user_service.repository.adapter.EventRepositoryAdapter;
import school.faang.user_service.repository.adapter.GoalRepositoryAdapter;
import school.faang.user_service.repository.adapter.UserRepositoryAdapter;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;

    private final UserMapper userMapper;
    private final DeactivatedUserMapper deactivatedUserMapper;

    private final UserRepositoryAdapter userRepositoryAdapter;
    private final GoalRepositoryAdapter goalRepositoryAdapter;
    private final EventRepositoryAdapter eventRepositoryAdapter;
    private final EventParticipationRepositoryAdapter eventParticipationRepositoryAdapter;

    private final MentorshipService mentorshipService;
  
    public User getUser(Long userId) {
        if (userId == null) {
            log.error("User ID is null");
            throw new IllegalArgumentException("User ID must not be null");
        }

        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User with ID {} not found", userId);
            return new EntityNotFoundException("User with ID: " + userId + " not found");
        });
    }

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        List<User> users = userRepository.findPremiumUsers().toList();
        for (UserFilter filter : userFilters) {
            if (filter.isApplicable(userFilterDto)) {
                users = filter.apply(users, userFilterDto);
            }
        }

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserDto> getPremiumUsers() {
        return userRepository.findPremiumUsers()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto getUserById(Long userId) {
        return userMapper.toDto(userRepositoryAdapter.getById(userId));
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userMapper.toListDto(userRepositoryAdapter.getUsersByIds(ids));
    }

    @Transactional
    public DeactivatedUserDto deactivateUser(long id) {
        User user = userRepositoryAdapter.getById(id);

        if (!user.isActive()) {
            log.error("User with ID {} is already deactivated", id);
            throw new BadRequestException("User with ID " + id + " is already deactivated");
        }

        deleteUserGoals(user);
        deleteUserEvents(user);
        user.setActive(false);
        mentorshipService.stopMentorship(user);

        log.info("User with ID {} is deactivated", id);
        return deactivatedUserMapper.toDto(user);
    }

    private void deleteUserGoals(User user) {
        for (Goal goal : user.getGoals()) {
            if (goal.getUsers().size() < 2) {
                log.info("Goal with ID {} deleted", goal.getId());
                goalRepositoryAdapter.delete(goal);
            }
        }
        log.info("User's goals with ID {} have been deleted", user.getId());
        goalRepositoryAdapter.removeUserGoals(user.getId());
    }

    private void deleteUserEvents(User user) {
        List<Event> userEvents = user.getOwnedEvents();
        for (Event event : userEvents) {
            log.info("Registration for event with ID {} has been canceled for all users", event.getId());
            eventParticipationRepositoryAdapter.unregisterAll(event.getId());
        }
        log.info("All user events with ID {} have been deleted", user.getId());
        eventRepositoryAdapter.deleteAll(userEvents);
    }
}

