package school.faang.user_service.service.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    // Не знаю, что теперь делать с этим репо, т.к. в данном случае можно обойтись просто userRepo,
    // но по условию написано использовать именно mentorshipRepository
    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;

    public List<User> getMentees(long mentorId) {
        User user = validateUserId(mentorId);
        return user.getMentees();
    }

    private User validateUserId(long mentorId) {
        return userRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Invalid mentor id"));
    }
}

