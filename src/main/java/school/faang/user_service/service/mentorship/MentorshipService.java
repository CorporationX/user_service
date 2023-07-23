package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipValidator mentorshipValidator;

    public boolean deleteMentee(long menteeId, long mentorId) {
        Optional<User> menteeOptional = mentorshipRepository.findById(menteeId);
        Optional<User> mentorOptional = mentorshipRepository.findById(mentorId);

        User mentee = mentorshipValidator.findUserByIdValidate(menteeOptional);
        User mentor = mentorshipValidator.findUserByIdValidate(mentorOptional);

        List<User> mentees = mentor.getMentees();
        //надо ли тут обновлять данные у ученика и удалять ли из MentorshipRepository связь?
        if (mentees.contains(mentee)) {
            mentees.remove(mentee);
            mentor.setMentees(mentees);
            return true;
        }
        return false;
    }

    public boolean deleteMentor(long menteeId, long mentorId) {
        Optional<User> menteeOptional = mentorshipRepository.findById(menteeId);
        Optional<User> mentorOptional = mentorshipRepository.findById(mentorId);

        User mentee = mentorshipValidator.findUserByIdValidate(menteeOptional);
        User mentor = mentorshipValidator.findUserByIdValidate(mentorOptional);

        List<User> mentors = mentee.getMentors();
        if (mentors.contains(mentor)) {
            mentors.remove(mentor);
            mentee.setMentors(mentors);
            return true;
        }
        return false;
    }
}
