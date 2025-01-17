package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;

    public void stopUserMentorship(Long userId) {
        Optional<User> userOptional = mentorshipRepository.findById(userId);

        if (userOptional.isPresent() && userOptional.get().getMentees() != null) {
            userOptional.get().getMentees().forEach(mentee ->
            {
                removeMentorFromMentees(mentee);
                removeMentorFromGoals(mentee, userId);
            });
        }
    }

    private void removeMentorFromMentees(User mentee) {
        mentee.setMentors(mentee.getMentors().stream()
                .filter(mentor -> !Objects.equals(mentor.getId(), mentee.getId()))
                .toList());
        userRepository.save(mentee);
    }

    private void removeMentorFromGoals(User mentee,Long userId) {
        mentee.setGoals(mentee.getGoals().stream()
                .filter(goal -> Objects.equals(goal.getMentor().getId(), userId))
                .peek(goal -> goal.setMentor(mentee))
                .toList());
        userRepository.save(mentee);
    }
}
