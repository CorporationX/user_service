package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.util.goal.exception.GoalNotFoundException;
import school.faang.user_service.util.goal.exception.UserNotFoundException;
import school.faang.user_service.util.goal.validator.GoalInvitationAcceptValidator;
import school.faang.user_service.util.goal.validator.GoalInvitationEntityValidator;
import school.faang.user_service.util.goal.validator.GoalInvitationRejectValidator;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationEntityValidator goalInvitationDtoValidator;
    private final GoalInvitationAcceptValidator goalInvitationAcceptValidator;
    private final GoalInvitationRejectValidator goalInvitationRejectValidator;

    @Transactional
    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        GoalInvitation goalInvitation = goalInvitationMapper.toEntityForCreatingInvitation(goalInvitationDto, this);
        goalInvitationDtoValidator.validate(goalInvitation);

        goalInvitation.getInviter().getSentGoalInvitations().add(goalInvitation);
        goalInvitation.getInvited().getReceivedGoalInvitations().add(goalInvitation);
        goalInvitation = goalInvitationRepository.save(goalInvitation);

        return goalInvitationMapper.toDto(goalInvitation);
    }

    @Transactional
    public GoalInvitationDto acceptGoalInvitation(Long id) {
        GoalInvitation goalInvitation = goalInvitationAcceptValidator
                .validateRequest(goalInvitationRepository.findById(id));
        User invited = goalInvitation.getInvited();
        User inviter = goalInvitation.getInviter();

        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        invited.getReceivedGoalInvitations().remove(goalInvitation);
        inviter.getSentGoalInvitations().remove(goalInvitation);
        invited.getGoals().add(goalInvitation.getGoal());
        goalInvitation.getGoal().getUsers().add(invited);
        goalInvitationRepository.save(goalInvitation);

        return goalInvitationMapper.toDto(goalInvitation);
    }

    @Transactional
    public GoalInvitationDto rejectGoalInvitation(Long id) {
        GoalInvitation goalInvitation = goalInvitationRejectValidator
                .validateRequest(goalInvitationRepository.findById(id));
        User invited = goalInvitation.getInvited();
        User inviter = goalInvitation.getInviter();

        goalInvitation.setStatus(RequestStatus.REJECTED);
        invited.getReceivedGoalInvitations().remove(goalInvitation);
        inviter.getSentGoalInvitations().remove(goalInvitation);
        invited.getGoals().remove(goalInvitation.getGoal());
        goalInvitation.getGoal().getUsers().remove(invited);
        goalInvitationRepository.save(goalInvitation);

        return goalInvitationMapper.toDto(goalInvitation);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User with id = " + id + " not found")
        );
    }

    public Goal findGoalById(Long id) {
        return goalRepository.findById(id).orElseThrow(
                () -> new GoalNotFoundException("Goal with id = " + id + " not found")
        );
    }
}
