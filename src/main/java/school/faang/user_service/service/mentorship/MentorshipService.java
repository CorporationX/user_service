package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;

    public void deleteMentee(long menteeId, long mentorId) {
        User mentee = findUserById(menteeId);
        User mentor = findUserById(mentorId);

        mentor.getMentees().remove(mentee);
        mentorshipRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = findUserById(menteeId);
        User mentor = findUserById(mentorId);

        mentee.getMentors().remove(mentor);
        mentorshipRepository.save(mentee);
    }

    private User findUserById(long id) {
        return mentorshipRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("User was not found"));
    }
}
