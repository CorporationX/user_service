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

    public void validate(final GoalInvitationDto goalInvitationDto) {
        if (goalInvitationDto.getInvitedUserId().equals(goalInvitationDto.getInviterId())) {
            throw new IllegalArgumentException("Exception invited user can`t be invitor ");
        }
        if (!userRepository.existsById(goalInvitationDto.getInviterId())) {
            throw new NoSuchElementException("User with id:" + goalInvitationDto.getInviterId() + " doesn't exist!");
        }
        if (!userRepository.existsById(goalInvitationDto.getInvitedUserId())) {
            throw new NoSuchElementException("User with id:" + goalInvitationDto.getInvitedUserId() + " doesn't exist!");
        }
        if (!goalRepository.existsById(goalInvitationDto.getGoalId())) {
            throw new NoSuchElementException("Goal with id:" + goalInvitationDto.getGoalId() + " doesn't exist!");
        }
    }
}
