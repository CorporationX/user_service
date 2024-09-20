package school.faang.user_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.validator.user.UserValidator;

@Service
@RequiredArgsConstructor
public class UserDeactivationService {

    private final UserService userService;
    private final GoalService goalService;
    private final EventService eventService;
    private final UserValidator userValidator;
    private final MentorshipService mentorshipService;

    @Transactional
    public void deactivateAccount(Long userId) {
        userValidator.validateUserIdIsPositiveAndNotNull(userId);

        User user = userService.getUserById(userId);

        if (!user.getGoals().isEmpty()) {
            goalService.deactivateActiveUserGoals(user);
        }
        eventService.deactivatePlanningUserEventsAndDeleteEvent(user);
        user.setActive(false);

        mentorshipService.removeUserFromListHisMentees(user);
        user.getMentees().clear();
    }

}
