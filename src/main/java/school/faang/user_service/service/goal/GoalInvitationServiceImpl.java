package school.faang.user_service.service.goal;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.dto.goal.SortOption;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepositoryAdapter;
import school.faang.user_service.repository.user.UserRepositoryAdapter;
import school.faang.user_service.service.promotion.utils.GoalProperties;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalInvitationServiceImpl implements GoalInvitationService {
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepositoryAdapter userRepositoryAdapter;
    private final GoalRepositoryAdapter goalRepositoryAdapter;
    private final InvitationBoolBuilderConstructor invitationBoolBuilderConstructor;
    private final GoalProperties goalProperties;

    @Override
    @Transactional
    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        log.debug("Execution of the method createInvitation, parameters: goalInvitationDto = {}", goalInvitationDto);
        Objects.requireNonNull(goalInvitationDto, "passed goalInvitationDto cannot be null");
        User inviter = userRepositoryAdapter.findById(goalInvitationDto.getInviterId());
        User invited = userRepositoryAdapter.findById(goalInvitationDto.getInvitedId());
        compareIfDifferentUsers(inviter, invited);
        Goal goal = goalRepositoryAdapter.findById(goalInvitationDto.getGoalId());

        if (!invited.getGoals().isEmpty()) {
            checkIfHasAGoal(invited.getGoals(), goal);
        }
        checkIfWasInvited(invited.getReceivedGoalInvitations(), goal);
        GoalInvitation goalInvitation = goalInvitationMapper.toGoalInvitation(inviter, invited, goal);
        goal.getInvitations().add(goalInvitation);
        GoalInvitation savedGoalInvitation = goalInvitationRepository.save(goalInvitation);
        log.debug("Saved goalInvitation = {}", savedGoalInvitation);
        return goalInvitationMapper.toGoalInvitationDto(savedGoalInvitation);
    }

    @Override
    @Transactional
    public void acceptGoalInvitation(long id) {
        log.debug("Execution of the method acceptGoalInvitation, parameters: id={}", id);
        GoalInvitation goalInvitation = getGoalInvitationById(id);
        User invited = goalInvitation.getInvited();
        Goal goal = goalInvitation.getGoal();
        List<Goal> invitedUserGoals = invited.getGoals();

        validateGoals(invitedUserGoals, goal);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goal.getUsers().add(invited);
        invitedUserGoals.add(goal);
        log.debug("Accepted goalInvitation = {}", goalInvitation);
    }

    @Override
    @Transactional
    public void rejectGoalInvitation(long id) {
        log.debug("Execution of the method rejectGoalInvitation, parameters: id={}", id);
        GoalInvitation goalInvitation = getGoalInvitationById(id);
        goalInvitation.setStatus(RequestStatus.REJECTED);
        log.debug("Rejected goalInvitation = {}", goalInvitation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalInvitationDto> getAllInvitations(InvitationFilterDto invitationFilterDto) {
        log.debug("Execution of the method getAllInvitations, parameters: invitationFilterDto={}", invitationFilterDto);
        Objects.requireNonNull(invitationFilterDto, "passed invitationFilterDto cannot be null");
        BooleanBuilder queryBooleanBuilder = invitationBoolBuilderConstructor.getQueryBooleanBuilder(invitationFilterDto);
        PageRequest pageRequest = PageRequest.of(invitationFilterDto.getOffset(), invitationFilterDto.getSize());

        List<GoalInvitation> goalInvitations = goalInvitationRepository.findAll(queryBooleanBuilder, pageRequest)
                .getContent();
        if (invitationFilterDto.getSort() != null) {
            goalInvitations = getSortedGoalInvitations(goalInvitations, invitationFilterDto.getSort());
        }
        log.debug("Loaded List<GoalInvitation>, size = {}", goalInvitations.size());
        return goalInvitationMapper.toGoalInvitations(goalInvitations);
    }

    private void checkIfHasAGoal(List<Goal> goals, Goal goal) {
        if (goals.contains(goal)) {
            throw new IllegalArgumentException("goal already exists");
        }
    }

    private void checkIfWasInvited(List<GoalInvitation> receivedGoalInvitations, Goal goal) {
        receivedGoalInvitations.stream()
                .filter(invitation -> invitation.getGoal().getId().equals(goal.getId()))
                .findFirst()
                .ifPresent(invitation -> {
                    throw new IllegalArgumentException(String.format("This user has been already invited to a goal " +
                            "with id %d", goal.getId()));
                });
    }

    private GoalInvitation getGoalInvitationById(long id) {
        log.debug("Execution of the method getGoalInvitationById, parameters: id={}", id);
        return goalInvitationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No goal invitation found with с id=%d ", id)));
    }

    private void compareIfDifferentUsers(User user1, User user2) {
        Objects.requireNonNull(user1, "user1 must not be null");
        Objects.requireNonNull(user2, "user2 must not be null");
        if (Objects.equals(user1.getId(), user2.getId())) {
            throw new IllegalArgumentException("User cannot invite to a Goal execution himself/herself");
        }
    }

    private void validateGoals(List<Goal> goals, Goal goalToInvite) {
        Objects.requireNonNull(goals, "List of goals cannot be null");
        Objects.requireNonNull(goalToInvite, "Goal to invite cannot be null");
        long numOfGoals = goals.stream()
                .filter(Objects::nonNull)
                .filter(goal -> GoalStatus.ACTIVE == goal.getStatus())
                .count();
        long maxActiveGoals = goalProperties.getMaxActiveGoals();
        log.info("Number of active goals = {}", maxActiveGoals);
        if (numOfGoals >= maxActiveGoals) {
            throw new IllegalArgumentException(String.format("Number of active goals cannot be greater that %d",
                    maxActiveGoals));
        }
        if (goals.contains(goalToInvite)) {
            throw new IllegalArgumentException(String.format("Goal with id %s is already " +
                    "present", goalToInvite.getId()));
        }
    }

    private List<GoalInvitation> getSortedGoalInvitations(List<GoalInvitation> goalInvitations, SortOption sort) {
        return switch (sort) {
            case GOAL_ID -> goalInvitations.stream()
                    .sorted(Comparator.comparingLong(invitation -> invitation.getGoal().getId()))
                    .collect(Collectors.toList());
            case INVITER_ID -> goalInvitations.stream()
                    .sorted(Comparator.comparingLong(invitation -> invitation.getInviter().getId()))
                    .collect(Collectors.toList());
            case CREATED_AT -> goalInvitations.stream()
                    .sorted(Comparator.comparing(GoalInvitation::getCreatedAt))
                    .collect(Collectors.toList());
            case STATUS -> goalInvitations.stream()
                    .sorted(Comparator.comparing(GoalInvitation::getStatus))
                    .collect(Collectors.toList());
        };
    }
}

