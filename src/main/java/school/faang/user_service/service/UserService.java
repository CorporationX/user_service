package school.faang.user_service.service;

import lombok.AllArgsConstructor;
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
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private UserMapper userMapper;
    private UserRepository userRepository;
    private GoalRepository goalRepository;
    private EventRepository eventRepository;
    private MentorshipRepository mentorshipRepository;
    private MentorshipService mentorshipService;

    @Transactional
    public UserDto deactivateUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        stopUserGoalActivities(user);
        stopPlannedEventActivities(user);
        user.setActive(false);
        stopMentorship(user);
        return userMapper.toDto(user);
    }

    private void stopUserGoalActivities(User user) {
        List<Goal> goals = user.getGoals();
        goals.forEach(goal -> {
            goal.getUsers().remove(user);
            if (goal.getUsers().isEmpty()) {
                goalRepository.delete(goal);
            }
        });
    }

    private void stopPlannedEventActivities(User user) {
        List<Event> ownedEvents = user.getOwnedEvents();
        user.getOwnedEvents().stream()
                .filter(event -> event.getStatus() == EventStatus.PLANNED)
                .peek(event -> event.setStatus(EventStatus.CANCELED))
                .forEach(ownedEvents::remove);
//                .forEach(event -> eventRepository.save(event));
    }

    private void stopMentorship(User user) {
        List<User> menteesWithoutMentor = user.getMentees().stream()
                .peek(mentee -> mentorshipService.deleteMentor(mentee.getId(), user.getId())).toList();
        menteesWithoutMentor
                .forEach(mentee -> mentee.getGoals().stream()
                        .filter(goal -> goal.getMentor().equals(user))
                        .forEach(goal -> goal.setMentor(mentee)));
//                        .forEach(goalRepository::save))
//                .forEach(mentorshipRepository::save);
    }
}