package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(Long userId) {
        User mentor = findUserById(userId);
        return Optional.ofNullable(mentor.getMentees())
                .orElse(Collections.emptyList())
                .stream()
                .map(userMapper::userToUserDto)
                .toList();
    }

    public List<UserDto> getMentors(Long userId) {
        User mentee = findUserById(userId);
        return Optional.ofNullable(mentee.getMentors())
                .orElse(Collections.emptyList())
                .stream()
                .map(userMapper::userToUserDto)
                .toList();
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        User mentor = findUserById(mentorId);
        User mentee = mentor.getMentees()
                .stream()
                .filter(m -> m.getId() == menteeId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("You do not have the mentee with id: " + menteeId + "among your mentees"));

        userRepository.delete(mentee);
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        User mentee = findUserById(menteeId);
        User mentor = mentee.getMentors()
                .stream()
                .filter(m -> m.getId() == mentorId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("You do not have the mentor with id: " + mentorId + "among your mentors"));

        userRepository.delete(mentor);

    }
    private User findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElseThrow(() -> new EntityNotFoundException("User with " + userId + " id is not found"));
    }
}
