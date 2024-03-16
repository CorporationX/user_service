package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.exceptions.UserNotFoundException;
import school.faang.user_service.service.exceptions.messageerror.MessageError;

import java.util.List;

@Service
@RequiredArgsConstructor//or I can use @AutoWired and constructor with required fields instead
public class MentorshipService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public List<UserDto> getMentees(long userId) {
        User mentor = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION));
        List<User> mentees = mentor.getMentees();
        return userMapper.toDto(mentees);
    }


    public List<UserDto> getMentors(long userId) {
        List<User> mentors = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION)).getMentors();
        return userMapper.toDto(mentors);
    }

    public void deleteMentee(long mentorId, long menteeId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION));

        User mentee = mentor.getMentees().stream()
                .filter(user -> user.getId() == menteeId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION));

        mentor.getMentees().remove(mentee);

        userRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {

        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION));

        User mentor = mentee.getMentees().stream()
                .filter(user -> user.getId() == mentorId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION));

        mentee.getMentors().remove(mentor);

        userRepository.save(mentee);
    }

}