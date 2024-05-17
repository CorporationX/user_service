package school.faang.user_service.service;

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
import school.faang.user_service.service.filter.*;

import java.util.List;

import static school.faang.user_service.exception.MessageForGoalInvitationService.*;

@Service
@AllArgsConstructor
@Data
public class GoalInvitationService {

    private GoalInvitationRepository goalInvitationRepository;
    private GoalRepository goalRepository;
    private UserRepository userRepository;
    private GoalInvitationMapper goalInvitationMapper;
    private GoalInvitationServiceValidator goalInvitationServiceValidator;
    static final int SETGOAL_SIZE = 3;

    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        goalInvitationServiceValidator.validateForCreateInvitation(goalInvitationDto);

        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(goalInvitationDto);

        goalInvitation.setInviter(userRepository.findById(goalInvitationDto.getInviterId()).
                orElseThrow(() -> new DataValidationException(NO_INVITER_IN_DB.getMessage())));
        goalInvitation.setInvited(userRepository.findById(goalInvitationDto.getInvitedUserId()).
                orElseThrow(() -> new DataValidationException(NO_INVITED_IN_DB.getMessage())));
        goalInvitation.setGoal(goalRepository.findById(goalInvitationDto.getGoalId()).
                orElseThrow(() -> new DataValidationException(NO_GOAL_IN_DB.getMessage())));

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
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).
                orElseThrow(() -> new DataValidationException(NO_GOAL_INVITATION_IN_DB.getMessage()));

        goalRepository.findById(goalInvitation.getGoal().getId()).orElseThrow(() ->
                new DataValidationException(NO_GOAL_IN_DB.getMessage()));

        goalInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(goalInvitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filters) {
        List<InvitationFilter> invitationFilters = List.of(
                new InviterIdFilter(),
                new InviterIdFilter(),
                new InvitedNamePatternFilter(),
                new InviterNamePatternFilter(),
                new RequestStatusFilter()
        );
        goalInvitationServiceValidator.validateForGetInvitations(filters);

        return invitationFilters.stream().
                filter(filter -> filter.isApplicable(filters)).
                flatMap(filter -> filter.apply(goalInvitationRepository.findAll().stream(), filters)).
                distinct().
                map(goalInvitationMapper::toDto).
                toList();
    }
}
