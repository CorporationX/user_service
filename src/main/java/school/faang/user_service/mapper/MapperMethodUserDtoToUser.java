package school.faang.user_service.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MapperMethodUserDtoToUser {
    private static final String MESSAGE_EVENT_NOT_IN_DB = "Event is not in database";
    private static final String MESSAGE_USER_NOT_IN_DB = "The user is not in the database";
    private static final String MESSAGE_GOAL_NOT_IN_DB = "The goal is not in the database";
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;

    @Named("idsToOwnedEvents")
    public List<Event> idsToOwnedEvents(List<Long> eventIds) {
        return eventIds.stream()
                .map(id -> eventRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException(MESSAGE_EVENT_NOT_IN_DB)))
                .toList();
    }

    @Named("idsToMenteesOrMentors")
    public List<User> idsToMenteesOrMentors(List<Long> userIds) {
        return userIds.stream()
                .map(id -> userRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException(MESSAGE_USER_NOT_IN_DB)))
                .toList();
    }

    @Named("idsToSetGoalsOrGoal")
    public List<Goal> idsToSetGoalsOrGoal(List<Long> goalId) {
        return goalId.stream()
                .map(id -> goalRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException(MESSAGE_GOAL_NOT_IN_DB)))
                .toList();
    }
}
