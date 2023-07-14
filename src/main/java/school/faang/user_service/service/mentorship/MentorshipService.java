package school.faang.user_service.service.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;

    public List<User> getMentees(long mentorId) {
        User user = validateUserId(mentorId);
        return user.getMentees();
    }

    private User validateUserId(long mentorId) {
        return mentorshipRepository.findUserById(mentorId)
                .orElseThrow(() -> new UserNotFoundException("Invalid mentor id"));
    }
}

