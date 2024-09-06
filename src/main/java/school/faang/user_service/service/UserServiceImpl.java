package school.faang.user_service.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final MentorshipService mentorshipService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;

    @Transactional
    public void deactivateUser(@NonNull Long id) {
        log.info("deactivating user with id: {}", id);
        goalRepository.deleteUnusedGoalsByMentorId(id);
        eventRepository.deleteAllByOwnerId(id);
        mentorshipService.stopMentorship(id);
        userRepository.updateUserActive(id, false);
        log.info("deactivated user with id: {}", id);
    }
}
