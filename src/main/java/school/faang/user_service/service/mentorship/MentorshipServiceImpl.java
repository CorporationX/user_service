package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipServiceImpl implements MentorshipService {
    MentorshipRepository mentorshipRepository;
    UserMapper userMapper;

    @Override
    public void addMentorship(long mentorId, long menteeId) {
        validateUserIsNotSelfMentorOrMentee(mentorId, menteeId);
        validateMentorshipExist(mentorId, menteeId);

        User mentee = mentorshipRepository.getByIdOrThrow(menteeId);
        User mentor = mentorshipRepository.getByIdOrThrow(mentorId);

        log.info("Добавляем ментора {} для менти {}", mentorId, menteeId);
        mentee.getMentors().add(mentor);
        mentor.getMentees().add(mentee);
        mentorshipRepository.save(mentee);
    }

    @Override
    public void deleteMentorship(long menteeId, long mentorId) {
        validateUserIsNotSelfMentorOrMentee(mentorId, menteeId);
        validateMentorshipNotExist(mentorId, menteeId);

        User mentee = mentorshipRepository.getByIdOrThrow(menteeId);
        User mentor = mentorshipRepository.getByIdOrThrow(mentorId);

        log.info("Удаляем ментора {} у менти {}", mentorId, menteeId);
        mentee.getMentors().remove(mentor);
        mentor.getMentees().remove(mentee);
        mentorshipRepository.save(mentee);
    }

    @Override
    public List<UserDto> getMentees(long userId) {
        User mentor = mentorshipRepository.getByIdOrThrow(userId);

        if (mentor.getMentees().isEmpty()) {
            return new ArrayList<>();
        }

        return mentor.getMentees().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public List<UserDto> getMentors(long userId) {
        User mentee = mentorshipRepository.getByIdOrThrow(userId);

        if (mentee.getMentees().isEmpty()) {
            return new ArrayList<>();
        }

        return mentee.getMentors().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    private void validateMentorshipExist(long mentorId, long menteeId) {
        if (mentorshipRepository.getByIdOrThrow(mentorId)
                .getMentees()
                .contains(mentorshipRepository.getByIdOrThrow(menteeId))) {
            throw new DataValidationException("Такая связь уже существует");
        }
    }

    private void validateMentorshipNotExist(long mentorId, long menteeId) {
        if (!mentorshipRepository.getByIdOrThrow(mentorId)
                .getMentees()
                .contains(mentorshipRepository.getByIdOrThrow(menteeId))) {
            throw new DataValidationException("Такой связи нет");
        }
    }

    private void validateUserIsNotSelfMentorOrMentee(long mentorId, long menteeId) {
        if (mentorId == menteeId) {
            throw new DataValidationException("Пользователь не может быть ментором/менти для самого себя");
        }
    }
}