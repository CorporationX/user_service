package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {
    private final UserRepository userRepository;

    public void stopMentorship(Long mentorId, Long menteeId) {
        User mentee = userRepository.findById(menteeId).orElseThrow();
        User mentor = userRepository.findById(mentorId).orElseThrow();
        List<User> mentorsMentees = mentor.getMentees();
        List<User> menteesMentors = mentee.getMentors();
        List<Goal> changedMentorsGoals = mentee.getGoals().stream()
                .peek(goal -> {
                    if (goal.getMentor().equals(mentor)) {
                        goal.setMentor(mentee);
                    }
                })
                .toList();

        mentee.setGoals(changedMentorsGoals);

        menteesMentors.remove(mentor);
        mentorsMentees.remove(mentee);

        mentee.setMentors(menteesMentors);
        mentor.setMentors(mentorsMentees);

        userRepository.save(mentee);
        userRepository.save(mentor);
    }
}
