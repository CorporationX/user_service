package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalInvitationServiceImpl implements GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final UserContext userContext;
    private static final int MAX_ACTIVE_GOALS = 3;


    @Override
    public GoalInvitationDto create(long goalId, CreateGoalInvitationDto invitationDto) {
        Long invitedUserId = invitationDto.getInvitedUserId();
        if (invitedUserId == null) {
            throw new DataValidationException("Invited user ID must not be null");
        }

        Long inviterId = userContext.getUserId();
        if (inviterId.equals(invitedUserId)) {
            throw new ForbiddenException("You cannot invite yourself");
        }


        log.info("User {} is inviting user {} to goal {}", inviterId, invitedUserId, goalId);

        List<User> goalUsers = goalRepository.findUsersByGoalId(goalId);
        boolean alreadyParticipant = goalUsers.stream()
                .anyMatch(u -> u.getId().equals(invitedUserId));
        if (alreadyParticipant) {
            throw new DataValidationException("User already participates in this goal");
        }

        int activeGoals = goalRepository.countActiveGoalsPerUser(invitedUserId);
        if (activeGoals >= MAX_ACTIVE_GOALS) {
            throw new DataValidationException("User already has 3 active goals");
        }


        Goal goal = goalRepository.getByIdOrThrow(goalId);
        User inviter = userRepository.getByIdOrThrow(inviterId);
        User invited = userRepository.getByIdOrThrow(invitedUserId);

        GoalInvitation invitation = new GoalInvitation();
        invitation.setGoal(goal);
        invitation.setInviter(inviter);
        invitation.setInvited(invited);
        invitation.setStatus(RequestStatus.PENDING);

        GoalInvitation saved = goalInvitationRepository.save(invitation);
        log.info("Created invitation id={} from user {} to user {} for goal {}",
                saved.getId(), inviterId, invitedUserId, goalId);

        return goalInvitationMapper.toGoalInvitationDto(saved);
    }

    @Override
    public void accept(long invitationId) {
        GoalInvitation invitation = goalInvitationRepository.getByIdOrThrow(invitationId);

        if (invitation.getStatus() == RequestStatus.ACCEPTED) {
            throw new ForbiddenException("Приглашение уже принято");
        }
        if (invitation.getStatus() == RequestStatus.REJECTED) {
            throw new ForbiddenException("Приглашение уже отклонено");
        }

        invitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitationRepository.save(invitation);

    }

    @Override
    public void reject(long invitationId) {
        GoalInvitation invitation = goalInvitationRepository.getByIdOrThrow(invitationId);

        if (invitation.getStatus() == RequestStatus.ACCEPTED) {
            throw new DataValidationException("Приглашение уже принято и не может быть отклонено");
        }
        if (invitation.getStatus() == RequestStatus.REJECTED) {
            throw new DataValidationException("Приглашение уже отклонено");
        }

        invitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(invitation);
    }

    @Override
    public List<GoalInvitationDto> getByFilters(GoalInvitationFilterDto filters) {
        List<GoalInvitation> invitations = goalInvitationRepository.findAll();

        return invitations.stream()
                .filter(inv -> filters.getInviterId() == null
                        || inv.getInviter().getId().equals(filters.getInviterId()))
                .filter(inv -> filters.getInvitedId() == null
                        || inv.getInvited().getId().equals(filters.getInvitedId()))
                .filter(inv -> filters.getStatus() == null
                        || inv.getStatus() == filters.getStatus())
                .map(goalInvitationMapper::toGoalInvitationDto)
                .toList();
    }

}
