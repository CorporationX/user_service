package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    private User findUserById(Long userId) {
        Optional<User> user = mentorshipRepository.findById(userId);
        return user.orElseThrow(() -> new EntityNotFoundException(String.format("User with ID %d%n is not found", userId)));
    }

    public List<UserDto> getMentees(Long userId) {
        User mentor = findUserById(userId);

        return Optional.ofNullable(mentor.getMentees())
                .orElse(Collections.emptyList())
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserDto> getMentors(Long userId) {
        User mentee = findUserById(userId);

        return Optional.ofNullable(mentee.getMentors())
                .orElse(Collections.emptyList())
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        User mentor = findUserById(mentorId);

        User mentee = Optional.ofNullable(mentor.getMentees())
                .orElse(Collections.emptyList())
                .stream()
                .filter(current -> Objects.equals(current.getId(), menteeId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("There is no mentee with ID %d%n among your mentee.", menteeId))
                );

        mentorshipRepository.delete(mentee);
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        User mentee = findUserById(menteeId);

        User mentor = Optional.ofNullable(mentee.getMentors())
                .orElse(Collections.emptyList())
                .stream()
                .filter(current -> Objects.equals(current.getId(), mentorId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("You do not have the mentor with ID: %d%n.", menteeId))
                );

        mentorshipRepository.delete(mentor);
    }
}
