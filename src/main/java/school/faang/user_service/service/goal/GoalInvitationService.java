package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.filter.goal.GoalInvitationFilter;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.GoalInvitationValidator;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    @Autowired
    private final GoalInvitationRepository goalInvitationRepository;

    @Autowired
    private final GoalInvitationMapper goalInvitationMapper;

    @Autowired
    private final GoalInvitationValidator goalInvitationValidator;

    @Autowired
    private final UserService userService;

    @Autowired
    private final List<GoalInvitationFilter> filters;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        goalInvitationValidator.validateNewGoalInvitation(invitation);

        User inviter = userService.findUserById(invitation.getInviterId());
        User invitedUser = userService.findUserById(invitation.getInvitedUserId());

        GoalInvitation goalInvitationEntity = goalInvitationMapper.toEntity(invitation);
        goalInvitationEntity.setInviter(inviter);
        goalInvitationEntity.setInvited(invitedUser);
        goalInvitationRepository.save(goalInvitationEntity);
        return invitation;
    }

    public GoalInvitationDto acceptGoalInvitation(long invitationId) {
        GoalInvitation goalInvitation = getGoalInvitationById(invitationId);
        goalInvitationValidator.validateAcceptedGoalInvitation(goalInvitation);

        Goal goal = goalInvitation.getGoal();
        User invitedUser = goalInvitation.getInvited();

        List<Goal> currentUserGoals = invitedUser.getGoals();

        currentUserGoals.add(goal);
        invitedUser.setGoals(currentUserGoals);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitation.setInvited(invitedUser);

        userService.saveUser(invitedUser);
        goalInvitationRepository.save(goalInvitation);

        return goalInvitationMapper.toDto(goalInvitation);
    }

    public GoalInvitationDto rejectGoalInvitation(long invitationId) {
        GoalInvitation goalInvitation = getGoalInvitationById(invitationId);
        goalInvitationValidator.validateRejectedGoalInvitation(goalInvitation);

        goalInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }

    public List<GoalInvitationDto> getInvitations(GoalInvitationFilterDto filter) {
        Stream<GoalInvitation> invitationStream = StreamSupport.stream(goalInvitationRepository
                .findAll().spliterator(), false);

        List<GoalInvitationFilter> applicableFilters = filters.stream().filter(fl -> fl.isApplicable(filter)).toList();

        for (GoalInvitationFilter fil : applicableFilters) {
            invitationStream = fil.apply(invitationStream, filter);
        };

        return invitationStream.map(goalInvitationMapper::toDto).toList();
    }

    private GoalInvitation getGoalInvitationById(long invitationId) {
        return goalInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("Goal invitation not found"));
    }
}
