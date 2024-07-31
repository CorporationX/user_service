package school.faang.user_service.service.mentorshipService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {

    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;
    private final GoalRepository goalRepository;

    @Override
    public void deactivateMentorship(long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<User> mentees = user.getMentees();
        mentees.forEach(mentee -> {
//            Set user goal mentor to itself
//            could be brought to goal service as a function for consistency and from user as well
            List<Goal> userGoals = mentee.getGoals();
            userGoals.forEach(goal -> {
                goal.setMentor(mentee);
            });

//            remove mentor from every mentee mentor list
            List<User> userMentors = mentee.getMentors();
            userMentors.removeIf(mentor -> mentor.getId() == userId);
            mentee.setMentors(userMentors);
        });

    }
}
