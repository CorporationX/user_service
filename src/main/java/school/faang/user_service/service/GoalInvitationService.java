package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.InvalidInvitationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    @Value("${goal.invitation.max-active-goals}")
    private int maxActiveGoals;

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalService goalService;
    private final UserService userService;
    private final GoalInvitationMapper goalInvitationMapper;


    @Transactional
    public void createInvitation(GoalInvitationDto invitationDto) {
        log.info("Creating invitation: {}", invitationDto);

        GoalInvitation invitation = goalInvitationMapper.toEntity(invitationDto);
        validateInvitation(invitation);

        invitation.setStatus(RequestStatus.PENDING);
        goalInvitationRepository.save(invitation);

        log.info("Invitation created successfully: {}", invitation);
    }

    @Transactional
    public void acceptGoalInvitation(Long id) {
        log.info("Accepting invitation with ID: {}", id);

        GoalInvitation invitation = getInvitationById(id);
        validateAcceptance(invitation);

        invitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitationRepository.save(invitation);

        log.info("Invitation accepted successfully: {}", invitation);
    }

    @Transactional
    public void rejectGoalInvitation(Long id) {
        log.info("Rejecting invitation with ID: {}", id);

        GoalInvitation invitation = getInvitationById(id);
        if (invitation.getStatus() != RequestStatus.PENDING) {
            throw new InvalidInvitationException("Invitation is already processed.");
        }

        invitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(invitation);

        log.info("Invitation rejected successfully: {}", invitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        log.info("Fetching invitations with filter: {}", filter);

        List<GoalInvitation> invitations = goalInvitationRepository.findByInviterIdAndInvitedIdAndStatus(
                filter.inviterId(),
                filter.invitedId(),
                filter.status()
        );

        log.info("Found {} invitations", invitations.size());
        return invitations.stream()
                .map(goalInvitationMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<GoalInvitationDto> getInvitationsByInvitedUserId(Long invitedUserId) {
        log.info("Fetching invitations for invited user ID: {}", invitedUserId);

        List<GoalInvitation> invitations = goalInvitationRepository.findByInvitedId(invitedUserId);

        log.info("Found {} invitations for user ID: {}", invitations.size(), invitedUserId);
        return invitations.stream()
                .map(goalInvitationMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateInvitation(GoalInvitation invitation) {
        if (invitation.getInviter() == null || invitation.getInvited() == null) {
            throw new InvalidInvitationException("Inviter and invited user must be specified.");
        }

        if (invitation.getInviter().getId().equals(invitation.getInvited().getId())) {
            throw new InvalidInvitationException("Inviter and invited user cannot be the same.");
        }

        userService.validateUserExists(invitation.getInviter().getId());
        userService.validateUserExists(invitation.getInvited().getId());

        if (!goalService.existsById(invitation.getGoal().getId())) {
            throw new InvalidInvitationException("Goal does not exist.");
        }
    }

    private void validateAcceptance(GoalInvitation invitation) {
        if (invitation.getStatus() != RequestStatus.PENDING) {
            throw new InvalidInvitationException("Invitation is already processed.");
        }

        if (goalService.countActiveGoalsPerUser(invitation.getInvited().getId()) >= maxActiveGoals) {
            throw new InvalidInvitationException("User has reached the maximum number of active goals.");
        }

        if (goalInvitationRepository.existsByInvitedAndGoal(invitation.getInvited(), invitation.getGoal())) {
            throw new InvalidInvitationException("User is already working on this goal.");
        }
    }

    private GoalInvitation getInvitationById(Long id) {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() -> new InvalidInvitationException("Invitation not found."));
    }
}