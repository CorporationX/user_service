package school.faang.user_service.service.goal;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.goal.InvitationFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validator.InvitationValidator;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final InvitationValidator invitationValidator;
    private final List<InvitationFilter> invitationFilters;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto) {
        Long inviterId = invitationDto.getInviterId();
        Long invitedId = invitationDto.getInvitedUserId();
        Long goalId = invitationDto.getGoalId();

        invitationValidator.validateIdEquality(inviterId, invitedId);
        invitationValidator.validateUsersExistence(inviterId, invitedId);

        GoalInvitation invitation = goalInvitationMapper.toEntity(invitationDto);
        log.info("Goal invitation was created. Goal ID - {}, Inviter ID - {}, Invited ID - {}",
                  goalId, inviterId, invitedId);
        invitation.setStatus(RequestStatus.PENDING);
        invitation = goalInvitationRepository.save(invitation);
        log.info("Goal invitation has been saved in the db.");

        return goalInvitationMapper.toDto(invitation);
    }

    public GoalInvitationDto acceptGoalInvitation(Long id) {
        GoalInvitation invitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Invitation to join the goal with id: " + id + " not found in DB"));
        User invitedUser = invitation.getInvited();

        invitationValidator.validateUserGoalsAmount(invitedUser.getId());
        invitationValidator.validateGoalExistence(invitation.getId());
        invitationValidator.validateGoalAlreadyPicked(invitedUser.getId());

        invitedUser.setGoals(List.of(invitation.getGoal()));
        invitation.setStatus(RequestStatus.ACCEPTED);
        invitation = goalInvitationRepository.save(invitation);
        log.info("Goal invitation was accepted. Invitation ID - {}", invitation.getId());

        return goalInvitationMapper.toDto(invitation);
    }

    public GoalInvitationDto rejectGoalInvitation(Long id) {
        GoalInvitation invitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Invitation to join the goal with id: " + id + " not found in DB"));

        invitationValidator.validateGoalExistence(invitation.getId());
        invitation.setStatus(RequestStatus.REJECTED);
        invitation = goalInvitationRepository.save(invitation);
        log.info("Goal invitation was rejected. Invitation ID - {}", invitation.getId());

        return goalInvitationMapper.toDto(invitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filters) {
        log.info("Search for invitations matching the provided filter...");
        Stream<GoalInvitation> invitations = goalInvitationRepository.findAll().stream();

        List<GoalInvitation> filteredInvitations = invitationFilters.stream()
                .filter(invitation -> invitation.isApplicable(filters))
                .reduce(invitations,
                       (currentStream, invitationFilter) -> invitationFilter.apply(currentStream, filters),
                       (stream1, stream2) -> stream2
                ).toList();
        log.info("Found {} invitations matching the provided filter", filteredInvitations.size());
        return goalInvitationMapper.toDto(filteredInvitations);
    }
}
