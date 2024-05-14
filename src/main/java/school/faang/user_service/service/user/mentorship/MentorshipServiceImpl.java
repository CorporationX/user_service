package school.faang.user_service.service.user.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {

    private final GoalRepository goalRepository;

    @Override
    @Transactional
    public void deleteMentorFromMentee(User mentor) {
        List<User> mentees = mentor.getMentees();
        mentees.forEach(mentee -> {
            mentee.getMentors().remove(mentor);
            mentee.getSetGoals().stream()
                    .filter(goal -> goal.getMentor().equals(mentor))
                    .forEach(goal -> {
                        goal.setMentor(mentee);
                        goalRepository.save(goal);
                    });
        });
    }
}
