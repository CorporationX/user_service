package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;

    public boolean deleteMentee(long menteeId, long mentorId) {
        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(()->new DataValidationException("Mentee was not found"));
        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(()->new DataValidationException("Mentor was not found"));

        List<User> mentees = mentor.getMentees();
        if (mentees.contains(mentee)) {
            mentees.remove(mentee);
            mentor.setMentees(mentees);
            return true;
        }
        return false;
    }

    public boolean deleteMentor(long menteeId, long mentorId) {
        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(()->new DataValidationException("Mentee was not found"));
        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(()->new DataValidationException("Mentor was not found"));

        List<User> mentors = mentee.getMentors();
        if (mentors.contains(mentor)) {
            mentors.remove(mentor);
            mentee.setMentors(mentors);
            return true;
        }
        return false;
    }
}
