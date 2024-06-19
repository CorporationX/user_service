package school.faang.user_service.service.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.InvitationFilter;

import java.util.List;

import static school.faang.user_service.exception.message.MessageForGoalInvitationService.*;

@Service
@AllArgsConstructor
@Data
public class GoalInvitationService {

    private GoalInvitationRepository goalInvitationRepository;

    private GoalRepository goalRepository;

    private UserRepository userRepository;

    private GoalInvitationServiceValidator goalInvitationServiceValidator;

    private GoalInvitationMapper goalInvitationMapper;

    private List<InvitationFilter> invitationFilters;

    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        goalInvitationServiceValidator.validateForCreateInvitation(goalInvitationDto);

        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(goalInvitationDto);

        goalInvitation.setInviter(returnUser(goalInvitationDto.getInviterId(), NO_INVITER_IN_DB.getMessage()));
        goalInvitation.setInvited(returnUser(goalInvitationDto.getInvitedUserId(), NO_INVITED_IN_DB.getMessage()));
        goalInvitation.setGoal(goalRepository.findById(goalInvitationDto.getGoalId())
                .orElseThrow(() -> new DataValidationException(NO_GOAL_IN_DB.getMessage())));
        goalInvitation.setStatus(RequestStatus.PENDING);

        goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }

    public void acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).orElseThrow(() ->
                new DataValidationException(NO_GOAL_INVITATION_IN_DB.getMessage()));

        Goal goal = goalRepository.findById(goalInvitation.getGoal().getId()).orElseThrow(() ->
                new DataValidationException(NO_GOAL_IN_DB.getMessage()));

        User invited = goalInvitation.getInvited();

        List<Goal> setGoals = goalInvitationServiceValidator.validateForAcceptGoalInvitation(invited, goal);

        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        setGoals.add(goal);

        goalInvitationRepository.save(goalInvitation);
    }

    public void rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(NO_GOAL_INVITATION_IN_DB.getMessage()));

        goalRepository.findById(goalInvitation.getGoal().getId()).orElseThrow(() ->
                new DataValidationException(NO_GOAL_IN_DB.getMessage()));

        goalInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(goalInvitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filters) {
        List<GoalInvitation> goalInvitations = goalInvitationRepository.findAll();

        return goalInvitations
                .stream()
                .filter(goalInvitation -> invitationFilters
                        .stream()
                        .filter(filter -> filter.isApplicable(filters))
                        .flatMap(filter -> filter.apply(goalInvitation, filters))
                        .count() == invitationFilters.stream().filter(filter -> filter.isApplicable(filters)).count())
                .map(goalInvitation -> goalInvitationMapper.toDto(goalInvitation))
                .toList();
    }

    private User returnUser(Long id, String message) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(message));
    }
}
