package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationValidator goalInvitationValidator;
    private final UserService userService;
    private final GoalService goalService;
    private final List<Filter<GoalInvitation, GoalInvitationFilterDto>> filters;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        goalInvitationValidator.validateDto(invitation);
        GoalInvitation goalInvitation = initializeGoalInvitation(invitation);
        goalInvitation = goalInvitationRepository.save(goalInvitation);
        log.info("Created invitation with id: {}", goalInvitation.getId());
        return goalInvitationMapper.toDto(goalInvitation);
    }

    @Transactional
    public GoalInvitationDto acceptGoalInvitation(long id) {
        goalInvitationValidator.validateId(id);
        return goalInvitationRepository.getById(id)
                .map(invitation -> {
                    goalInvitationValidator.validateGoalInvitationAcceptance(invitation);

                    invitation.setStatus(RequestStatus.ACCEPTED);
                    invitation.getInvited()
                            .getGoals()
                            .add(invitation.getGoal());
                    GoalInvitationDto dto = goalInvitationMapper.toDto(goalInvitationRepository.save(invitation));
                    log.info("User {} accepted invitation with id: {}", invitation.getInvited().getUsername(), id);
                    return dto;
                }).orElseThrow(() -> new EntityNotFoundException("Invitation not found for id: " + id));
    }

    @Transactional
    public GoalInvitationDto rejectGoalInvitation(long id) {
        goalInvitationValidator.validateId(id);
        return goalInvitationRepository.getById(id)
                .map(invitation -> {
                    goalInvitationValidator.validateGoalInvitationRejection(invitation);

                    invitation.setStatus(RequestStatus.REJECTED);

                    GoalInvitationDto dto = goalInvitationMapper.toDto(goalInvitationRepository.save(invitation));
                    log.info("Rejected invitation with id: {}", id);
                    return dto;
                }).orElseThrow(() -> new EntityNotFoundException("Invitation not found for id: " + id));
    }

    public List<GoalInvitationDto> getInvitations(GoalInvitationFilterDto filterDto) {
        Stream<GoalInvitation> invitations = goalInvitationRepository.findAll().stream();

        List<GoalInvitationDto> invitationDtoList = filters.stream().filter(filter -> filter.isApplicable(filterDto))
                .reduce(invitations, (streamGoalInvitation, filter) ->
                        filter.apply(streamGoalInvitation, filterDto), (s1, s2) -> s1)
                .map(goalInvitationMapper::toDto)
                .toList();
        log.info("Found {} invitations", invitationDtoList.size());
        return invitationDtoList;
    }

    private GoalInvitation initializeGoalInvitation(GoalInvitationDto invitation) {
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitation);
        goalInvitation.setInviter(userService.findUserById(invitation.getInviterId()));
        goalInvitation.setInvited(userService.findUserById(invitation.getInvitedUserId()));
        goalInvitation.setGoal(goalService.findGoalById(invitation.getGoalId()));
        goalInvitation.setStatus(RequestStatus.PENDING);
        return goalInvitation;
    }
}
