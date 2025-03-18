package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.goal.GoalInvitationFilter;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.utils.validationUtils.GoalInvitationValidation;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
public class GoalInvitationService {
    private static final int MAX_ACTIVE_GOALS = 3;
    private static final String INVITATION_FILTER_DTO_CANNOT_BE_NULL = "InvitationFilterDto can't be null";

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<GoalInvitationFilter> filters;

    public GoalInvitation createInvitation(GoalInvitationDto invitationDto) {
        GoalInvitationValidation.validateGoalInvitationDto(invitationDto);
        GoalInvitation createdInvitation = goalInvitationRepository.save(
                goalInvitationMapper.toEntity(invitationDto));
        log.info("Goal invitation created successfully for: {}", invitationDto);
        return createdInvitation;
    }

    public void acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.getReferenceById(id);
        User invitedUser = goalInvitation.getInvited();
        if (invitedUser.getReceivedGoalInvitations().size() >= MAX_ACTIVE_GOALS) {
            log.warn("User with ID {} has reached the active target limit", goalInvitation.getId());
            goalInvitation.setStatus(RequestStatus.REJECTED);
            return;
        }
        if (invitedUser.getReceivedGoalInvitations().contains(goalInvitation)) {
            log.warn("User with ID {} is already working on this goal", goalInvitation.getId());
            return;
        }
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        log.info("Goal invitation with ID: {} accepted successfully", id);
    }

    public void rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.getReferenceById(id);
        goalInvitation.getInvited().getReceivedGoalInvitations().remove(goalInvitation);
        goalInvitation.setStatus(RequestStatus.REJECTED);
        log.info("Goal invitation with ID: {} rejected successfully", id);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filterDto) {
        if (filterDto == null) {
            log.error(INVITATION_FILTER_DTO_CANNOT_BE_NULL);
            throw new DataValidationException(INVITATION_FILTER_DTO_CANNOT_BE_NULL);
        }
        Stream<GoalInvitation> goalInvitations = goalInvitationRepository.findAll().stream();
        for (GoalInvitationFilter filter : filters) {
            if (filter.isApplicable(filterDto)) {
                goalInvitations = filter.apply(goalInvitations, filterDto);
            }
        }
        return goalInvitationMapper.toDtoList(goalInvitations.toList());
    }
}