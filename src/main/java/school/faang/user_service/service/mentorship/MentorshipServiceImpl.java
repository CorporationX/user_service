package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.MentorshipService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class MentorshipServiceImpl implements MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getMentees(Long userId) {
        User user = findUserById(userId);

        return Optional.ofNullable(user.getMentees())
                .orElse(Collections.emptyList())
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public List<UserDto> getMentors(Long userId) {
        User user = findUserById(userId);

        return Optional.ofNullable(user.getMentors())
                .orElse(Collections.emptyList())
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public void deleteMentee(Long menteeId, Long mentorId) {
        User user = findUserById(mentorId);

        User mentee = Optional.ofNullable(user.getMentees())
                .orElse(Collections.emptyList())
                .stream()
                .filter(current -> Objects.equals(current.getId(), menteeId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("There is no mentee with ID %d%n among your mentee.", menteeId))
                );

        userRepository.delete(mentee);
    }

    @Override
    public void deleteMentor(Long menteeId, Long mentorId) {
        User user = findUserById(menteeId);

        User mentor = Optional.ofNullable(user.getMentors())
                .orElse(Collections.emptyList())
                .stream()
                .filter(current -> Objects.equals(current.getId(), mentorId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("You do not have the mentor with ID: %d%n.", menteeId))
                );

        userRepository.delete(mentor);
    }

    private User findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElseThrow(() -> new EntityNotFoundException(String.format("User with ID %d%n is not found", userId)));
    }

    public void stopMentorship(@NonNull Long id) {
        log.info("Stop mentorship with id {}", id);
        mentorshipRepository.deleteByMentorId(id);
        goalRepository.updateMentorIdByMentorId(id, null);
        log.info("Mentorship stopped with id {}", id);
    }
}
