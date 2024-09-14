package school.faang.user_service.service.goal;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
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
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalValidator goalValidator;
    private final UserValidator userValidator;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationRepository goalInvitationRepository;
    private final List<GoalInvitationFilter> goalInvitationFilters;

    private final static int MAX_LIMIT_ACTIVE_GOALS_FOR_USER = 3;

    public void deleteGoalInvitations(List<GoalInvitation> goalInvitations) {
        goalInvitations.forEach(goalInvitation -> goalInvitationRepository.deleteById(goalInvitation.getId()));
    }

    public void deleteGoalInvitationForUser(List<GoalInvitation> goalInvitations, User user) {
        goalInvitations.stream()
                .filter(invitation -> (Objects.equals(invitation.getInvited().getId(), user.getId()) ||
                        Objects.equals(invitation.getInviter().getId(), user.getId())))
                .forEach(invitation -> goalInvitationRepository.deleteById(invitation.getId()));
    }

    @Transactional
    public void createInvitation(GoalInvitationDto goalInvitationDto) {
        validateUsersId(goalInvitationDto.getInviterUserId(), goalInvitationDto.getInvitedUserId());
        validateGoalId(goalInvitationDto.getGoalId());
        userValidator.validateFirstUserIdAndSecondUserIdNotEquals(goalInvitationDto.getInviterUserId(),
                goalInvitationDto.getInvitedUserId(),
                "Inviter and invited cannot be equal");

        goalInvitationRepository.save(goalInvitationMapper.toEntity(goalInvitationDto));
    }

    @Transactional
    public void acceptGoalInvitation(long goalInvitationId) {
        GoalInvitation goalInvitation = getGoalInvitation(goalInvitationId);

        validateGoalId(goalInvitation.getGoal().getId());
        goalValidator.validateUserActiveGoalsAreLessThenIncoming(goalInvitation.getInvited().getId(),
                MAX_LIMIT_ACTIVE_GOALS_FOR_USER);
        goalValidator.validateUserNotWorkingWithGoal(goalInvitation.getInvited().getId(),
                goalInvitation.getGoal().getId());

        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        goalInvitationRepository.save(goalInvitation);
        goalInvitation.getGoal().getUsers().add(goalInvitation.getInvited());
    }

    @Transactional
    public void rejectGoalInvitation(long goalInvitationId) {
        GoalInvitation goalInvitation = getGoalInvitation(goalInvitationId);

        validateGoalId(goalInvitation.getGoal().getId());

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

    private GoalInvitation getGoalInvitation(long goalInvitationId) {
        return goalInvitationRepository.findById(goalInvitationId)
                .orElseThrow(() -> new ValidationException("No goal invitation with id " + goalInvitationId + " found"));
    }

    private void validateUsersId(Long firstUserId, Long secondUserId) {
        validateUserId(firstUserId);
        validateUserId(secondUserId);
    }

    private void validateUserId(Long userId) {
        userValidator.validateUserIdIsPositiveAndNotNull(userId);
        userValidator.validateUserIsExisted(userId);
    }

    private void validateGoalId(Long goalId) {
        goalValidator.validateGoalIdIsPositiveAndNotNull(goalId);
        goalValidator.validateGoalWithIdIsExisted(goalId);
    }
}
