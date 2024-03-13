package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.userDto.UserDto;
import school.faang.user_service.userMapper.UserMapper;
import school.faang.user_service.userService.UserService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        User user = userService.findUserById(userId);
        if (user.getMentees().isEmpty()) {
            return Collections.emptyList();
        }
        return user.getMentees().stream().map(userMapper::toUserDto).toList();
    }

    public List<UserDto> getMentors(long userId) {
        User user = userService.findUserById(userId);
        if (user.getMentors().isEmpty()) {
            return Collections.emptyList();
        }
        return user.getMentees().stream().map(userMapper::toUserDto).toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = userService.findMentorById(mentorId);
        mentor.getMentees().remove(menteeId);
        userRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = userService.findMenteeById(menteeId);
        mentee.getMentors().remove(mentorId);
        userRepository.save(mentee);
    }
}

