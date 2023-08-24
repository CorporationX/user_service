package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.dto.redis.GoalSetEventDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.filter.goal.InvitationFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.publisher.GoalSetPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final UserRepository userRepository;
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<InvitationFilter> filters;
    private final GoalService goalService;
    private final GoalSetPublisher goalSetPublisher;

    @Transactional
    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto) {
        validateInvitation(invitationDto);
        GoalInvitation invitation = goalInvitationRepository.save(goalInvitationMapper.toEntity(invitationDto));
        return goalInvitationMapper.toDto(invitation);
    }

    @Transactional
    public void acceptGoalInvitation(long id) {
        GoalInvitation invitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new DataValidException("Goal Invitation not found. Id: " + id));
        validateAccept(invitation);

        User invited = invitation.getInvited();
        Goal goal = invitation.getGoal();

        goalService.addUser(goal, invited);
        invited.getGoals().add(goal);

        invitation.setInvited(invited);
        invitation.setStatus(RequestStatus.ACCEPTED);

        goalInvitationRepository.save(invitation);
        goalSetPublisher.publishMessage(new GoalSetEventDto(goal.getId(), invited.getId()));
    }

    @Transactional
    public void rejectGoalInvitation(long id) {
        GoalInvitation invitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new DataValidException("Goal Invitation not found. Id: " + id));
        checkGoalExists(invitation);
        invitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(invitation);
    }

    @Transactional(readOnly = true)
    private void validateAccept(GoalInvitation invitation) {
        long id = invitation.getId();
        if (!goalService.canAddGoalToUser(invitation.getInvited().getId())) {
            throw new DataValidException("Unable to accept Goal Invitation, invited has reached the limit. Id: " + id);
        }
        if (invitation.getInvited().getGoals().contains(invitation.getGoal())) {
            throw new DataValidException("Unable to accept Goal Invitation, invited already has goal. Id: " + id);
        }
        if (!goalService.existGoalById(invitation.getGoal().getId())) {
            throw new DataValidException("Unable to accept Goal Invitation, Goal not found. Id: " + id);
        }
    }

    @Transactional(readOnly = true)
    public List<GoalInvitationDto> getInvitations(GoalInvitationFilterDto filter) {
        Stream<GoalInvitation> goalInvitationStream = goalInvitationRepository.findAll().stream();

        for (InvitationFilter invitationFilter : filters) {
            if (invitationFilter.isApplicable(filter)) {
                goalInvitationStream = invitationFilter.apply(goalInvitationStream, filter);
            }
        }

        return goalInvitationMapper.toDtoList(goalInvitationStream.toList());
    }

    @Transactional(readOnly = true)
    private void checkGoalExists(GoalInvitation invitation) {
        if (!goalService.existGoalById(invitation.getGoal().getId())) {
            throw new DataValidException("Unable to decline Invitation, Goal not found. Id: " + invitation.getId());
        }
    }

    @Transactional(readOnly = true)
    private void validateInvitation(GoalInvitationDto invitation) {
        invitation.setId(null);
        if (!goalService.existGoalById(invitation.getGoalId())) {
            throw new DataValidException("Goal does not exist. Invitation Id: " + invitation.getId());
        }
        if (!userRepository.existsById(invitation.getInviterId())) {
            throw new DataValidException("Inviter does not exist, Id: " + invitation.getInviterId());
        }
        if (!userRepository.existsById(invitation.getInvitedUserId())) {
            throw new DataValidException("Invited does not exist, Id: " + invitation.getInvitedUserId());
        }
        if (invitation.getInviterId().equals(invitation.getInvitedUserId())) {
            throw new DataValidException("Inviter and invited are equal. Invitation Id: " + invitation.getId());
        }
    }
}
