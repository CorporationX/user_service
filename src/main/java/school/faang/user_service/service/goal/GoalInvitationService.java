package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;

    public void deleteGoalInvitations(List<GoalInvitation> goalInvitations) {
        goalInvitations.forEach(goalInvitation -> goalInvitationRepository.deleteById(goalInvitation.getId()));
    }

    public void deleteGoalInvitationForUser(List<GoalInvitation> goalInvitations, User user) {
        goalInvitations.stream()
                .filter(invitation -> (Objects.equals(invitation.getInvited().getId(), user.getId()) ||
                        Objects.equals(invitation.getInviter().getId(), user.getId())))
                .forEach(invitation -> goalInvitationRepository.deleteById(invitation.getId()));
    }

}
