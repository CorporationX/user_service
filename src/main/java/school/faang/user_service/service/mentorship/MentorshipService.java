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
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        return getUsers(userId, User::getMentees);
    }

    public List<UserDto> getMentors(long userId) {
        return getUsers(userId, User::getMentors);
    }

    private List<UserDto> getUsers(long userId, Function<User, List<User>> userFunction) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found."));
        List<User> users = userFunction.apply(user);
        return userMapper.toDto(users);
    }

    @Transactional
    public void deleteMentee(long menteeId, long mentorId) {
        String message = "Mentee not found.";
        deleteUserRelation(mentorId, menteeId, message);
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        String message = "Mentor not found.";
        deleteUserRelation(menteeId, mentorId, message);
    }

    private static void checkInList(List<UserDto> userDtos, long userId, String message) {
        if (userDtos.stream()
                .filter(userDto -> userDto.getId().equals(userId))
                .findFirst().isEmpty()) {
            throw new UserNotFoundException(message);
        }
    }

    public void deleteUserRelation(long userId, long relatedUserId, String message) {
        List<UserDto> userDto = getMentees(userId);
        checkInList(userDto, relatedUserId, message);
        User user = userRepository.findById(userId).get();
        User relatedUser = userRepository.findById(relatedUserId).get();
        user.getMentees().remove(relatedUser);
    }

}
