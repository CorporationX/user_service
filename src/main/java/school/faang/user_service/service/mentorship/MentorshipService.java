package school.faang.user_service.service.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;

    @Transactional(readOnly = true)
    public List<User> getMentees(long mentorId) {
        User user = validateUserId(mentorId);
        return user.getMentees();
    }

    @Transactional(readOnly = true)
    public List<User> getMentors(long userId) {
        User user = validateUserId(userId);
        return user.getMentors();
    }

    private User validateUserId(long mentorId) {
        return mentorshipRepository.findUserById(mentorId)
                .orElseThrow(() -> new UserNotFoundException("Invalid user id"));
    }
}

