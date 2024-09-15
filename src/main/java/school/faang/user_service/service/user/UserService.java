package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;


    public UserDto deactivateUser(UserDto userDto) {
        log.info("Деактивация пользователя с ID: {}", userDto.getId());
        User user = userRepository.findById(userDto.getId()).orElseThrow(
                () -> new NoSuchElementException("Пользователь с ID " + userDto.getId() + " не найден"));
        stopUserActivities(user);
        user.setActive(false);
        mentorshipService.stopMentorship(user);
        userRepository.save(user);
        log.info("Пользователь с ID: {} был успешно деактивирован", userDto.getId());
        return userMapper.toDto(user);
    }

    public User stopUserActivities(User user) {
        log.info("Останавливаем активности для пользователя с ID: {}", user.getId());
        stopGoals(user);
        stopEvents(user);
        log.info("Активности пользователя с ID: {} остановлены", user.getId());
        return user;
    }

    public User stopGoals(User user) {
        List<Goal> goals = user.getGoals();
        log.info("Останавливаем цели для пользователя с ID: {}. Количество целей: {}", user.getId(), goals.size());
        for (Goal goal : goals) {
            if (goal.getUsers().size() == 1 && goal.getUsers().contains(user)) {
                log.info("Удаляем цель с ID: {} для пользователя с ID: {}", goal.getId(), user.getId());
                goalRepository.deleteById(goal.getId());
            }
        }
        user.setGoals(new ArrayList<>());
        log.info("Цели пользователя с ID: {} были очищены", user.getId());
        return user;
    }

    public User stopEvents(User user) {
        List<Event> ownedEvents = user.getOwnedEvents();
        List<Long> eventsIdList = new ArrayList<>();
        log.info("Останавливаем события для пользователя с ID: {}. Количество собственных событий: {}", user.getId(), ownedEvents.size());
        if (ownedEvents != null || !ownedEvents.isEmpty()) {
            for (Event event : ownedEvents) {
                eventsIdList.add(event.getId());
                log.info("Удаляем событие с ID: {} для пользователя с ID: {}", event.getId(), user.getId());
            }
            eventRepository.deleteAllById(eventsIdList);
        }
        user.setOwnedEvents(new ArrayList<>());

        List<Event> participatedEvents = user.getParticipatedEvents();
        log.info("Останавливаем участие в событиях для пользователя с ID: {}. Количество участвующих событий: {}", user.getId(), participatedEvents.size());
        if (participatedEvents != null || !participatedEvents.isEmpty()) {
            for (Event event : participatedEvents) {
                List<User> attendees = new ArrayList<>(event.getAttendees());
                attendees.remove(user);
                event.setAttendees(attendees);
                log.info("Удаляем пользователя с ID: {} из участников события с ID: {}", user.getId(), event.getId());
            }
            user.setParticipatedEvents(new ArrayList<>());
        }
        log.info("Участие пользователя с ID: {} в событиях было очищено", user.getId());
        return user;
    }
}