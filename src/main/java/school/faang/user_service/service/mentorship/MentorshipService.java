package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.user.UserService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public void deleteMentorForAllHisMentees(long mentorId, List<User> mentees) {
        mentees.forEach(mentee -> {
            mentee.getMentors().removeIf(mentor -> mentor.getId() == mentorId);
            mentee.getGoals().stream()
                    .filter(goal -> goal.getMentor().getId() == mentorId)
                    .forEach(goal -> goal.setMentor(mentee));
        });
        mentorshipRepository.saveAll(mentees);
    }

    public List<UserDto> getMentees(long userId) {
        User user = userService.getUserById(userId);
        if (user.getMentees().isEmpty()) {
            return Collections.emptyList();
        }
        return userMapper.toDto(user.getMentees());
    }

    public List<UserDto> getMentors(long userId) {
        User user = userService.getUserById(userId);
        if (user.getMentors().isEmpty()) {
            return Collections.emptyList();
        }
        return userMapper.toDto(user.getMentors());
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = userService.getUserById(mentorId);
        User mentee = userService.getUserById(menteeId);
        if (mentor.getId() == mentee.getId()) {
            throw new IllegalArgumentException("Incorrect data");
        }
        mentee.getMentors().remove(mentor);
        userRepository.save(mentee);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentor = userService.getUserById(mentorId);
        User mentee = userService.getUserById(menteeId);
        if (mentor.getId() == mentee.getId()) {
            throw new IllegalArgumentException("Incorrect data");
        }
        mentee.getMentors().remove(mentee);
        userRepository.save(mentor);
    }
}

