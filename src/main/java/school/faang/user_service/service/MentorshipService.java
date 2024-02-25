package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
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
        User user = userService.findById(userId);
        List<User> mentees = user.getMentees();
        return userMapper.toDto(mentees);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getMentors(Long userId) {
        User user = userService.findById(userId);
        List<User> mentors = user.getMentors();
        return userMapper.toDto(mentors);
    }

    @Transactional
    public void removeMenteeOfMentor(Long mentorId, Long menteeId) {
        User mentor = userService.findById(mentorId);
        User mentee = userService.findById(menteeId);
        mentor.getMentees().remove(mentee);
    }

    @Transactional
    public void removeMentorOfMentee(Long mentorId, Long menteeId) {
        User mentor = userService.findById(mentorId);
        User mentee = userService.findById(menteeId);
        mentee.getMentors().remove(mentor);
    }
}
