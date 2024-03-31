package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.filter.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.goal.filter_goalinvitation.GoalInvitationFilter;
import school.faang.user_service.validation.goal.GoalInvitationValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<GoalInvitationFilter> goalInvitationFilters;
    private final GoalInvitationValidator goalInvitationValidator;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto) {
        goalInvitationValidator.validateCreateInvitation(invitationDto);
        GoalInvitation invitation = goalInvitationRepository.save(goalInvitationMapper
                .toEntity(invitationDto));
        return goalInvitationMapper.toDto(invitation);
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        GoalInvitation invitation = goalInvitationValidator.findInvitation(id);
        goalInvitationValidator.validateGoalExists(invitation);
        goalInvitationValidator.validateAcceptGoalInvitation(invitation);
        goalInvitationValidator.checkingStatusIsPending(invitation);

        User invited = invitation.getInvited();
        Goal goal = invitation.getGoal();
        invited.getGoals().add(goal);

        invitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitationRepository.save(invitation);
        return goalInvitationMapper.toDto(invitation);
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        GoalInvitation invitation = goalInvitationValidator.findInvitation(id);
        goalInvitationValidator.validateGoalExists(invitation);
        goalInvitationValidator.checkingStatusIsPending(invitation);
        invitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(invitation);
        return goalInvitationMapper.toDto(invitation);
    }

    public List<GoalInvitationDto> getFilteredInvitations(GoalInvitationFilterDto filters) {
        List<GoalInvitation> goalInvitations = goalInvitationRepository.findAll();

        List<GoalInvitationFilter> applicableFilters = goalInvitationFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();

        List<GoalInvitationDto> filteredInvitations = goalInvitations.stream()
                .filter(invitation -> applicableFilters.stream()
                        .allMatch(invitationFilter -> invitationFilter.apply(invitation, filters)))
                .map(goalInvitationMapper::toDto)
                .toList();
        return filteredInvitations;
    }
}