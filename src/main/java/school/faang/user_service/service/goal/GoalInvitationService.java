package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalInvitationValidation;
import school.faang.user_service.validator.GoalInvitationValidationMaxActiveGoal;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationValidation goalInvitationValidation;
    private final GoalInvitationValidationMaxActiveGoal goalInvitationValidationMaxActiveGoal;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationMapper mapper;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {

        goalInvitationValidation.invitationValidationUser(invitation);

        GoalInvitation goalInvitation = mapper.toEntity(invitation);

        return mapper.toDto(goalInvitationRepository.save(goalInvitation));
    }

    public GoalInvitationDto acceptGoalInvitation(long idGoalInvitation) { //invitedUser

       GoalInvitation invitation = goalInvitationRepository.findById(idGoalInvitation).orElseThrow();

        goalInvitationValidationMaxActiveGoal.isCheckActiveTargetUser(invitation);

        return null;
    }
}
