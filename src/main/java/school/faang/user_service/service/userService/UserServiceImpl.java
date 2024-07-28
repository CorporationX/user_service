package school.faang.user_service.service.userService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorshipService.MentorshipServiceImpl;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipServiceImpl mentorshipService;

    public void deactivateUser(long userId){

        List<Event> userEvents = eventRepository.findAllByUserId(userId);
        eventRepository.deleteAllById(userEvents.stream().map(event -> event.getId()).toList());


    }
}
