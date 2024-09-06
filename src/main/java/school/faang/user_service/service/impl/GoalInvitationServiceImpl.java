package school.faang.user_service.service.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalInvitationService;
import school.faang.user_service.service.goal.invitation_filter.InvitationFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Validated
public class GoalInvitationServiceImpl implements GoalInvitationService {

    private static final int MAX_ACTIVE_GOALS = 3;
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final List<InvitationFilter> filters;

    @Override
    @Transactional
    public GoalInvitationDto createInvitation(@Valid GoalInvitationDto goalInvitationDto) {
        GoalInvitation goalInvitation = goalInvitationRepository.save(getGoalInvitation(goalInvitationDto));
        return goalInvitationMapper.toDto(goalInvitation);
    }

    @Override
    @Transactional
    public GoalInvitationDto acceptGoalInvitation(@PositiveOrZero long id) {
        GoalInvitation goalInvitation = findGoalInvitationById(id);
        Goal goal = findGoalById(goalInvitation.getGoal().getId());
        User invited = goalInvitation.getInvited();

        if (checkIsGoalSuitableToAdd(invited, goal)) {
            addUserToGoal(goal, invited);
            goalInvitation = saveAcceptedGoalInvitation(goalInvitation);
        }

        return goalInvitationMapper.toDto(goalInvitation);
    }

    @Override
    @Transactional
    public GoalInvitationDto rejectGoalInvitation(@PositiveOrZero long id) {

        GoalInvitation rejectedGoalInvitation = createRejectedGoalInvitation(id);
        goalInvitationRepository.save(rejectedGoalInvitation);

        return goalInvitationMapper.toDto(rejectedGoalInvitation);
    }

    @Override
    public List<GoalInvitationDto> getInvitations(@Valid InvitationFilterDto invitationFilterDto) {
        Stream<GoalInvitation> invitations = getAllGoalInvitations().stream();
        return filters.stream()
                .filter(filter -> filter.isApplicable(invitationFilterDto))
                .reduce(
                        invitations,
                        (invites, filter) -> filter.apply(invites, invitationFilterDto),
                        (inv1, inv2) -> inv1
                )
                .map(goalInvitationMapper::toDto)
                .toList();
    }

    private List<GoalInvitation> getAllGoalInvitations() {
        return goalInvitationRepository.findAll();
    }

    private GoalInvitation createRejectedGoalInvitation(long id) {
        GoalInvitation goalInvitation = findGoalInvitationById(id);
        if (isGoalAvailable(goalInvitation.getGoal().getId())) {
            goalInvitation.setStatus(RequestStatus.REJECTED);
        }
        return goalInvitation;
    }

    private GoalInvitation saveAcceptedGoalInvitation(GoalInvitation goalInvitation) {
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        return goalInvitationRepository.save(goalInvitation);
    }

    private boolean isGoalAvailable(long goalId) {
        return findGoalById(goalId) != null;
    }

    private void addUserToGoal(Goal goal, User user) {
        goal.getUsers().add(user);
        goalRepository.save(goal);
    }

    private boolean checkIsGoalSuitableToAdd(User user, Goal goal) {
        return !user.getGoals().contains(goal) && user.getGoals().size() < MAX_ACTIVE_GOALS;
    }

    private GoalInvitation findGoalInvitationById(long id) {
        return goalInvitationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("GoalInvitation not found with id " + id));
    }

    private GoalInvitation getGoalInvitation(GoalInvitationDto goalInvitationDto) {

        checkUsersIdEquality(goalInvitationDto.getInvitedUserId(), goalInvitationDto.getInviterId());
        GoalInvitation goalInvitation = goalInvitationMapper.fromDto(goalInvitationDto);

        return uniteGoalAndUsers(goalInvitation, goalInvitationDto);
    }

    private GoalInvitation uniteGoalAndUsers(GoalInvitation goalInvitation, GoalInvitationDto goalInvitationDto) {

        goalInvitation.setInviter(findUserById(goalInvitationDto.getInviterId()));
        goalInvitation.setInvited(findUserById(goalInvitationDto.getInvitedUserId()));
        goalInvitation.setGoal(findGoalById(goalInvitationDto.getGoalId()));

        return goalInvitation;
    }

    private void checkUsersIdEquality(Long invitedId, Long inviterId) {
        if (invitedId.equals(inviterId)) {
            throw new IllegalArgumentException("Invited user and inviter user are the same");
        }
    }


    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    }

    private Goal findGoalById(Long id) {
        return goalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Goal not found with id " + id));
    }
}
