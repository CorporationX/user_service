package school.faang.user_service.service.impl.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.dto.goal.GoalInvitationDto;
import school.faang.user_service.model.dto.goal.InvitationFilterDto;
import school.faang.user_service.model.entity.RequestStatus;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.goal.Goal;
import school.faang.user_service.model.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.GoalInvitationValidationException;
import school.faang.user_service.filter.goal.GoalInvitationFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalInvitationService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalInvitationServiceImpl implements GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationValidator goalInvitationValidator;
    private final List<GoalInvitationFilter> goalInvitationFilters;

    @Override
    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        Goal goal = goalRepository.findById(goalInvitationDto.goalId()).orElseThrow(
                () -> new GoalInvitationValidationException("Goal not found"));
        User inviter = userRepository.findById(goalInvitationDto.inviterId()).orElseThrow(
                () -> new GoalInvitationValidationException("Inviter not found"));
        User invited = userRepository.findById(goalInvitationDto.invitedUserId()).orElseThrow(
                () -> new GoalInvitationValidationException("Invited user not found"));
        goalInvitationValidator.validateInvitationToCreate(inviter, invited);
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(goalInvitationDto);
        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        return goalInvitationMapper.toDto(goalInvitationRepository.save(goalInvitation));
    }

    @Override
    public GoalInvitationDto acceptInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).orElseThrow(
                () -> new GoalInvitationValidationException("Goal invitation not found"));
        User invited = goalInvitation.getInvited();
        Goal goal = goalInvitation.getGoal();
        goalInvitationValidator.validateInvitationToAccept(goalInvitation, goal, invited);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        invited.getGoals().add(goal);
        goalInvitation.getGoal().getUsers().add(invited);
        return goalInvitationMapper.toDto(goalInvitationRepository.save(goalInvitation));
    }

    @Override
    public GoalInvitationDto rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).orElseThrow(
                () -> new GoalInvitationValidationException("Goal invitation not found"));
        User invited = goalInvitation.getInvited();
        Goal goal = goalInvitation.getGoal();
        goalInvitationValidator.validateInvitationToReject(goalInvitation, goal);
        goalInvitation.setStatus(RequestStatus.REJECTED);
        invited.getGoals().remove(goal);
        goalInvitation.getGoal().getUsers().remove(invited);
        return goalInvitationMapper.toDto(goalInvitationRepository.save(goalInvitation));
    }

    @Override
    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        Stream<GoalInvitation> goalInvitations = goalInvitationRepository.findAll().stream();
        goalInvitationFilters.stream()
                .filter(currentFilter -> currentFilter.isApplicable(filter))
                .forEach(currentFilter -> currentFilter.apply(goalInvitations, filter));
        return goalInvitationMapper.toDto(goalInvitations.toList());
    }
}
