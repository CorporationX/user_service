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
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;

    @Override
    public List<UserDto> getPremiumUsers(UserFilterDto filters) {
        var premiumUsers = userRepository.findPremiumUsers();
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(premiumUsers, (stream, filter) -> filter.apply(stream, filters),
                        (newStream, oldStream) -> newStream)
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public void deactivateUserProfile(long id) {
        User userToDeactivate = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: %d does not exist"));

        removeGoals(userToDeactivate);
        removeEvents(userToDeactivate);
        userToDeactivate.setActive(false);
        stopMentorship(userToDeactivate);

        userRepository.save(userToDeactivate);
    }

    @Override
    public User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                "User with ID: %d does not exist.".formatted(userId)));
    }

    private void stopMentorship(User userToDeactivate) {
        mentorshipService.deleteMentorFromMentees(userToDeactivate.getId(), userToDeactivate.getMentees());
    }

    private void removeEvents(User userToDeactivate) {
        List<Event> eventsToRemove = userToDeactivate.getOwnedEvents()
                .stream()
                .filter(event -> event.getStatus() == EventStatus.PLANNED)
                .peek(event -> event.setStatus(EventStatus.CANCELED))
                .toList();

        eventService.deleteEvents(eventsToRemove);
    }

    private void removeGoals(User userToDeactivate) {
        List<Goal> goalsToRemove = userToDeactivate.getGoals()
                .stream()
                .peek(goal -> goal.getUsers().removeIf(user -> user.getId().equals(userToDeactivate.getId())))
                .filter(goal -> goal.getUsers().isEmpty())
                .toList();

        goalService.removeGoals(goalsToRemove);
    }

    @Override
    public UserDto getUser(long userId) {
        return userMapper.toDto(findUserById(userId));
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .map(userMapper::toDto)
                .toList();
    }
}
