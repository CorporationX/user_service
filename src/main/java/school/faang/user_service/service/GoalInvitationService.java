package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.GoalInvitationValidationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    private final GoalInvitationMapper mapper;

    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {

        Long inviterId = goalInvitationDto.getInviterId();
        Long invitedUserId = goalInvitationDto.getInvitedUserId();

        if (inviterId.equals(invitedUserId)) {
            throw new GoalInvitationValidationException("User invited and user inviter cannot be equals, id " + invitedUserId);
        }

        Optional<User> optionalUserInviter = userRepository.findById(inviterId);
        if (optionalUserInviter.isEmpty()) {
            throw new GoalInvitationValidationException("User inviter with id " + inviterId + " user does not exist");
        }

        Optional<User> optionalUserInvited = userRepository.findById(invitedUserId);
        if (optionalUserInvited.isEmpty()) {
            throw new GoalInvitationValidationException("User invited with id " + invitedUserId + " user does not exist");
        }

        Long goalId = goalInvitationDto.getGoalId();
        Optional<Goal> optionalGoal = goalRepository.findById(goalId);
        if (optionalGoal.isEmpty()) {
            throw new GoalInvitationValidationException("Goal with id " + goalId + " does not exist");
        }

        // TODO: ??? в маппере нет смысла ? пусть будет на будущее?
        //       GoalInvitationDto.id - при создании передаём null ?
        GoalInvitation entity = mapper.toEntity(goalInvitationDto);

        entity.setInviter(optionalUserInviter.get());
        entity.setInvited(optionalUserInvited.get());
        entity.setGoal(optionalGoal.get());
        entity.setStatus(RequestStatus.PENDING);

        GoalInvitation saved = goalInvitationRepository.save(entity);

        return mapper.toDto(saved);
    }
}
