package school.faang.user_service.service.goal.invitation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.invitation.invitation_filter.InvitationFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalInvitationServiceImpl implements GoalInvitationService {

    private static final int MAX_ACTIVE_GOALS = 3;
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final List<InvitationFilter> filters;

    @Override
    @Transactional
    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {

        if (goalInvitationDto.getInvitedUserId().equals(goalInvitationDto.getInviterId())) {
            throw new IllegalArgumentException("Invited user and inviter user are the same");
        }

        GoalInvitation goalInvitation = goalInvitationMapper.fromGoalInvitationDto(goalInvitationDto);
        goalInvitation.setInviter(findUserById(goalInvitationDto.getInviterId()));
        goalInvitation.setInvited(findUserById(goalInvitationDto.getInvitedUserId()));
        goalInvitation.setGoal(findGoalById(goalInvitationDto.getGoalId()));

        goalInvitation = goalInvitationRepository.save(goalInvitation);
        log.info("Invitation created with id: {} ", goalInvitation.getId());
        return goalInvitationMapper.toGoalInvitationDto(goalInvitation);
    }

    @Override
    @Transactional
    public GoalInvitationDto acceptInvitation(long id) {
        GoalInvitation goalInvitation = findGoalInvitationById(id);
        Goal goal = findGoalById(goalInvitation.getGoal().getId());
        User invitedUser = goalInvitation.getInvited();

        if (checkIfGoalSuitableToAdd(invitedUser, goal)) {
            goal.getUsers().add(invitedUser);
            goalRepository.save(goal);
            goalInvitation.setStatus(RequestStatus.ACCEPTED);
            goalInvitation = goalInvitationRepository.save(goalInvitation);
            log.info("Invitation accepted: {} ", goalInvitation.getId());
        }

        return goalInvitationMapper.toGoalInvitationDto(goalInvitation);
    }

    @Override
    @Transactional
    public GoalInvitationDto rejectInvitation(long id) {
        GoalInvitation goalInvitation = findGoalInvitationById(id);
        goalInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitation = goalInvitationRepository.save(goalInvitation);
        log.info("Invitation rejected: {} ", goalInvitation.getId());

        return goalInvitationMapper.toGoalInvitationDto(goalInvitation);
    }

    @Override
    public List<GoalInvitationDto> getInvitations(InvitationFilterDto invitationFilterDto) {
        Stream<GoalInvitation> invitations = goalInvitationRepository.findAll().stream();
        return filters.stream()
                .filter(filter -> filter.isApplicable(invitationFilterDto))
                .reduce(invitations,
                        (invites, filter) -> filter.apply(invites, invitationFilterDto),
                        (inv1, inv2) -> inv1
                )
                .map(goalInvitationMapper::toGoalInvitationDto)
                .toList();
    }

    private boolean checkIfGoalSuitableToAdd(User user, Goal goal) {
        return !user.getGoals().contains(goal) && user.getGoals().size() < MAX_ACTIVE_GOALS;
    }

    private GoalInvitation findGoalInvitationById(long id) {
        return goalInvitationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("GoalInvitation not found with id " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found with id " + id));
    }

    private Goal findGoalById(Long id) {
        return goalRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Goal not found with id " + id));
    }
}
