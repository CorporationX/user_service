package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;

    public void stopMentorship(User mentor) {
        List<User> mentees = mentor.getMentees();
        for (User mentee : mentees) {
            mentee.getMentors().remove(mentor);
            transferGoalsToMentee(mentee);
            mentorshipRepository.save(mentee);
        }
    }

    private void transferGoalsToMentee(User mentee) {
        if (mentee.getSetGoals() != null) {
            mentee.getSetGoals().forEach(goal -> mentee.getGoals().add(goal));
        }
        mentee.setSetGoals(new ArrayList<>());
    }
}