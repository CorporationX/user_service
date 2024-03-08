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
    public List<UserDto> getMentees(Long mentorId) {
        User mentor = getUserById(mentorId);
        List<User> mentees = mentor.getMentees();
        return userMapper.toDto(mentees);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getMentors(Long menteeId) {
        User mentee = getUserById(menteeId);
        List<User> mentors = mentee.getMentors();
        return userMapper.toDto(mentors);
    }

    @Transactional
    public void removeMenteeOfMentor(Long mentorId, Long menteeId) {
        User mentor = getUserById(mentorId);
        User mentee = getUserById(menteeId);
        mentor.getMentees().remove(mentee);
    }

    @Transactional
    public void removeMentorOfMentee(Long mentorId, Long menteeId) {
        User mentor = getUserById(mentorId);
        User mentee = getUserById(menteeId);
        mentee.getMentors().remove(mentor);
    }

    public void stopMentoring(User mentor, User mentee) {
        List<User> mentors = mentee.getMentors();
        mentors.remove(mentor);
        mentee.getReceivedGoalInvitations().stream()
                .filter(goalInvitation -> goalInvitation.getInviter() == mentor)
                .forEach(goalInvitation -> goalInvitation.setInviter(mentee));
    }

    private User getUserById(long userId) {
        return userService.findById(userId);
    }
}
