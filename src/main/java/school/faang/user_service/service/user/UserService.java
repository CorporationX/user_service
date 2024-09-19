package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;

    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;

    @Transactional
    public void deactivateAccount(Long userId) {
        userValidator.validateUserIdIsPositiveAndNotNull(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not existed"));

        if (!user.getGoals().isEmpty()) {
            goalService.deactivateActiveUserGoals(user);
        }
        eventService.deactivatePlanningUserEventsAndDeleteEvent(user);
        user.setActive(false);

        mentorshipService.removeUserFromListHisMentees(user);
        user.getMentees().clear();
    }

    public UserDto getUser(long userId) {
        userValidator.validateUserIdIsPositiveAndNotNull(userId);

        User existedUser = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User with id " + userId + " does not exist"));

        return userMapper.toDto(existedUser);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        ids.forEach(userValidator::validateUserIdIsPositiveAndNotNull);

        return userMapper.toDtos(userRepository.findAllById(ids));
    }
}
