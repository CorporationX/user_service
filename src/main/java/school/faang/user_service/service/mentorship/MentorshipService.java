package school.faang.user_service.service.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.exception.mentorship.MenteeDoesNotExist;
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

    @Transactional
    public void deleteMentee(long mentorId, long menteeId) {
        validateToDeleteMentee(mentorId, menteeId);
        mentorshipRepository.deleteMentee(mentorId, menteeId);
    }

    private User validateUserId(long userId) {
        return mentorshipRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("Invalid user id: " + userId));
    }

    private void validateToDeleteMentee(long mentorId, long menteeId) {
        User mentor = validateUserId(mentorId);

        mentor.getMentees().stream()
                .filter(mentee -> mentee.getId() == menteeId)
                .findFirst()
                .orElseThrow(() -> new MenteeDoesNotExist("Mentee does not exist"));
    }
}

