package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        User user = validateId(userId);

        if (user.getMentees().isEmpty()) {
            return new ArrayList<>();
        }
        return user.getMentees().stream().map(userMapper::userToUserDto).toList();
    }

    public List<UserDto> getMentors(long userId) {
        User user = validateId(userId);

        if (user.getMentors().isEmpty()) {
            return new ArrayList<>();
        }
        return user.getMentors().stream().map(userMapper::userToUserDto).toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = validateId(mentorId);
        User mentee = validateId(menteeId);

        if (mentor.getMentees().contains(mentee)) {
            mentor.getMentees().remove(mentee);
        }
        mentorshipRepository.save(mentor);
    }

    private User validateId(long userId) {
        if (userId < 1) {
            throw new IllegalArgumentException("Некоректный ввод данных id");
        }
        User user = mentorshipRepository.findById(userId).orElseGet(() -> {
            throw new IllegalArgumentException("Пользователя с таким id не существует");
        });
        return user;
    }
}
