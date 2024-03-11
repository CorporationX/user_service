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
        //check if Repository has MentorId otherwise throw custom exception
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION));

        //check if mentor has such mentee in his list by id, otherwise throw exception
        User mentee = mentor.getMentees().stream()
                .filter(user -> user.getId() == menteeId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION));

        //delete this mentee
        mentor.getMentees().remove(mentee);

        //update the mentor in repository (because I have updated the list of this user (mentor))
        userRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        //check if Repository has menteeId otherwise throw custom exception

        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION));

        //check if mentee has such mentor in his list by id, otherwise throw exception
        User mentor = mentee.getMentees().stream()
                .filter(user -> user.getId() == mentorId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION));

        //delete this mentor
        mentee.getMentors().remove(mentor);

        //update the mentor in repository (because I have updated the list of this user (mentor))
        userRepository.save(mentee);
    }

}