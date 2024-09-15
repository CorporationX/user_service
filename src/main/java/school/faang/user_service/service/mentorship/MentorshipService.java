package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;

    @Transactional
    public void deleteMentorFromMentees(Long mentorId, List<User> mentees) {
        mentees.forEach(mentee -> {
            mentee.getMentors().removeIf(mentor -> mentor.getId().equals(mentorId));
            mentee.getGoals()
                    .stream()
                    .filter(goal -> goal.getMentor().getId().equals(mentorId))
                    .forEach(goal -> goal.setMentor(mentee));
        });

        mentorshipRepository.saveAll(mentees);
    }
}
