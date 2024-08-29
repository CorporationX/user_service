package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class InvintationDtoValidatorImpl implements InvintationDtoValidator {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    @Override
    public void validate(final GoalInvitationDto goalInvitationDto) {
        validateUserIsNotInvitingSelf(goalInvitationDto);
        validateUserExists(goalInvitationDto.getInviterId(), "Inviter");
        validateUserExists(goalInvitationDto.getInvitedUserId(), "Invited");
        validateGoalExists(goalInvitationDto.getGoalId());
    }

    private void validateUserIsNotInvitingSelf(final GoalInvitationDto goalInvitationDto) {
        if (goalInvitationDto.getInvitedUserId().equals(goalInvitationDto.getInviterId())) {
            throw new IllegalArgumentException("Invited user cannot be the same as the inviter.");
        }
    }

    private void validateUserExists(final Long userId, final String userType) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException(userType + " user with id: " + userId + " does not exist.");
        }
    }

    private void validateGoalExists(final Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new NoSuchElementException("Goal with id: " + goalId + " does not exist.");
        }
    }
}