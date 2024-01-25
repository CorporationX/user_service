package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserService userService;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getMentees(Long userId) {
        User user = userService.getUserById(userId);
        List<User> mentees = user.getMentees();
        return mentees.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserDto> getMentors(Long userId) {
        User user = userService.getUserById(userId);
        List<User> mentors = user.getMentors();
        return mentors.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public void removeMenteeOfMentor(Long mentorId, Long menteeId) {
        List<User> users = getMentorAndMentee(mentorId, menteeId);
        users.get(0).getMentees().remove(users.get(1));
    }

    @Transactional
    public void removeMentorOfMentee(Long mentorId, Long menteeId) {
        List<User> users = getMentorAndMentee(mentorId, menteeId);
        users.get(1).getMentors().remove(users.get(0));
    }

    private List<User> getMentorAndMentee(Long mentorId, Long menteeId) {
        User mentor = userService.getUserById(mentorId);
        User mentee = userService.getUserById(menteeId);
        return List.of(mentor, mentee);

    }
}
