package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserService userService;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(Long userId) {
        User user = userService.getUserById(userId);
        List<User> mentees = user.getMentees();
        return mentees.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserDto> getMentors(Long userId) {
        User user = userService.getUserById(userId);
        List<User> mentors = user.getMentors();
        return mentors.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public void removeMentorsMentee(Long mentorId, Long menteeId) {
        User mentor = userService.getUserById(mentorId);
        User mentee = userService.getUserById(menteeId);
        int index = mentor.getMentees().indexOf(mentee);
        mentor.getMentees().remove(index);
    }

    public void removeMentorOfMentee(Long mentorId, Long menteeId) {
        User mentor = userService.getUserById(mentorId);
        User mentee = userService.getUserById(menteeId);
        int index = mentee.getMentors().indexOf(mentor);
        mentee.getMentors().remove(index);
    }
}
