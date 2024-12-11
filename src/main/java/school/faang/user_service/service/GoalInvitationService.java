package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.filter.goalInvitationFilters.GoalInvitationFilter;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.GoalInvitationValidator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalInvitationService {
    private static final int ACCEPTABLE_NUMBERS_OF_GOALS = 3;

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final UserService userService;
    private final GoalService goalService;
    private final List<GoalInvitationFilter> filters;
    private final GoalInvitationValidator validator;

    @Transactional
    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        log.info("Request to create a new goal invitation. : {}", goalInvitationDto);
        validator.validateInvitation(goalInvitationDto);
        GoalInvitation goalInvitation = initializeGoalInvitation(goalInvitationDto);
        goalInvitation = goalInvitationRepository.save(goalInvitation);
        log.info("Created goal invitation: {}", goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }

    @Transactional
    public GoalInvitationDto acceptGoalInvitation(long goalInvitationId) {
        GoalInvitation goalInvitation = findById(goalInvitationId);
        log.info("Request to accept a goal invitation : {}", goalInvitation);
        validateGoalInvitationAcceptance(goalInvitation);

        User invitedUser = goalInvitation.getInvited();
        invitedUser.addGoal(goalInvitation.getGoal());
        userService.saveUser(invitedUser);

        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        log.info("User {} accepted invitation: {}", goalInvitation.getInvited(), goalInvitation);
        goalInvitation = goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }

    @Transactional
    public GoalInvitationDto rejectGoalInvitation(long goalInvitationId) {
        GoalInvitation goalInvitation = findById(goalInvitationId);

        goalInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitation = goalInvitationRepository.save(goalInvitation);
        log.info("Rejected invitation: {}", goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filterDto) {
        Stream<GoalInvitation> goalInvitationStream = goalInvitationRepository.findAll().stream();
        List<GoalInvitation> goalInvitations = filters.stream()
                .filter(f -> f.isApplicable(filterDto))
                .reduce(goalInvitationStream, (stream, filter) -> filter.apply(stream, filterDto),
                        ((subGenStream, stream) -> stream))
                .distinct()
                .toList();

        return goalInvitationMapper.toDtoList(goalInvitations);
    }

    private void validateGoalInvitationAcceptance(GoalInvitation goalInvitation) {
        long invitedId = goalInvitation.getInvited().getId();
        long inviterId = goalInvitation.getInviter().getId();
        if (goalService.countActiveGoalsPerUser(invitedId) >= ACCEPTABLE_NUMBERS_OF_GOALS) {
            throw new DataValidationException("The user has the maximum number of goals");
        }
        if (validateWhetherTheUserHasProposedGoal(invitedId, inviterId)) {
            throw new DataValidationException("The user is already working on this goal");
        }
    }

    private boolean validateWhetherTheUserHasProposedGoal(Long userId, Long goalId) {
        List<Goal> userGoals = goalService.findGoalsByUserId(userId).toList();
        if (userGoals.isEmpty()) {
            return false;
        }
        return userGoals.stream()
                .map(Goal::getId)
                .anyMatch(id -> Objects.equals(id, goalId));
    }

    public GoalInvitation findById(long id) {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found for id: %s".formatted(id)));
    }

    private GoalInvitation initializeGoalInvitation(GoalInvitationDto goalInvitationDto) {
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(goalInvitationDto);

        goalInvitation.setInviter(userService.getUserById(goalInvitationDto.inviterId()));
        goalInvitation.setInvited(userService.getUserById(goalInvitationDto.invitedUserId()));
        goalInvitation.setGoal(goalService.findGoalById(goalInvitationDto.goalId()));
        return goalInvitation;
    }
}
