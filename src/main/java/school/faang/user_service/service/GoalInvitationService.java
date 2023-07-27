package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private UserRepository userRepository;

    public void acceptGoalInvitation(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found. Id: " + id));
        int goals = user.getGoals().size();
    }
}
