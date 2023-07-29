package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.DtoDeactiv;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DeactivationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeactivatingService {

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final GoalRepository goalRepository;

    private final MentorshipService mentorshipService;

    @Transactional
    public DtoDeactiv deactivatingTheUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DeactivationException("there is no user", userId));
        if (!user.isActive()) {
            throw new DeactivationException("The user has already been deactivated", userId);
        }
        List<Goal> goals = goalRepository.findGoalsByUserId(userId).toList();
        goals.stream().filter(goal -> goal.getUsers().size() == 1).forEach(goalRepository::delete);

        List<Event> eventList = eventRepository.findAllByUserId(userId);
        eventRepository.deleteAll(eventList.stream().filter(event -> event.getOwner().getId() == userId).toList());

        mentorshipService.cancelMentoring(user, goals);

        user.setActive(false);
        userRepository.save(user);
        return new DtoDeactiv("The user is deactivated", userId);
    }

}
