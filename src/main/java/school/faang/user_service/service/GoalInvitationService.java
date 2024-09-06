package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.GoalInvitationValidationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GoalInvitationService {

    public static final int MAX_ACTIVE_USER_GOALS = 3;

    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    private final GoalInvitationMapper mapper;

    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {

        Long inviterId = goalInvitationDto.getInviterId();
        Long invitedUserId = goalInvitationDto.getInvitedUserId();

        if (inviterId.equals(invitedUserId)) {
            throw new GoalInvitationValidationException("User invited and user inviter cannot be equals, id " + invitedUserId);
        }

        Optional<User> optionalUserInviter = userRepository.findById(inviterId);
        if (optionalUserInviter.isEmpty()) {
            throw new GoalInvitationValidationException("User inviter with id " + inviterId + " user does not exist");
        }

        Optional<User> optionalUserInvited = userRepository.findById(invitedUserId);
        if (optionalUserInvited.isEmpty()) {
            throw new GoalInvitationValidationException("User invited with id " + invitedUserId + " user does not exist");
        }

        Long goalId = goalInvitationDto.getGoalId();
        Optional<Goal> optionalGoal = goalRepository.findById(goalId);
        if (optionalGoal.isEmpty()) {
            throw new GoalInvitationValidationException("Goal with id " + goalId + " does not exist");
        }

        // TODO: ??? в маппере нет смысла ? пусть будет на будущее?
        GoalInvitation entity = mapper.toEntity(goalInvitationDto);

        entity.setInviter(optionalUserInviter.get());
        entity.setInvited(optionalUserInvited.get());
        entity.setGoal(optionalGoal.get());
        entity.setStatus(RequestStatus.PENDING);

        GoalInvitation saved = goalInvitationRepository.save(entity);

        return mapper.toDto(saved);
    }

    public void acceptGoalInvitation(long id) {

        Optional<GoalInvitation> optionalGoalInvitation = goalInvitationRepository.findById(id);
        if (optionalGoalInvitation.isEmpty()) {
            throw new GoalInvitationValidationException("GoalInvitation with id " + id + " does not exist");
        }

        GoalInvitation goalInvitation = optionalGoalInvitation.get();

        RequestStatus invitationStatus = goalInvitation.getStatus();
        if (invitationStatus != RequestStatus.PENDING) {
            throw new GoalInvitationValidationException("Goal with id " + id + " is not in PENDING status (current status = " + invitationStatus + ")");
        }

        User invitedUser = goalInvitation.getInvited();
        List<Goal> invitedUserGoals = invitedUser.getGoals();

        if (invitedUserGoals.stream().anyMatch(goal -> goal.getId() == id)) {
            throw new GoalInvitationValidationException("User with id " + invitedUser.getId() + " already has goal with id " + id);
        }

        long activeGoalsAmount = invitedUserGoals.stream().filter(g -> g.getStatus() == GoalStatus.ACTIVE).count();
        if (activeGoalsAmount >= MAX_ACTIVE_USER_GOALS) {
            throw new GoalInvitationValidationException("User with id " + invitedUser.getId() + " has more or equals then " + MAX_ACTIVE_USER_GOALS
                    + " active goals (current amount = " + activeGoalsAmount + ")");
        }

        goalInvitationRepository.updateStatusById(RequestStatus.ACCEPTED, id);

        Goal goal = goalInvitation.getGoal();
        goal.getUsers().add(invitedUser);
        goalRepository.save(goal);
    }

    public void rejectGoalInvitation(long id) {
        Optional<GoalInvitation> optionalGoalInvitation = goalInvitationRepository.findById(id);
        if (optionalGoalInvitation.isEmpty()) {
            throw new GoalInvitationValidationException("GoalInvitation with id " + id + " does not exist");
        }

        GoalInvitation goalInvitation = optionalGoalInvitation.get();
        RequestStatus invitationStatus = goalInvitation.getStatus();
        if (invitationStatus == RequestStatus.REJECTED) {
            throw new GoalInvitationValidationException("Goal with id " + id + " is already in REJECTED status");
        }

        goalInvitationRepository.updateStatusById(RequestStatus.REJECTED, id);
    }
}
