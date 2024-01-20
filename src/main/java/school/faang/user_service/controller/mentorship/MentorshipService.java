package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        return getUser(userId).getMentees().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    public List<UserDto> getMentors(long userId) {
        return getUser(userId).getMentors().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentee = getUser(menteeId);
        User mentor = getUser(mentorId);
        //if (mentor.getMentees().contains(mentee))
            mentor.getMentees().remove(mentee);
        userRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = getUser(menteeId);
        User mentor = getUser(mentorId);
       // if (mentee.getMentors().contains(mentor))
            mentee.getMentors().remove(mentor);
        userRepository.save(mentee);
    }

    public User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
    }
}
