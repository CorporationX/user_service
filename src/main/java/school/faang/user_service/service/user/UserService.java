package school.faang.user_service.service.user;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.validator.user.UserValidator;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;

    @Transactional
    public void deactivateAccount(Long userId) {
        userValidator.validateUser(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User with id " + userId + " not existed"));

        if (!user.getGoals().isEmpty()) {
            goalService.deactivateActiveUserGoals(user);
        }
        eventService.deactivatePlanningUserEventsAndDeleteEvent(user);
        user.setActive(false);

        mentorshipService.removeUserFromListHisMentees(user);
        user.getMentees().clear();
    }
}
