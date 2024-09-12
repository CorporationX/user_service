package school.faang.user_service.service.mentorship;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class MentorshipServiceImpl implements MentorshipService {
    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;

    @Override
    public Collection<User> getMentees(@NonNull Long userId) {
        var user = findById(userId);
        return user.getMentees();
    }

    @Override
    public Collection<User> getMentors(@NonNull Long userId) {
        var user = findById(userId);
        return user.getMentors();
    }

    @Transactional
    public void deleteMentee(@NotNull Long menteeId, @NotNull Long mentorId) {
        var mentor = findById(mentorId);
        var mentee = checkMentorOrMentee(mentor.getMentees(), mentorId, menteeId, "mentee");
        mentorshipRepository.delete(mentee.getId(), mentorId);
    }

    @Transactional
    public void deleteMentor(@NonNull Long menteeId, @NonNull Long mentorId) {
        var mentee = findById(menteeId);
        var mentor = checkMentorOrMentee(mentee.getMentors(), menteeId, mentorId, "mentor");
        mentorshipRepository.delete(menteeId, mentor.getId());
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new UserNotFoundException("User with id = [" + userId + "] not found")
                );
    }

    private User checkMentorOrMentee(Collection<User> users, Long parentId, Long filteredId, String mentorship) {
        return users.stream()
                .filter(u -> u.getId().equals(filteredId))
                .findFirst()
                .orElseThrow(
                        () -> new UserNotFoundException(
                                String.format(
                                        "User with id = [%d] is not a %s of user with id = [%d].",
                                        filteredId, mentorship, parentId
                                )
                        )
                );
    }
}
