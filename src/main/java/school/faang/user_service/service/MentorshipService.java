package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.MentorshipServiceValidator;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MentorshipService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        MentorshipServiceValidator.testValidUserId(userId, userRepository);

        User user = getUser(userId);
        List<User> mentees = user.getMentees();
        MentorshipServiceValidator.testEmptyValue(mentees);

        return mentees.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserDto> getMentors(long userId) {
        MentorshipServiceValidator.testValidUserId(userId, userRepository);

        User user = getUser(userId);
        List<User> mentors = user.getMentors();

        MentorshipServiceValidator.testEmptyValue(mentors);
        return mentors.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        MentorshipServiceValidator.testValidUserId(menteeId, userRepository);
        MentorshipServiceValidator.testValidUserId(mentorId, userRepository);

        User mentor = getUser(mentorId);
        MentorshipServiceValidator.testEmptyValue(mentor.getMentees());
        mentor.getMentees().removeIf(user -> user.getId() == menteeId);
        userRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        MentorshipServiceValidator.testValidUserId(menteeId, userRepository);
        MentorshipServiceValidator.testValidUserId(mentorId, userRepository);


        User mentee = getUser(menteeId);
        MentorshipServiceValidator.testEmptyValue(mentee.getMentors());
        mentee.getMentors().removeIf(user -> user.getId() == mentorId);
        userRepository.save(mentee);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Такого пользователя не существует"));
    }
}
