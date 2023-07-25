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

    public List<User> getMentees(long id) {
        User user = mentorshipRepository.findById(id)
                .orElseThrow(()->new DataValidationException("User was not found"));
        return user.getMentees();
    }

    public List<User> getMentors(long id) {
        User user = mentorshipRepository.findById(id)
                .orElseThrow(()->new DataValidationException("User was not found"));
        return user.getMentors();
    }
}
