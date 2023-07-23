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

    public List<User> getMentees(long id) {
        Optional<User> userOptional = mentorshipRepository.findById(id);
        User user = mentorshipValidator.findUserByIdValidate(userOptional);
        return user.getMentees();
    }

    public List<User> getMentors(long id) {
        Optional<User> userOptional = mentorshipRepository.findById(id);
        User user = mentorshipValidator.findUserByIdValidate(userOptional);
        return user.getMentors();
    }
}
