package school.faang.user_service.service.goal.invitation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationCreateDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.goal.invitation.filter.GoalInvitationFilterService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalInvitationServiceImpl implements GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationValidator goalInvitationValidator;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationFilterService goalInvitationFilterService;
    private final GoalService goalService;
    private final UserService userService;

    @Override
    @Transactional
    public GoalInvitation findById(long id) {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("GoalInvitation with id %s not found", id)));
    }

    @Override
    @Transactional
    public void createInvitation(GoalInvitationCreateDto invitation) {
        goalInvitationValidator.validateUserIds(invitation);

        User inviter = userService.findUserById(invitation.getInviterId());
        User invitedUser = userService.findUserById(invitation.getInvitedUserId());
        Goal goal = goalService.findGoalById(invitation.getGoalId());
        GoalInvitation goalInvitationEntity = goalInvitationMapper.toEntity(inviter, invitedUser, goal, RequestStatus.PENDING);

        goalInvitationRepository.save(goalInvitationEntity);
    }

    @Override
    @Transactional
    public void acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = findById(id);
        User invited = goalInvitation.getInvited();

        goalInvitationValidator.validateGoalAlreadyExistence(goalInvitation.getGoal(), invited);
        goalInvitationValidator.validateMaxGoals(goalService.findActiveGoalsByUserId(invited.getId()));

        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitationRepository.save(goalInvitation);
    }

    @Override
    public void rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = findById(id);

        goalInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(goalInvitation);
    }

    @Override
    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filterDto) {
        Stream<GoalInvitation> invitationStream = goalInvitationRepository.findAll().stream();
        return goalInvitationFilterService.applyFilters(invitationStream, filterDto)
                .map(goalInvitationMapper::toDto)
                .toList();
    }
}
