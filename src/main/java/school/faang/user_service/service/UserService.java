package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.notFoundExceptions.contact.UserNotFoundException;
import school.faang.user_service.mapper.mymappers.User1Mapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final User1Mapper mapper;
    private final GoalService goalService;
    private final EventService eventService;

    @Transactional
    public UserDto getUser(long id) {
        User foundUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        return mapper.toDto(foundUser);
    }

    @Transactional
    public List<UserDto> getUsersByIds(List<Long> userIds) {
        List<User> foundUsers = userRepository.findAllById(userIds);

        return mapper.toDtos(foundUsers);
    }

    @Transactional
    public UserDto deactivateUser(Long userId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with id: " + userId + " wasn`t found")
        );

        removeMentees(user);
        removeGoals(user);
        removeEvents(user);

        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());

        return mapper.toDto(userRepository.save(user));

    }

    private void removeMentees(User user) {
        if (user.getMentees() == null) return;
        user.getMentees().forEach(mentee -> {
            mentee.getMentors().remove(user);
            var goals = mentee.getSetGoals();
            if (goals != null) {
                goals.forEach(goal -> {
                            if (goal.getMentor() != null && goal.getMentor().getId() == user.getId()) {
                                goal.setMentor(null);
                                goalService.save(goal);
                            }
                        }
                );
            }
        });
    }

    private void removeGoals(User user) {
        if (user.getGoals() == null) return;
        user.getGoals().forEach(goal -> {
            goal.getUsers().remove(user);
            if (goal.getUsers().isEmpty()) {
                goalService.deleteGoal(goal.getId());
            }
        });
    }

    private void removeEvents(User user) {
        if (user.getOwnedEvents() == null) return;
        user.getOwnedEvents().forEach(event -> {
            event.setStatus(EventStatus.CANCELED);
            eventService.save(event);
        });
    }
}
