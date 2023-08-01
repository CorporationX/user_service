package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.goalinvitation.GoalInvitationFilter;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.util.validator.GoalInvitationServiceValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationServiceValidator validator;
    private final List<GoalInvitationFilter> goalInvitationFilters;

    @Transactional
    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        Optional<Goal> goal = goalRepository.findById(goalInvitationDto.goalId());
        Optional<User> inviter = userRepository.findById(goalInvitationDto.inviterId());
        Optional<User> invited = userRepository.findById(goalInvitationDto.invitedUserId());

        validator.validateToCreate(goal, inviter, invited);

        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(goalInvitationDto);

        goalInvitation = goalInvitationRepository.save(goalInvitation);

        return goalInvitationMapper.toDto(goalInvitation);
    }

    @Transactional
    public GoalInvitationDto acceptInvitation(Long id) {
        GoalInvitation goalInvitation = validator
                .validateToAccept(goalInvitationRepository.findById(id));
        User invited = goalInvitation.getInvited();

        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        invited.getGoals().add(goalInvitation.getGoal());
        goalInvitation.getGoal().getUsers().add(invited);

        goalInvitationRepository.save(goalInvitation);

        return goalInvitationMapper.toDto(goalInvitation);
    }

    @Transactional
    public GoalInvitationDto rejectInvitation(Long id) {
        GoalInvitation goalInvitation = validator
                .validateToReject(goalInvitationRepository.findById(id));
        User invited = goalInvitation.getInvited();

        goalInvitation.setStatus(RequestStatus.REJECTED);
        invited.getGoals().remove(goalInvitation.getGoal());
        goalInvitation.getGoal().getUsers().remove(invited);
        goalInvitationRepository.save(goalInvitation);

        return goalInvitationMapper.toDto(goalInvitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto invitationFilterDto) {
        Stream<GoalInvitation> goalInvitations = goalInvitationRepository.findAll().stream();

        for (GoalInvitationFilter goalFilter : goalInvitationFilters) {
            if (goalFilter.isApplicable(invitationFilterDto)) {
                goalInvitations = goalFilter.apply(goalInvitations, invitationFilterDto);
            }
        }

        return goalInvitations.map(goalInvitationMapper::toDto).toList();
    }
}
