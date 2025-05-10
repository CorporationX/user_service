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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalInvitationService;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.service.UserService;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class GoalInvitationServiceImpl implements GoalInvitationService {

    private final ApplicationContext context;

    private final GoalInvitationRepository goalInvitationRepository;

    private final GoalInvitationMapper goalInvitationMapper;

    @Value("${logic.constants.max_active_goals}")
    private final int MAX_ACTIVE_GOALS;

    @Override
    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        Long inviterId = goalInvitationDto.getInviterId();
        Long invitedUserId = goalInvitationDto.getInvitedUserId();
        if (invitedUserId.equals(inviterId))
            throw new IllegalArgumentException("Inviter and Invited IDs are the same ");

        //коммент для ревьювера: проверка на наличие юзеров в бд
        // происходит в user service, который вызывается используется в маппинге.
        GoalInvitation goalInvitation = goalInvitationMapper.gIDTOToGoalInvitation(
                goalInvitationDto,
                context.getBean(GoalService.class),
                context.getBean(UserService.class)
        );

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

        if (checkUserAlreadyWorksOnGoal(goal, invited)) {
            throw new UnsupportedOperationException("Invited user already works on goal");
        }

        if (checkMaximumAllowedActiveGoalsReachedForUser(invited)) {
            throw new DataValidationException("User has Maximum allowed active goals");
        }

        goal.getUsers().add(invited);
        invited.getGoals().add(goal);
        GoalRepository goalRepository = context.getBean(GoalRepository.class);
        UserRepository userRepository = context.getBean(UserRepository.class);
        goalRepository.saveAndFlush(goal);
        userRepository.saveAndFlush(invited);
    }

    private boolean checkMaximumAllowedActiveGoalsReachedForUser(User invited) {
        long activeGoalsOfInvited = invited
                .getGoals().stream()
                .filter(GoalService::goalIsActive)
                .count();
        return activeGoalsOfInvited >= MAX_ACTIVE_GOALS;
    }

    private boolean checkUserAlreadyWorksOnGoal(Goal goal, User invited) {
        return invited
                .getGoals().stream()
                .anyMatch(goal::equals);
    }
}
