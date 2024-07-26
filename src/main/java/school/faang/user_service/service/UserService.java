package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String MESSAGE_USER_NOT_EXIST = "User does not exist";
    private static final String MESSAGE_USER_ALREADY_DEACTIVATED = "User is already deactivated";
    private static final int ONE_USER = 1;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final MentorshipService mentorshipService;
    private final UserMapper mapper;

    public UserDto deactivatesUserProfile(long userId) {
        User user = getValidationUser(userId);
        List<Goal> goalsForDeleteFromDB = getGoalsForDelete(user);
        user.setGoals(null);
        deleteGoalFromDbIfPresent(goalsForDeleteFromDB);
        List<Event> events = getEventsWithCanceledStatus(user);
        events.forEach(event -> eventRepository.deleteById(event.getId()));
        user.setActive(false);
        return mapper.toDto(userRepository.save(mentorshipService.stopMentorship(user)));
    }

    public UserDto getUser(Long userId) {
        if(userId == null){
            throw new RuntimeException("userId is can't null");
        }
        Optional<User> user = userRepository.findById(userId);
        UserDto dto = mapper.toDto(user.orElse(null));
        return dto;
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        return ids.stream()
                .map(num -> userRepository.findById(num))
                .map(user -> mapper.toDto(user))
                .toList();
    }

    private void deleteGoalFromDbIfPresent(List<Goal> goalsForDeleteFromDB) {
        if (!goalsForDeleteFromDB.isEmpty()) {
            goalsForDeleteFromDB.forEach(goal -> goalRepository
                    .deleteById(goal.getId()));
        }
    }

    private User getValidationUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException(MESSAGE_USER_NOT_EXIST));
        if (!user.isActive()) {
            throw new RuntimeException(MESSAGE_USER_ALREADY_DEACTIVATED);
        }
        return user;
    }

    private List<Event> getEventsWithCanceledStatus(User user) {
        return user.getOwnedEvents().stream()
                .peek(event -> event.setStatus(EventStatus.CANCELED))
                .toList();
    }

    private List<Goal> getGoalsForDelete(User user) {
        return user.getGoals().stream()
                .filter(goal -> goal.getUsers().size() == ONE_USER)
                .toList();
    }

}
