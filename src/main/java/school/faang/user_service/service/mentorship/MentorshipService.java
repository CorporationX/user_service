package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipValidator mentorshipValidator;

    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipValidator.equalsIdValidator(menteeId, mentorId);

        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new DataValidationException("Mentee was not found"));
        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new DataValidationException("Mentor was not found"));

        mentor.getMentees().remove(mentee);
        mentorshipRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipValidator.equalsIdValidator(menteeId, mentorId);

        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new DataValidationException("Mentee was not found"));
        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new DataValidationException("Mentor was not found"));

        mentee.getMentors().remove(mentor);
        mentorshipRepository.save(mentee);
    }
}
