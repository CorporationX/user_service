package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.Deactiv;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DeactivatingService {

//    findUserById
//
//    updateGoal
//
//    updateEvent
//
//    deleteGoalById
//
//    deleteEventById

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final GoalRepository goalRepository;

    private final MentorshipRepository mentorshipRepository;

    @Transactional
    public Deactiv deactivatingTheUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new DataValidException("there is no user");
        }
        long idUser = user.get().getId();

        Stream<Goal> goals = goalRepository.findGoalsByUserId(idUser);
        goals.filter(goal -> goal.getUsers().size() == 1).forEach(goalRepository::delete);

        List<Event> eventList = eventRepository.findAllByUserId(idUser);
        eventRepository.deleteAll(eventList.stream().filter(event -> event.getOwner().getId() == idUser).toList());

        user.get().setActive(false);
        return new Deactiv("The user is deactivated", idUser);
    }

}
