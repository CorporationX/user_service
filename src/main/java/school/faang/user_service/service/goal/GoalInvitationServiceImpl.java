package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalInvitationService;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class GoalInvitationServiceImpl implements GoalInvitationService {

    @Value("${logic.constants.max_active_goals}")
    private int maximumAllowedActiveGoals;
    private final ApplicationContext context;
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalInvitationMapper goalInvitationMapper;

    @Override
    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        Long inviterId = goalInvitationDto.getInviterId();
        Long invitedUserId = goalInvitationDto.getInvitedUserId();
        if (invitedUserId.equals(inviterId))
            throw new IllegalArgumentException("Inviter and Invited IDs are the same ");

        GoalInvitation goalInvitation = goalInvitationMapper.gIDTOToGoalInvitation(goalInvitationDto);

        Long goalId = goalInvitationDto.getGoalId();
        goalInvitation.setGoal(goalRepository.findById(goalId)
                .orElseThrow(() -> new NoSuchElementException("Goal id: " + goalId)));
        goalInvitation.setInviter(userRepository.findById(inviterId)
                .orElseThrow(() -> new NoSuchElementException("User id: " + inviterId)));
        goalInvitation.setInvited(userRepository.findById(invitedUserId)
                .orElseThrow(() -> new NoSuchElementException("User id: " + invitedUserId)));

        GoalInvitation created = goalInvitationRepository.saveAndFlush(goalInvitation);
        return goalInvitationMapper.gInvitationToGIDTO(created);
    }

    @Override
    @Transactional
    public void acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Invitation ID: " + id));

        Goal goal = goalInvitation.getGoal();
        User invited = goalInvitation.getInvited();

        if (goal == null) {
            throw new IllegalStateException("No existing goal in invitation");
        }

        if (isUserAlreadyWorksOnGoal(goal, invited)) {
            throw new UnsupportedOperationException("Invited user already works on goal");
        }

        if (isMaximumAllowedActiveGoalsReachedForUser(invited)) {
            throw new DataValidationException("User has Maximum allowed active goals");
        }

        goal.getUsers().add(invited);
        invited.getGoals().add(goal);
        GoalRepository goalRepository = context.getBean(GoalRepository.class);
        UserRepository userRepository = context.getBean(UserRepository.class);
        goalRepository.saveAndFlush(goal);
        userRepository.saveAndFlush(invited);
    }

    private boolean isMaximumAllowedActiveGoalsReachedForUser(User invited) {
        long activeGoalsOfInvited = invited
                .getGoals().stream()
                .filter(goal -> GoalStatus.ACTIVE == goal.getStatus())
                .count();
        return activeGoalsOfInvited >= maximumAllowedActiveGoals;
    }

    private boolean isUserAlreadyWorksOnGoal(Goal goal, User invited) {
        return invited
                .getGoals().stream()
                .anyMatch(goal::equals);
    }
}
