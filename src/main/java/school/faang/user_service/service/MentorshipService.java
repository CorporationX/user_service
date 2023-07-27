package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.IncorrectIdException;
import school.faang.user_service.exception.NoUserInDataBaseException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        return validateId(userId).getMentees()
                .stream().map(userMapper::toDto).toList();
    }

    public List<UserDto> getMentors(long userId) {
        return validateId(userId).getMentors()
                .stream().map(userMapper::toDto).toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = validateId(mentorId);
        User mentee = validateId(menteeId);

        removeUserFromList(mentor.getMentees(), mentee);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentor = validateId(mentorId);
        User mentee = validateId(menteeId);

        removeUserFromList(mentee.getMentors(), mentor);
    }

    private User validateId(long userId) {
        if (userId < 1) {
            throw new IncorrectIdException("Некоректный ввод данных id");
        }
        return mentorshipRepository.findById(userId).orElseThrow(() -> {
            throw new NoUserInDataBaseException("Пользователя с таким id не существует");
        });
    }

    private void removeUserFromList(List<User> list, User deletedUser) {
        if (list.contains(deletedUser)) {
            list.remove(deletedUser);
        }
    }
}