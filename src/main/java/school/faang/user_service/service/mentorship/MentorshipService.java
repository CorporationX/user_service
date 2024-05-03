package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        List<User> mentees;
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found."));
        mentees = user.getMentees();
        return userMapper.toDto(mentees);
    }

    public List<UserDto> getMentors(long userId) {
        List<User> mentors;
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found."));
        mentors = user.getMentors();
        return userMapper.toDto(mentors);
    }

    @Transactional
    public void deleteMentee(long menteeId, long mentorId) {
        List<UserDto> menteesDto = getMentees(mentorId);
        checkInList(menteesDto, menteeId, "Mentee not found.");
        User mentee = userRepository.findById(menteeId).get();
        User mentor = userRepository.findById(mentorId).get();
        mentor.getMentees().remove(mentee);
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        List<UserDto> mentorsDto = getMentors(menteeId);
        checkInList(mentorsDto, mentorId, "Mentor not found.");
        User mentee = userRepository.findById(menteeId).get();
        User mentor = userRepository.findById(mentorId).get();
        mentee.getMentors().remove(mentor);
    }

    private static void checkInList(List<UserDto> userDtos, long userId, String message) {
        if (userDtos.stream()
                .filter(userDto -> userDto.getId().equals(userId))
                .findFirst().isEmpty()) {
            throw new UserNotFoundException(message);
        }
    }

}
