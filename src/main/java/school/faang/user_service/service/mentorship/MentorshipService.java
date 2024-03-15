package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;

import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.userDto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public List<UserDto> getMentees(long userId) {
        User user = userService.findUserById(userId);
        if (user.getMentees().isEmpty()) {
            return Collections.emptyList();
        }
        return userMapper.toUserDtoList(user.getMentees());
    }

    public List<UserDto> getMentors(long userId) {
        User user = userService.findUserById(userId);
        if (user.getMentors().isEmpty()) {
            return Collections.emptyList();
        }
        return userMapper.toUserDtoList(user.getMentors());
    }
    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = userService.findUserById(mentorId);
        User mentee = userService.findUserById(menteeId);
        if (mentor.getId() == mentee.getId()) {
            throw new IllegalArgumentException("Incorrect data");
        }
        mentee.getMentors().remove(mentor);
        userRepository.save(mentee);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentor = userService.findUserById(mentorId);
        User mentee = userService.findUserById(menteeId);
        if (mentor.getId() == mentee.getId()) {
            throw new IllegalArgumentException("Incorrect data");
        }
        mentee.getMentors().remove(mentee);
        userRepository.save(mentor);
    }
}

