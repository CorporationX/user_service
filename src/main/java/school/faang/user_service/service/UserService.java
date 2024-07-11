package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final int USER_INACTIVE_DAYS_LIMIT = 90;

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final MentorshipService mentorshipService;

    @Transactional
    public UserDto deactivateUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        stopUserGoalActivities(user);
        stopPlannedEventActivities(user);
        user.setActive(false);
        stopMentorship(user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    private void stopUserGoalActivities(User user) {
        List<Goal> userGoals = user.getGoals();
        Iterator<Goal> iterator = userGoals.iterator();
        while (iterator.hasNext()) {
            Goal goal = iterator.next();
            goal.getUsers().remove(user);
            if (goal.getUsers().isEmpty()) {
                goalRepository.delete(goal);
                iterator.remove();
            } else {
                goalRepository.save(goal);
            }
        }
    }

    private void stopPlannedEventActivities(User user) {
        List<Event> ownedEvents = user.getOwnedEvents();
        for (Event event : ownedEvents) {
            if (event.getStatus() == EventStatus.PLANNED) {
                event.setStatus(EventStatus.CANCELED);
                eventRepository.save(event);
            }
        }
        ownedEvents.removeIf(event -> event.getStatus() == EventStatus.CANCELED);
    }

    private void stopMentorship(User user) {
        List<User> menteesWithoutMentor = user.getMentees().stream()
                .peek(mentee -> mentorshipService.deleteMentor(mentee.getId(), user.getId())).toList();
        menteesWithoutMentor.forEach(mentee -> mentee.getGoals().stream()
                .filter(goal -> goal.getMentor().equals(user))
                .peek(goal -> goal.setMentor(mentee))
                .forEach(goalRepository::save));
    }


    @Scheduled(cron = "@daily")
    public void deleteInactiveUsers() {
        userRepository.findAll().stream()
                .filter(user -> !user.isActive())
                .filter(user -> user.getUpdatedAt().plusDays(USER_INACTIVE_DAYS_LIMIT).isBefore(LocalDateTime.now()))
                .forEach(user -> {
                    userRepository.delete(user);
                    log.info("User with ID: {} was deleted", user.getId());
                });
    }
}