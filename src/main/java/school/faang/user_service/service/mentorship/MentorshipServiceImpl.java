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
        return user.getMentees().stream()
                .toList();
    }

    @Override
    public Collection<User> getMentors(@NonNull Long userId) {
        var user = findById(userId);
        return user.getMentors().stream()
                .toList();
    }

    @Transactional
    public void deleteMentee(@NotNull Long menteeId, @NotNull Long mentorId) {
        var mentor = findById(mentorId);
        var mentee = mentor.getMentees().stream()
                .filter(u -> u.getId().equals(menteeId))
                .findFirst()
                .orElseThrow(
                        () -> new UserNotFoundException(
                                String.format(
                                        "Mentee with id = [%d] is not a mentee of mentor with id = [%d]",
                                        menteeId,
                                        mentorId
                                )
                        )
                );
        mentorshipRepository.delete(mentee.getId(), mentorId);
    }

    @Transactional
    public void deleteMentor(@NonNull Long menteeId, @NonNull Long mentorId) {
        var mentee = findById(menteeId);
        var mentor = mentee.getMentors().stream()
                .filter(u -> u.getId().equals(mentorId))
                .findFirst()
                .orElseThrow(
                        () -> new UserNotFoundException(
                                String.format(
                                        "Mentor with id = [%d] is not a mentor of mentee with id = [%d]",
                                        mentorId,
                                        menteeId
                                )
                        )
                );
        mentorshipRepository.delete(menteeId, mentor.getId());
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new UserNotFoundException("User with id = [" + userId + "] not found")
                );
    }
}
