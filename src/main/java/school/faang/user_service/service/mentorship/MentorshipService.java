package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        log.info("Получение всех менти для пользователя с ID {}", userId);

        User user = getUser(userId);
        List<User> mentees = user.getMentees();
        return mentees.stream()
                .map(this::mapUserToUserDto)
                .toList();
    }

    public List<UserDto> getMentors(long userId) {
        log.info("Получение всех менторов для пользователя с ID {}", userId);

        User user = getUser(userId);
        List<User> mentors = user.getMentors();
        return mentors.stream()
                .map(this::mapUserToUserDto)
                .toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        log.info("Попытка удалить менти {} у ментора {}", menteeId, mentorId);

        User mentor = getUser(mentorId);
        List<User> mentees = mentor.getMentees();

        boolean isRemoved = removeUserFromList(mentees, menteeId);

        if (!isRemoved) {
            log.error("Не удалось найти менти {} у ментора {}", menteeId, mentorId);
            throw new DataValidationException("Менти не найден");
        }
        mentor.setMentees(mentees);
        mentorshipRepository.save(mentor);
        log.info("Менти {} успешно удален у ментора {}", menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) throws DataValidationException {
        log.info("Попытка удалить ментора {} у менти {}", mentorId, menteeId);

        User mentee = getUser(menteeId);
        List<User> mentors = mentee.getMentors();

        boolean isRemoved = removeUserFromList(mentors, mentorId);
        if (!isRemoved) {
            log.error("Не удалось найти ментора {} у менти {}", mentorId, menteeId);
            throw new DataValidationException("Ментор не найден");
        }
        mentee.setMentors(mentors);
        mentorshipRepository.save(mentee);
        log.info("Ментор {} успешно удален у менти {}", mentorId, menteeId);
    }

    private UserDto mapUserToUserDto(User user) {
        List<Long> menteesIds = user.getMentees().stream()
                .map(User::getId)
                .toList();
        List<Long> mentorsIds = user.getMentors().stream()
                .map(User::getId)
                .toList();
        UserDto userDto = userMapper.toDto(user);
        userDto.setMenteesIds(menteesIds);
        userDto.setMentorsIds(mentorsIds);
        return userDto;
    }

    private User getUser(long userId) {
        return mentorshipRepository.findById(userId).orElseThrow(() -> {
            log.error("Не удалось найти пользователя с ID {}", userId);
            return new DataValidationException("Пользователь не найден");
        });
    }

    private boolean removeUserFromList(List<User> users, long userId) {
        return users.removeIf(user -> user.getId() == userId);
    }
    fail();
}
