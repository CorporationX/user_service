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
import school.faang.user_service.validator.goal.GoalValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalValidator goalValidator;
    private final UserValidator userValidator;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationRepository goalInvitationRepository;
    private final List<GoalInvitationFilter> goalInvitationFilters;

    private final int MAX_LIMIT_ACTIVE_GOALS_FOR_USER = 3;

    @Transactional
    public void createInvitation(GoalInvitationDto goalInvitationDto) {
        checkUsers(goalInvitationDto.getInviterUserId(), goalInvitationDto.getInvitedUserId());
        checkGoal(goalInvitationDto.getGoalId());
        userValidator.checkIfFirstUserIdAndSecondUserIdNotEqualsOrElseThrowException(goalInvitationDto.getInviterUserId(),
                goalInvitationDto.getInvitedUserId(),
                "Inviter and invited cannot be equal");

        goalInvitationRepository.save(goalInvitationMapper.toEntity(goalInvitationDto));
    }

    @Transactional
    public void acceptGoalInvitation(long goalInvitationId) {
        GoalInvitation goalInvitation = getGoalInvitationOrElseThrowException(goalInvitationId);

        checkGoal(goalInvitation.getGoal().getId());
        goalValidator.userActiveGoalsAreLessThenIncomingOrElseThrowException(goalInvitation.getInvited().getId(),
                MAX_LIMIT_ACTIVE_GOALS_FOR_USER);
        goalValidator.userNotWorkingWithGoalOrElseThrowException(goalInvitation.getInvited().getId(),
                goalInvitation.getGoal().getId());

        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        goalInvitationRepository.save(goalInvitation);
        goalInvitation.getGoal().getUsers().add(goalInvitation.getInvited());
    }

    @Transactional
    public void rejectGoalInvitation(long goalInvitationId) {
        GoalInvitation goalInvitation = getGoalInvitationOrElseThrowException(goalInvitationId);

        checkGoal(goalInvitation.getGoal().getId());

        goalInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(goalInvitation);
    }

    @Transactional(readOnly = true)
    public List<GoalInvitationDto> getInvitations(GoalInvitationFilterDto filter) {
        Stream<GoalInvitation> invitations = goalInvitationRepository.findAll().stream();
        if (filter != null) {
            invitations = goalInvitationFilters.stream()
                    .filter(goalInvitationFilter -> goalInvitationFilter.isApplicable(filter))
                    .reduce(invitations,
                            (stream, goalInvitationFilter) -> goalInvitationFilter.apply(stream, filter),
                            (s1, s2) -> s1);
        }
        return goalInvitationMapper.toDtos(invitations.toList());
    }

    private GoalInvitation getGoalInvitationOrElseThrowException(long goalInvitationId) {
        return goalInvitationRepository.findById(goalInvitationId)
                .orElseThrow(() -> new ValidationException("No goal invitation with id " + goalInvitationId + " found"));
    }

    private void checkUsers(Long firstUserId, Long secondUserId) {
        checkUser(firstUserId);
        checkUser(secondUserId);
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
