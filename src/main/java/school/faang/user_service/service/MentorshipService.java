package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.UserValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    public void removeMenteeFromUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserValidationException("User not found"));
        if (user.getMentees().isEmpty()) return;
        user.getMentees().forEach(mentee -> {
            mentee.getMentors().remove(user);
        });
    }

    public void removeMenteeFromUserGoals(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserValidationException("User not found"));
        user.getMentees().forEach(mentee -> {
            List<Goal> menteeGoals = mentee.getSetGoals();
            if (!menteeGoals.isEmpty()) {
                menteeGoals.stream().filter(goal -> goal.getMentor().getId() == userId).forEach(goal -> {
                    goal.setMentor(user);
                    goalRepository.save(goal);
                });
            }
        });
    }
}
