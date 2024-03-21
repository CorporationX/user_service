package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.mentorship.MentorshipValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final MentorshipValidator mentorshipValidator;

    public List<UserDto> getMentees(long userId) {
        User user = userService.getUserById(userId);
        return userMapper.toDto(user.getMentees());
    }

    public List<UserDto> getMentors(long userId) {
        User user = userService.getUserById(userId);
        return userMapper.toDto(user.getMentors());
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = userService.getUserById(mentorId);
        User mentee = userService.getUserById(menteeId);

        mentorshipValidator.validateMentorMenteeIds(menteeId, mentorId);
        mentor.getMentees().remove(mentee);
        userRepository.save(mentor);
    }

    public void deleteMentor(long mentorId, long menteeId) {
        User mentor = userService.getUserById(mentorId);
        User mentee = userService.getUserById(menteeId);

        mentorshipValidator.validateMentorMenteeIds(menteeId, mentorId);
        mentee.getMentors().remove(mentor);
        userRepository.save(mentee);
    }

    public void deleteMentorForAllHisMentees(long mentorId, List<User> mentees) {
        mentees.forEach(mentee -> {
            mentee.getMentors().removeIf(mentor -> mentor.getId() == mentorId);
            mentee.getGoals().stream()
                    .filter(goal -> goal.getMentor().getId() == mentorId)
                    .forEach(goal -> goal.setMentor(mentee));
        });
        mentorshipRepository.saveAll(mentees);
    }
}
