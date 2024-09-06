package school.faang.user_service.service.goal;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.goalInvitation.GoalInvitationFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final UserService userService;
    private final GoalValidator goalValidator;
    private final UserValidator userValidator;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationRepository goalInvitationRepository;
    private final List<GoalInvitationFilter> goalInvitationFilters;

    private final int MAX_LIMIT_ACTIVE_GOALS_FOR_USER = 3;

    public void createInvitation(GoalInvitationDto goalInvitationDto) {
        checkUser(goalInvitationDto.getInviterId());
        checkUser(goalInvitationDto.getInvitedUserId());
        checkGoal(goalInvitationDto.getGoalId());

        if (isInviterAndInvitedAreEquals(goalInvitationDto.getInviterId(), goalInvitationDto.getInvitedUserId())) {
            throw new ValidationException("Inviter and invited cannot be equal");
        }

        goalInvitationRepository.save(goalInvitationMapper.toEntity(goalInvitationDto));
    }

    @Transactional
    public void acceptGoalInvitation(long goalInvitationId) {
        GoalInvitation goalInvitation = getGoalInvitationOrElseThrowException(goalInvitationId);

        checkGoal(goalInvitation.getGoal().getId());

        goalValidator.userActiveGoalsAreLessThenIncomingOrElseThrowException(goalInvitation.getInvited().getId(),
                MAX_LIMIT_ACTIVE_GOALS_FOR_USER);
        goalValidator.userNotWorkingWithGoalOrElseThrowException(goalInvitation.getGoal().getId(),
                goalInvitation.getGoal().getId());

        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        goalInvitationRepository.save(goalInvitation);
        userService.addGoalToUserGoals(goalInvitation.getInvited().getId(), goalInvitation.getGoal());
    }

    public void rejectGoalInvitation(long goalInvitationId) {
        GoalInvitation goalInvitation = getGoalInvitationOrElseThrowException(goalInvitationId);

        checkGoal(goalInvitation.getGoal().getId());

        goalInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(goalInvitation);
    }

    public List<GoalInvitationDto> getInvitations(GoalInvitationFilterDto filter) {
        List<GoalInvitation> invitations = goalInvitationRepository.findAll();
        goalInvitationFilters.stream()
                .filter(goalInvitationFilter -> goalInvitationFilter.isApplicable(filter))
                .forEach(goalInvitationFilter -> goalInvitationFilter
                        .apply(invitations, filter));
        return goalInvitationMapper.toDto(invitations);
    }

    private GoalInvitation getGoalInvitationOrElseThrowException(long goalInvitationId) {
        return goalInvitationRepository.findById(goalInvitationId)
                .orElseThrow(() -> new ValidationException("No goal invitation with id " + goalInvitationId + " found"));
    }

    private boolean isInviterAndInvitedAreEquals(Long inviterId, Long inventedId) {
        return Objects.equals(inviterId, inventedId);
    }

    private void checkUser(Long userId) {
        userValidator.userIdIsPositiveAndNotNullOrElseThrowValidationException(userId);
        userValidator.userIsExistedOrElseThrowValidationException(userId);
    }

    private void checkGoal(Long goalId) {
        goalValidator.goalIdIsPositiveAndNotNullOrElseThrowValidationException(goalId);
        goalValidator.goalIsExistedOrElseThrowException(goalId);
    }
}
