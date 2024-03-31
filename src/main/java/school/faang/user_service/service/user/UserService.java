package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.handler.exception.DataValidationException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.goal.GoalService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MentorshipService mentorshipService;
    private final GoalService goalService;
    private final EventRepository eventRepository;

    public UserDto deactivationUserById(Long userId) {
        User userDeactivate = userRepository.findById(userId).orElseThrow(() -> new DataValidationException("Пользователь с id: "+ userId+" не найден"));
        if (userDeactivate.getGoals() != null && !userDeactivate.getGoals().isEmpty()) {
            List<Long> deleteGoals = userDeactivate.getGoals().stream().filter(goal -> !GoalStatus.COMPLETED.equals(goal.getStatus()))
                    .peek(goal -> goal.getUsers().removeIf(user -> user.getId() == userId))
                    .filter(goal -> goal.getUsers().isEmpty())
                    .map(goal -> goal.getId())
                    .toList();
            userDeactivate.setGoals(Collections.emptyList());
            deleteGoals.forEach(deleteGoal -> goalService.deleteGoal(deleteGoal));
        }

        if (userDeactivate.getOwnedEvents() != null && !userDeactivate.getOwnedEvents().isEmpty()) {
            List<Long> deleteEvents = userDeactivate.getOwnedEvents().stream().filter(event -> EventStatus.PLANNED.equals(event.getStatus()))
                    .map(event -> event.getId())
                    .toList();
            userDeactivate.setOwnedEvents(Collections.emptyList());
            deleteEvents.forEach(deleteEvent -> eventRepository.deleteById(deleteEvent));
        }

        userDeactivate.setActive(false);

        if (userDeactivate.getMentees() != null && !userDeactivate.getMentees().isEmpty()) {
            mentorshipService.deleteMentorForHisMentees(userId, userDeactivate.getMentees());
            userDeactivate.setMentees(Collections.emptyList());
        }

        User savedUser = userRepository.save(userDeactivate);
        return userMapper.toDto(savedUser);
    }
}
