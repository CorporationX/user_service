package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;

    public void deleteMentorForAllHisMentees(long mentorId, List<Long> menteesIds) {
        List<User> mentees = menteesIds.stream()
                .map(id -> userRepository.findById(id).orElseThrow(() ->
                        new EntityNotFoundException("User doesn't exist by id: " + id)))
                .toList();
        mentees.forEach(mentee -> {
            mentee.getMentors().removeIf(mentor -> mentor.getId() == mentorId);
            mentee.getGoals().stream()
                    .filter(goal -> goal.getMentor().getId() == mentorId)
                    .forEach(goal -> goal.setMentor(mentee));
        });
        mentorshipRepository.saveAll(mentees);
    }
}
